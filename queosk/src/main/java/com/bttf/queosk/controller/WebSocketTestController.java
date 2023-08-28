package com.bttf.queosk.controller;

import com.bttf.queosk.dto.queuedto.QueueForm;
import com.bttf.queosk.service.queueservice.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class WebSocketTestController {
    private final QueueService queueService;

    // 프론트 연동 전 임시로 타임리프 페이지 구성
    @GetMapping("/queue/restaurant")
    public String waitingPage(Long restaurantId, Model model) {
        model.addAttribute("restaurantId", restaurantId);

        List<QueueForm.Response> queueTeamListInfo =
                queueService.getQueueList(restaurantId);

        model.addAttribute("queueList", queueTeamListInfo);

        model.addAttribute("defaultCount", queueTeamListInfo.size());

        return "restaurant-page";
    }

    // 프론트 연동 전 임시로 타임리프 페이지 구성
    @GetMapping("/queue/user")
    public String userWaitingPage(Long queueId, Long restaurantId, Model model) {
        model.addAttribute("restaurantId", restaurantId);
        model.addAttribute("queueId", queueId);
        Long defaultCount = queueService.getUserQueueNumber(restaurantId, queueId);
        model.addAttribute("defaultCount", defaultCount);
        return "user-queue-page";
    }
}