package com.alzz26.payments_ledger_service.web;   // must match the folder path (hint: same as the main class + ".web")

import org.springframework.web.bind.annotation.GetMapping;   // annotation for "GET handler"
import org.springframework.web.bind.annotation.RequestMapping;   // annotation for the URL prefix
import org.springframework.web.bind.annotation.RestController;   // annotation for "this is a REST controller"

import java.util.Map;

@RestController                     // = @Controller + @ResponseBody: return values are written as JSON, not looked up as HTML view names
@RequestMapping("/api")    // prefix shared by every endpoint in this class (we want /api/...)
public class PingController {

    @GetMapping("/ping")         // handles GET /api/ping
    public Map<String, String> ping() {
        return Map.of("status", "success");   // hint: immutable map factory added in Java 9, takes key, value
    }
}