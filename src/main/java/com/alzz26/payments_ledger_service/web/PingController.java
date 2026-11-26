package com.alzz26.payments_ledger_service.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health-check endpoint under /api: returns a fixed status to show the app is running.
 */
@RestController
@RequestMapping("/api")
public class PingController {

    // GET /api/ping -> 200 OK {"status":"success"}
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "success");
    }
}
