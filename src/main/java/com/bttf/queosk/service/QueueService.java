package com.bttf.queosk.service;

import com.bttf.queosk.dto.*;
import com.bttf.queosk.entity.Queue;
import com.bttf.queosk.entity.Restaurant;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.QueueRedisRepository;
import com.bttf.queosk.repository.QueueRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bttf.queosk.exception.ErrorCode.*;

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
    public void createQueue(QueueCreateForm.Request queueRequestRequest,
                            Long userId,
                            Long restaurantId) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(INVALID_RESTAURANT));

        checkIfUserAlreadyInQueue(userId, restaurantId);

        Queue queue = queueRepository.save(
                Queue.of(queueRequestRequest, restaurant.getId(), userId)
        );

        queueRedisRepository.createQueue(
                String.valueOf(restaurant.getId()),
                String.valueOf(queue.getId())
        );
    }

    //웨이팅 중인 팀들의 예약정보 가져오기
    @Transactional(readOnly = true)
    public QueueListDto getQueueList(Long restaurantId) {

        List<QueueDto> queueDtos = queueRedisRepository.findAll(String.valueOf(restaurantId))
                .stream()
                .map(queueId -> queueRepository.findById(Long.parseLong(queueId)).orElse(null))
                .filter(Objects::nonNull)
                .map(QueueDto::of)
                .collect(Collectors.toList());

        return QueueListDto.of(queueDtos);
    }

    public QueueOfRestaurantDto getQueueOfRestaurant(Long restaurantId) {
        List<String> queues = queueRedisRepository.findAll(String.valueOf(restaurantId));
        return QueueOfRestaurantDto.builder().totalQueue(queues.size()).build();
    }

    // 본인(사용자)의 순서 조회
    @Transactional(readOnly = true)
    public QueueIndexDto getUserQueueNumber(Long restaurantId, Long userId) {

        Queue queue = queueRepository
                .findFirstByUserIdAndRestaurantIdOrderByCreatedAtDesc(userId, restaurantId)
                .orElseThrow(() -> new CustomException(QUEUE_DOESNT_EXIST));

        Long userQueueIndex =
                queueRedisRepository.getUserWaitingCount(
                        String.valueOf(restaurantId),
                        String.valueOf(queue.getId())
                );

        // 사용자의 인덱스가 존재하지 않을 경우 (Queue 등록하지 않은상태) 예외 반환
        if (userQueueIndex == null || userQueueIndex < 0) {
            throw new CustomException(QUEUE_DOESNT_EXIST);
        }

        return QueueIndexDto.of(userQueueIndex);
    }

    // 웨이팅 수를 앞에서 1개 당김.
    @Transactional
    public void popTheFirstTeamOfQueue(Long restaurantId) {
        queueRedisRepository.popTheFirstTeamOfQueue(String.valueOf(restaurantId));

        // 존재하는 queue 일 경우 리스트에 담음, 대기번호 2 번째 보다 작거나 같은 경우 FCM 알림 전송
        queueRedisRepository.findAll(String.valueOf(restaurantId))
                .stream()
                .map(queueId -> queueRepository.findById(Long.parseLong(queueId)).orElse(null))
                .filter(Objects::nonNull)
                .forEach(queue -> {
                    if (queue.getId() <= 2) {
                        sendNotificationToWaitingUser(queue.getUserId());
                    }
                });
    }

    // 사용자가 본인의 웨이팅을 삭제(취소)
    @Transactional
    public void deleteUserQueue(Long restaurantId, Long userId) {

        Queue queue = queueRepository
                .findFirstByUserIdAndRestaurantIdOrderByCreatedAtDesc(userId, restaurantId)
                .orElseThrow(() -> new CustomException(QUEUE_DOESNT_EXIST));

        queueRedisRepository.deleteQueue(
                String.valueOf(restaurantId),
                String.valueOf(queue.getId())
        );
    }

    // 유저가 이미 해당 식당에 웨이팅 등록을 했는지 확인
    private void checkIfUserAlreadyInQueue(Long userId, Long restaurantId) {
        // 가장 최근에 생성된 Queue만 필요하므로, 첫 번째 Queue만 조회
        Optional<Queue> latestQueue =
                queueRepository.findFirstByUserIdAndRestaurantIdOrderByCreatedAtDesc(userId, restaurantId);

        if (latestQueue.isPresent()) {
            Long userWaitingCount =
                    queueRedisRepository.getUserWaitingCount(
                            String.valueOf(restaurantId),
                            String.valueOf(latestQueue.get().getId())
                    );

            if (userWaitingCount != null) {
                throw new CustomException(QUEUE_ALREADY_EXISTS);
            }
        }
    }

    //FCM 메세지 전송
    private void sendNotificationToWaitingUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            fcmService.sendMessageToWaitingUser(user.getEmail());
        });
    }
}
