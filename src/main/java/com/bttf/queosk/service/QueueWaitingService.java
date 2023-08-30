package com.bttf.queosk.service;

import com.bttf.queosk.dto.queuedto.QueueRemaining;
import com.bttf.queosk.exception.CustomException;
import com.bttf.queosk.repository.QueueRedisRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.bttf.queosk.exception.ErrorCode.FAILED_TO_FETCH_QUEUE;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueueWaitingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final QueueRedisRepository queueRedisRepository;

    // 전체 대기자 수 업데이트 (점주 화면용 웹소켓)
    @Transactional
    public void updateWaitingCount(Long restaurantId) {
        List<String> queueList = queueRedisRepository.findAll(String.valueOf(restaurantId));
        int waitingCount = queueList.size();

        QueueRemaining queueRemaining = QueueRemaining.builder()
                .waitingCount(waitingCount)
                .build();

        sendMessageToRestaurant(restaurantId, queueRemaining);
        log.info("총 대기 수 전달 완료 (식당 Id : " + restaurantId + " )");
    }

    // 고객 대기번호 업데이트(유저 대기화면용 웹소켓)
    @Transactional
    public void updateUserIndexes(Long restaurantId) {
        List<String> queueList = queueRedisRepository.findAll(String.valueOf(restaurantId));

        queueList.forEach(queueId -> {
            Integer index = queueList.indexOf(queueId) + 1; // 각 queueId의 인덱스(고객 대기번호) 구하기

            // QueueRemaining 객체 생성
            QueueRemaining queueRemaining = QueueRemaining.builder()
                    .waitingCount(index) // 업데이트된 대기번호 설정
                    .build();

            // 고객에게 업데이트된 대기번호 전달 (웹소켓)
            sendMessageToUser(restaurantId, Long.parseLong(queueId), queueRemaining);
        });

        log.info("고객 대기번호 업데이트완료  (식당 Id : " + restaurantId + " )");
    }

    private void sendMessageToUser(Long restaurantId, Long queueId, QueueRemaining queueRemaining) {
        try {
            String topic = "/topic/restaurant/" + restaurantId + "/queue/" + queueId;
            String queueRemainingJson = new ObjectMapper().writeValueAsString(queueRemaining);
            messagingTemplate.convertAndSend(topic, queueRemainingJson);
        } catch (JsonProcessingException e) {
            throw new CustomException(FAILED_TO_FETCH_QUEUE);
        }
    }

    private void sendMessageToRestaurant(Long restaurantId, QueueRemaining queueRemaining) {
        try {
            String topic = "/topic/restaurant/" + restaurantId;
            String queueRemainingJson = new ObjectMapper().writeValueAsString(queueRemaining);
            messagingTemplate.convertAndSend(topic, queueRemainingJson);
        } catch (JsonProcessingException e) {
            throw new CustomException(FAILED_TO_FETCH_QUEUE);
        }
    }
}