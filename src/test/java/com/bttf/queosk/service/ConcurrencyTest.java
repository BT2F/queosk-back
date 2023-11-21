package com.bttf.queosk.service;

import com.bttf.queosk.dto.QueueCreationRequestForm;
import com.bttf.queosk.entity.Queue;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.repository.QueueRedisRepository;
import com.bttf.queosk.repository.QueueRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

@DisplayName("동시성 테스트")
public class ConcurrencyTest {

    private QueueService queueService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private FcmService fcmService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private QueueRedisRepository queueRedisRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        queueService = new QueueService(
                userRepository,
                restaurantRepository,
                queueRedisRepository,
                queueRepository,
                fcmService
        );
    }

    @Test
    @DisplayName("동시에 큐 생성")
    public void testConcurrentCreateQueue() throws InterruptedException {
        //given
        int numThreads = 5; // 동시에 실행할 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        CountDownLatch countDownLatch = new CountDownLatch(numThreads);

        QueueCreationRequestForm queueCreationRequestForm = QueueCreationRequestForm.builder()
                .numberOfParty(1L)
                .build();

        Long userId = 1L;
        Long restaurantId = 1L;

        // 모의 객체 설정
        Restaurant mockRestaurant = Restaurant.builder().id(1L).build();
        Queue mockQueue = Queue.builder().id(1L).userId(1L).restaurantId(1L).build();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(mockRestaurant));
        when(queueRepository.save(any(Queue.class))).thenReturn(mockQueue);
        when(queueRedisRepository.setIfNotExists(anyString(), anyString())).thenReturn(true);

        // 동시성 테스트용 Runnable 정의
        Runnable createQueueTask = () -> {
            try {
                queueService.createQueue(queueCreationRequestForm, userId, restaurantId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        };
        //when(병렬로 스레드 실행)
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(createQueueTask);
        }

        // 모든 스레드가 완료될 때까지 대기
        if (!countDownLatch.await(10, TimeUnit.SECONDS)) {
            Assertions.fail("Timeout");
        }

        //then
        verify(queueRedisRepository,times(5))
                .createQueue("1", "1");
    }

    @Test
    @DisplayName("동시에 큐 당기기")
    public void testConcurrentPopTheFirstTeamOfQueue() throws InterruptedException {
        //given
        int numThreads = 5; // 동시에 실행할 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        AtomicReference<String> poppedQueueId = new AtomicReference<>(null);

        CountDownLatch countDownLatch = new CountDownLatch(numThreads);

        Long restaurantId = 1L;
        when(queueRedisRepository.popTheFirstTeamOfQueue(String.valueOf(1)))
                .thenReturn("1");
        when(queueRepository.findById(1L)).thenReturn(Optional.of(new Queue()));
        when(queueRedisRepository.popTheFirstTeamOfQueue(anyString()))
                .thenAnswer(invocation -> {
                    if (poppedQueueId.get() == null) {
                        poppedQueueId.set("1");
                        return "1";
                    }
                    return null;
                });

        when(queueRepository.findById(1L))
                .thenAnswer(invocation -> {
                    if (poppedQueueId.get() != null) {
                        return Optional.of(new Queue());
                    } else {
                        return Optional.empty();
                    }
                });

        // 동시성 테스트용 Runnable 정의
        Runnable popTheFirstTeamTask = () -> {
            try {
                queueService.popTheFirstTeamOfQueue(restaurantId);
            } catch (Exception e) {
            e.printStackTrace();
        }
        countDownLatch.countDown();
        };

        //when(병렬로 스레드 실행)
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(popTheFirstTeamTask);
        }

        // 모든 스레드가 완료될 때까지 대기 (10초 제한, 필요에 따라 조절)
        if (!countDownLatch.await(10, TimeUnit.SECONDS)) {
            Assertions.fail("Timeout");
        }

        //then
        verify(queueRedisRepository,  times(5))
                .findAll("1");
    }

    @Test
    @DisplayName("동시에 큐 삭제")
    public void testConcurrentDeleteUserQueue() throws InterruptedException {
        int numThreads = 5; // 동시에 실행할 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        CountDownLatch countDownLatch = new CountDownLatch(numThreads);
        Queue mockQueue = Queue.builder().id(1L).userId(1L).restaurantId(1L).build();

        Long userId = 1L;
        Long restaurantId = 1L;

        when(queueRepository.findFirstByUserIdAndRestaurantIdOrderByCreatedAtDesc(userId, restaurantId))
                .thenReturn(Optional.of(mockQueue));
        when(queueRedisRepository.getUserWaitingCount(anyString(), anyString()))
                .thenReturn(1L);
        when(queueRedisRepository.setIfNotExists(anyString(), anyString())).thenReturn(true);

        // 동시성 테스트용 Runnable 정의
        Runnable deleteUserQueueTask = () -> {
            try {
                queueService.deleteUserQueue(restaurantId, userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
        };

        // 병렬로 스레드 실행
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(deleteUserQueueTask);
        }

        // 모든 스레드가 완료될 때까지 대기
        if (!countDownLatch.await(10, TimeUnit.SECONDS)) {
            Assertions.fail("Timeout");
        }

        //then
        verify(queueRedisRepository, times(numThreads))
                .deleteQueue("1","1");
    }
}
