package com.bttf.queosk.service.queueservice;

import com.bttf.queosk.dao.FCMTokenDao;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcmService implements MessageService {


    private final FCMTokenDao fcmTokenDao;

    public void sendMessageToWaitingUser(String email) {
        if (!hasKey(email)) {
            return;
        }
        String token = getToken(email);
        Message message = Message.builder()
                .putData("title", "대기 순서 알림")
                .putData("content", "웨이팅 세 팀이 남았습니다. 빨리 오세요")
                .setToken(token)
                .build();

        send(message);
    }

    public void send(Message message) {
        FirebaseMessaging.getInstance().sendAsync(message);
    }

    public void saveToken(String email, String token) {
        fcmTokenDao.saveToken(email, token);
    }

    public void deleteToken(String email) {
        fcmTokenDao.deleteToken(email);
    }

    private boolean hasKey(String email) {
        return fcmTokenDao.hasKey(email);
    }

    private String getToken(String email) {
        return fcmTokenDao.getToken(email);
    }

}