package com.bttf.queosk.service.queueservice;

import com.bttf.queosk.repository.QueueRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QueueWaitingServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private QueueRedisRepository queueRedisRepository;

    private QueueWaitingService queueWaitingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        queueWaitingService = new QueueWaitingService(messagingTemplate, queueRedisRepository);
    }

    @Test
    public void testUpdateWaitingCount() {
        // given
        List<String> mockQueueList = Collections.nCopies(5, "1");
        when(queueRedisRepository.findAll(anyString())).thenReturn(mockQueueList);

        // when
        queueWaitingService.updateWaitingCount(1L);

        // then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(messagingTemplate).convertAndSend(topicCaptor.capture(), messageCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("/topic/restaurant/1");
        assertThat(messageCaptor.getValue()).isEqualTo("{\"waitingCount\":5}");
    }

    @Test
    public void testUpdateUserIndexes() {
        // given
        List<String> mockQueueList = Collections.nCopies(1, "1");
        when(queueRedisRepository.findAll("1")).thenReturn(mockQueueList);

        // when
        queueWaitingService.updateUserIndexes(1L);

        // then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(messagingTemplate).convertAndSend(topicCaptor.capture(), messageCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("/topic/restaurant/1/queue/1");
        assertThat(messageCaptor.getValue()).isEqualTo("{\"waitingCount\":1}");
    }
}
