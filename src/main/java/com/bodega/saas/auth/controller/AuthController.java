package com.bodega.saas.auth.controller;

import com.bodega.saas.usuario.model.Usuario;
import com.bodega.saas.usuario.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // MOSTRAR LOGIN
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // PROCESAR LOGIN
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session) {

        Usuario user = usuarioRepository.findByEmail(email).orElse(null);

        if (user != null && user.getPasswordHash().equals(password)) {

            session.setAttribute("usuario", user);

            return "redirect:/dashboard";
        }

        return "redirect:/login?error";
    }
}