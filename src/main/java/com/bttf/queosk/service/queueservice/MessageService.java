package com.bttf.queosk.service.queueservice;

public interface MessageService {

    void sendMessageToWaitingUser(String token);
}
