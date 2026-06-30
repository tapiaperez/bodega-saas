package com.bodega.saas.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // MOSTRAR LOGIN
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}
