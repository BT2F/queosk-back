package com.bttf.queosk.service.queueservice;

import com.bttf.queosk.dto.queuedto.QueueCreateForm;
import com.bttf.queosk.dto.queuedto.QueueDto;
import com.bttf.queosk.dto.queuedto.QueueForm;
import com.bttf.queosk.entity.Queue;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.mapper.queuemapper.QueueMapper;
import com.bttf.queosk.repository.QueueRedisRepository;
import com.bttf.queosk.repository.QueueRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bttf.queosk.exception.ErrorCode.INVALID_RESTAURANT;
import static com.bttf.queosk.exception.ErrorCode.INVALID_USER_ID;

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
    public Long createQueue(QueueCreateForm queueRequestForm, Long userId,
                            Long restaurantId) {

        userRepository.findById(userId).orElseThrow(
                () -> new CustomException(INVALID_USER_ID)
        );

        restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new CustomException(INVALID_RESTAURANT)
        );

        QueueDto queueDto =
                QueueDto.builder().numberPeople(queueRequestForm.getNumberPeople()).build();

        Queue queue = queueRepository.save(Queue.of(queueDto, restaurantId, userId));

        return queueRedisRepository.createQueue(
                String.valueOf(restaurantId),
                String.valueOf(queue.getId())
        ) + 1; // index + 1 해야 고객의 현재 대기번호.
    }

    //웨이팅중인 팀들의 예약정보 가져오기
    @Transactional(readOnly = true)
    public List<QueueForm.Response> getQueueList(Long restaurantId) {

        List<String> all = queueRedisRepository.findAll(String.valueOf(restaurantId));

        List<Queue> queueList = new ArrayList<>();

        all.forEach(element -> {
            queueRepository
                    .findById(Long.valueOf(element))
                    .ifPresent(queueList::add);
        });

        List<QueueDto> collects =
                queueList.stream().map(QueueDto::of).collect(Collectors.toList());

        List<QueueForm.Response> responses = new ArrayList<>();

        collects.forEach(queueDto -> {
            responses.add(QueueMapper.INSTANCE.QueueDtoToQueueFormResponse(queueDto));
        });

        return responses;
    }

    // 본인(유저)의 순서를 알 수 있다.
    @Transactional(readOnly = true)
    public Long getUserQueueNumber(Long restaurantId, Long userId) {

        return queueRedisRepository.getUserWaitingCount(
                String.valueOf(restaurantId),
                String.valueOf(userId)
        );
    }

    // 웨이팅 수를 앞에서 1개 당김.
    @Transactional
    public List<QueueDto> popTheFirstTeamOfQueue(Long restaurantId) {

        queueRedisRepository.popTheFirstTeamOfQueue(String.valueOf(restaurantId));

        List<String> all = queueRedisRepository.findAll(String.valueOf(restaurantId));

        List<QueueDto> queues = new ArrayList<>();

        // 대기번호 2 번째 보다 작거나 같은 경우
        all.forEach(queueId -> {
            queueRepository.findById(Long.parseLong(queueId)).ifPresent(queue -> {
                if (queue.getId() <= 2) {
                    sendNotificationToWaitingUser(queue.getUserId());
                }
                queues.add(QueueDto.of(queue));
            });
        });

        // 새로운 리스트 반환
        return queues;
    }

    //FCM 메세지 전송
    private void sendNotificationToWaitingUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            fcmService.sendMessageToWaitingUser(user.getEmail());
        });
    }
}
