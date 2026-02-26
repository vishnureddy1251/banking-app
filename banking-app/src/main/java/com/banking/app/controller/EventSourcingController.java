package com.banking.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventSourcingController {

    private final EventSourcingService eventSourcingService;

}
