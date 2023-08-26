package com.bttf.queosk.service.queueservice;

import com.bttf.queosk.dto.queuedto.QueueDto;
import com.bttf.queosk.dto.queuedto.QueueForm;
import com.bttf.queosk.entity.Queue;
import com.bttf.queosk.entity.User;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.exception.ErrorCode;
import com.bttf.queosk.mapper.queuemapper.QueueMapper;
import com.bttf.queosk.repository.QueueRedisRepository;
import com.bttf.queosk.repository.QueueRepository;
import com.bttf.queosk.repository.RestaurantRepository;
import com.bttf.queosk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final UserRepository userRepository;

    private final RestaurantRepository restaurantRepository;

    private final QueueRedisRepository queueRedisRepository;

    private final QueueRepository queueRepository;

    private final FcmService fcmService;

    /*
    사용자가 웨이팅 등록
     */
    public Long createQueue(QueueForm.Request queueRequestForm, Long userId,
                            Long restaurantId) {

        userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_USER_ID)
        );

        restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_RESTAURANT)
        );

        QueueDto queueDto = QueueMapper.INSTANCE
                .QueueFormRequestToQueueDto(queueRequestForm);

        Queue queue = queueRepository.save(Queue.of(queueDto, restaurantId, userId));

        return queueRedisRepository.createQueue(
                String.valueOf(restaurantId), String.valueOf(queue.getId()));
    }

    /*
    현재 대기중인 팀들의 수
     */
    public Integer getQueueCountingTeams(Long restaurantId, Long userId) {
        if (restaurantId != userId) {
            throw new CustomException(ErrorCode.NOT_PERMITTED);
        }

        return queueRedisRepository.findAll(String.valueOf(restaurantId)).size();

    }

    /*
        웨이팅중인 팀들의 예약정보
     */
    public List<QueueForm.Response> getQueueTeamListInfo(Long restaurantId, Long userId) {
        if (restaurantId != userId) {
            throw new CustomException(ErrorCode.NOT_PERMITTED);
        }
        Set<?> all = queueRedisRepository.findAll(String.valueOf(restaurantId));

        List<Queue> queueList = new ArrayList<>();
        for (Object s : all) {
            queueList.add(queueRepository.findById(Long.valueOf((String) s)).orElseThrow(
                    () -> new CustomException(ErrorCode.INVALID_WATING)
            ));
        }

        List<QueueDto> collect = queueList.stream().map(QueueDto::of)
                .collect(Collectors.toList());

        List<QueueForm.Response> responses = new ArrayList<>();
        for (QueueDto dto : collect) {
            responses.add(QueueMapper.INSTANCE.QueueDtoToQueueFormResponse(dto));
        }
        return responses;
    }

    /*
       본인(유저)의 순서를 알 수 있다.
     */
    public Long getQueueUserWaitingCount(Long restaurantId, Long userId) {

        return queueRedisRepository
                .getUserWaitingCount(String.valueOf(restaurantId), String.valueOf(userId));
    }

    public void deleteQueueWaitingTeam(Long userId, Long restaurantId) {

        if (restaurantId != userId) {
            throw new CustomException(ErrorCode.NOT_PERMITTED);
        }

        queueRedisRepository.deleteWaitingTeam(String.valueOf(restaurantId));

        Set<?> all = queueRedisRepository.findAll(String.valueOf(restaurantId));

        List<Queue> queueList = new ArrayList<>();

        for (Object s : all) {
            queueList.add(queueRepository.findById(Long.valueOf((String) s)).orElseThrow(
                    () -> new CustomException(ErrorCode.INVALID_WATING)
            ));
        }

        for (int i = 0; i < 3; i++) {
            User user = userRepository.findById(queueList.get(i).getUserId()).orElseThrow(
                    () -> new CustomException(ErrorCode.INVALID_USER_ID)
            );

            fcmService.sendMessageToWaitingUser(user.getEmail());
        }

    }
}
