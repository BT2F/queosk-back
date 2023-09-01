package com.bttf.queosk.service;

import com.bttf.queosk.dto.QueueCreateForm;
import com.bttf.queosk.dto.QueueDto;
import com.bttf.queosk.dto.QueueResponseForRestaurant;
import com.bttf.queosk.dto.QueueResponseForUser;
import com.bttf.queosk.entity.Queue;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.QueueRedisRepository;
import com.bttf.queosk.repository.QueueRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.stream.Collectors;

import static com.bttf.queosk.exception.ErrorCode.INVALID_RESTAURANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class QueueServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private QueueRedisRepository queueRedisRepository;

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private FcmService fcmService;

    private QueueService queueService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        queueService = new QueueService(
                userRepository,
                restaurantRepository,
                queueRedisRepository,
                queueRepository,
                fcmService
        );
    }

    @Test
    @DisplayName("큐등록 테스트 - 성공")
    public void testCreateQueue_success() {
        // given
        User mockUser = new User();
        Restaurant mockRestaurant = new Restaurant();
        Queue mockQueue = new Queue();
        List<Queue> queues = new ArrayList<>();
        queues.add(new Queue(1L, 1L, 1L, 1L));

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        when(restaurantRepository.findById(1L)).thenReturn(java.util.Optional.of(mockRestaurant));
        when(queueRepository.save(any(Queue.class))).thenReturn(mockQueue);
        when(queueRepository.findByUserIdAndRestaurantIdOrderByCreatedAtDesc(1L, 1L))
                .thenReturn(queues);
        when(queueRedisRepository.getUserWaitingCount("1", "1"))
                .thenReturn(null);

        // when
        QueueResponseForUser queueResponseForUser =
                queueService.createQueue(new QueueCreateForm(), 1L, 1L);

        // then
        assertThat(queueResponseForUser.getUserQueueIndex()).isEqualTo(1L);
    }

    @Test
    @DisplayName("큐등록 테스트 - 실패(식당 유효하지않음)")
    public void testCreateQueue_WithInvalidRestaurant() {
        // given
        when(restaurantRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // when and then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> queueService.createQueue(new QueueCreateForm(), 1L, 1L)
        );
        assertThat(exception.getErrorCode()).isEqualTo(INVALID_RESTAURANT);
    }

    @Test
    @DisplayName("큐정보가져오기 테스트 - 성공")
    public void testGetQueueList_success() {
        // given
        String restaurantId = "1";

        List<String> mockQueueIds = Arrays.asList("1", "2", "3");

        List<Queue> mockQueueList = mockQueueIds.stream()
                .map(queueId -> Queue.builder()
                        .id(Long.parseLong(queueId))
                        .build())
                .collect(Collectors.toList());

        List<QueueDto> mockQueueDtos = mockQueueList.stream()
                .map(QueueDto::of)
                .collect(Collectors.toList());

        when(queueRedisRepository.findAll(restaurantId)).thenReturn(mockQueueIds);
        when(queueRepository.findById(any())).thenAnswer(invocation -> {
            Long queueId = invocation.getArgument(0);
            return mockQueueList.stream()
                    .filter(queue -> queue.getId().equals(queueId))
                    .findFirst();
        });

        // when
        QueueResponseForRestaurant queueResponseForRestaurant = queueService.getQueueList(Long.parseLong(restaurantId));

        // then
        assertThat(mockQueueDtos.size())
                .isEqualTo(queueResponseForRestaurant.getQueueDtoList().size());

        assertThat(
                mockQueueDtos
                        .get(0)
                        .getId()
                        .equals(queueResponseForRestaurant.getQueueDtoList().get(0).getId()))
                .isTrue();
    }

    @Test
    @DisplayName("큐정보가져오기 테스트 - 실패(빈리스트)")
    public void testGetQueueList_WithEmptyQueue() {
        // given
        String restaurantId = "1";

        when(queueRedisRepository.findAll(restaurantId))
                .thenReturn(Collections.emptyList());

        // when, then
        assertThat(queueService.getQueueList(Long.parseLong(restaurantId)).getQueueDtoList())
                .isEmpty();
    }

    @Test
    @DisplayName("고객 대기번호가져오기 - 성공")
    public void testGetUserQueueNumber_Success() {
        // given
        Long restaurantId = 1L;
        Long userId = 123L;
        Long expectedUserQueueNumber = 3L;
        List<Queue> queues = new ArrayList<>();
        queues.add(Queue.builder().id(5L).build());

        when(queueRedisRepository.getUserWaitingCount(
                String.valueOf(restaurantId),
                String.valueOf(userId)
        )).thenReturn(expectedUserQueueNumber);
        when(queueRepository.findByUserIdAndRestaurantIdOrderByCreatedAtDesc(userId, restaurantId))
                .thenReturn(queues);
        when(queueRedisRepository.getUserWaitingCount(String.valueOf(restaurantId),"5"))
                .thenReturn(2L);


        // when
        QueueResponseForUser userQueueNumber =
                queueService.getUserQueueNumber(restaurantId, userId);

        // then
        assertThat(userQueueNumber.getUserQueueIndex()).isEqualTo(expectedUserQueueNumber);
    }

    @Test
    @DisplayName("고객 대기번호가져오기 - 실패(등록된 큐 없음)")
    public void testGetUserQueueNumber_NoQueueRegistered() {
        // given
        Long restaurantId = 9L;
        Long userId = 123L;
        List<Queue> queues = new ArrayList<>();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());
        when(queueRedisRepository.getUserWaitingCount(
                String.valueOf(restaurantId),
                "0"
        )).thenReturn(null);
        when(queueRepository.findByUserIdAndRestaurantIdOrderByCreatedAtDesc(userId, restaurantId))
                .thenReturn(queues);

        // when and then
        assertThatThrownBy(() -> queueService.getUserQueueNumber(restaurantId, userId))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("대기열 당기기 테스트 - 성공")
    public void testPopTheFirstTeamOfQueue_success() {
        // given
        Long restaurantId = 1L;
        List<String> mockQueueIds = Arrays.asList("1", "2", "3");
        List<String> mockQueueIdsAfterPop = Arrays.asList("2", "3");
        when(queueRedisRepository.findAll(String.valueOf(restaurantId))).thenReturn(mockQueueIdsAfterPop);

        Queue queue1 = Queue.builder().id(1L).restaurantId(restaurantId).build();
        Queue queue2 = Queue.builder().id(2L).restaurantId(restaurantId).build();
        Queue queue3 = Queue.builder().id(3L).restaurantId(restaurantId).build();
        when(queueRepository.findById(1L)).thenReturn(java.util.Optional.of(queue1));
        when(queueRepository.findById(2L)).thenReturn(java.util.Optional.of(queue2));
        when(queueRepository.findById(3L)).thenReturn(java.util.Optional.of(queue3));

        // when
        QueueResponseForRestaurant queueResponseForRestaurant =
                queueService.popTheFirstTeamOfQueue(restaurantId);

        // then
        assertThat(queueResponseForRestaurant.getQueueDtoList().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("대기열 당기기 테스트 - 실패(빈리스트)")
    public void testPopTheFirstTeamOfQueue_emptyQueue() {
        // given
        Long restaurantId = 1L;
        when(queueRedisRepository.findAll(String.valueOf(restaurantId)))
                .thenReturn(Collections.emptyList());

        // when
        QueueResponseForRestaurant queueResponseForRestaurant =
                queueService.popTheFirstTeamOfQueue(restaurantId);

        // then
        assertThat(queueResponseForRestaurant.getQueueDtoList().isEmpty()).isTrue();
    }
}