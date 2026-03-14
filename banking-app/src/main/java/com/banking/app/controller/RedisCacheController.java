package com.banking.app.controller;

import com.banking.app.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/redis")
@RequiredArgsConstructor
public class RedisCacheController {

    private final RedisCacheService redisCacheService;
}
