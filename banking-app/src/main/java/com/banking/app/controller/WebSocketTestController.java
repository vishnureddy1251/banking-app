package com.banking.app.controller;

import com.banking.app.service.WebSocketNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ws-test")
@RequiredArgsConstructor
public class WebSocketTestController {

    private final WebSocketNotificationService notificationService;
}
