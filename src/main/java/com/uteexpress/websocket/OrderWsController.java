package com.uteexpress.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class OrderWsController {
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String hello(String msg) { return "Hello " + msg; }
}
