package com.bttf.queosk.service;

import com.bttf.queosk.dto.queuedto.QueueCreateForm;
import com.bttf.queosk.dto.queuedto.QueueDto;
import com.bttf.queosk.dto.queuedto.QueueResponseForRestaurant;
import com.bttf.queosk.dto.queuedto.QueueResponseForUser;
import com.bttf.queosk.entity.Queue;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.QueueRedisRepository;
import com.bttf.queosk.repository.QueueRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bttf.queosk.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final QueueRedisRepository queueRedisRepository;
    private final QueueRepository queueRepository;
    private final FcmService fcmService;

    // 사용자가 웨이팅 등록
    @Transactional
    public QueueResponseForUser createQueue(QueueCreateForm queueRequestForm,
                                            Long userId,
                                            Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(INVALID_RESTAURANT));

        checkIfUserAlreadyInQueue(userId, restaurantId);

        Queue queue = Queue.of(queueRequestForm, restaurantId, userId);

        queueRepository.save(queue);

        long userQueueNumber = queueRedisRepository
                .createQueue(String.valueOf(restaurant.getId()), String.valueOf(queue.getId())) + 1;

        return QueueResponseForUser.builder()
                .queueRemaining(userQueueNumber - 1)
                .userQueueIndex(userQueueNumber)
                .build();
    }

    //웨이팅 중인 팀들의 예약정보 가져오기
    @Transactional(readOnly = true)
    public QueueResponseForRestaurant getQueueList(Long restaurantId) {

        List<String> queueIds = queueRedisRepository.findAll(String.valueOf(restaurantId));

        List<Queue> queues = new ArrayList<>();

        queueIds.forEach(queue -> {
            queueRepository
                    .findById(Long.valueOf(queue))
                    .ifPresent(queues::add);
        });

        List<QueueDto> queueDtos =
                queues.stream().map(QueueDto::of).collect(Collectors.toList());

        return QueueResponseForRestaurant.builder()
                .totalQueue(queueDtos.size())
                .queueDtoList(queueDtos)
                .build();
    }

    // 본인(사용자)의 순서 조회
    @Transactional(readOnly = true)
    public QueueResponseForUser getUserQueueNumber(Long restaurantId, Long userId) {

        List<Queue> queues =
                queueRepository.findByUserIdAndRestaurantIdOrderByCreatedAtDesc(userId, restaurantId);

        Long userWaitingCount =
                queueRedisRepository.getUserWaitingCount(
                        String.valueOf(restaurantId),
                        String.valueOf(queues.isEmpty() ? 0 : queues.get(0).getId())
                );

        // 사용자의 인덱스가 존재하지 않을 경우 (Queue 등록하지 않은상태) 예외 반환
        if (userWaitingCount == null || userWaitingCount == -1) {
            throw new CustomException(QUEUE_DOESNT_EXIST);
        }

        return QueueResponseForUser.builder()
                .queueRemaining(userWaitingCount)
                .userQueueIndex(userWaitingCount + 1)
                .build();
    }

    // 웨이팅 수를 앞에서 1개 당김.
    @Transactional
    public QueueResponseForRestaurant popTheFirstTeamOfQueue(Long restaurantId) {

        queueRedisRepository.popTheFirstTeamOfQueue(String.valueOf(restaurantId));

        List<String> queueIds = queueRedisRepository.findAll(String.valueOf(restaurantId));

        List<QueueDto> queueDtos = new ArrayList<>();

        // 존재하는 queue 일 경우 리스트에 담음, 대기번호 2 번째 보다 작거나 같은 경우 FCM 알림 전송
        queueIds.forEach(queueId -> {
            queueRepository
                    .findById(Long.parseLong(queueId))
                    .ifPresent(queue -> {
                        if (queue.getId() <= 2) {
                            sendNotificationToWaitingUser(queue.getUserId());
                        }
                        queueDtos.add(QueueDto.of(queue));
                    });
        });
        return QueueResponseForRestaurant.builder()
                .totalQueue(queueDtos.size())
                .queueDtoList(queueDtos)
                .build();
    }

    //FCM 메세지 전송
    private void sendNotificationToWaitingUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            fcmService.sendMessageToWaitingUser(user.getEmail());
        });
    }

    // 사용자가 본인의 웨이팅을 삭제(취소)
    @Transactional
    public void deleteUserQueue(Long restaurantId, Long userId) {

        List<Queue> queues =
                queueRepository.findByUserIdAndRestaurantIdOrderByCreatedAtDesc(userId, restaurantId);

        if (queues.isEmpty()) {
            throw new CustomException(FAILED_TO_FETCH_QUEUE);
        }

        queueRedisRepository.deleteQueue(
                String.valueOf(restaurantId),
                String.valueOf(queues.get(0).getId())
        );
    }

    private void checkIfUserAlreadyInQueue(Long userId, Long restaurantId) {
        List<Queue> queues =
                queueRepository.findByUserIdAndRestaurantIdOrderByCreatedAtDesc(userId, restaurantId);

        Long userWaitingCount =
                queueRedisRepository.getUserWaitingCount(
                        String.valueOf(restaurantId),
                        String.valueOf(queues.isEmpty() ? 0 : queues.get(0).getId())
                );

        if (userWaitingCount != null && userWaitingCount != -1) {
            throw new CustomException(QUEUE_ALREADY_EXISTS);
        }
    }
}
