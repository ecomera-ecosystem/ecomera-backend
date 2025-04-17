package com.moushtario.ecomera.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Youssef
 * @version 1.0
 * @created 16/04/2025
 */

@RestController
@RequestMapping("/api/v1/demo-controller")
public class DemoController {

    @GetMapping
    public ResponseEntity<String> sayHellow() {
        return ResponseEntity.ok("Hello from secured endpoint");
    }
}
