package com.bodega.saas.dashboard;

import com.bodega.saas.usuario.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        Object userObj = session.getAttribute("usuario");

        if (userObj == null) {
            return "redirect:/login";
        }

        Usuario user = (Usuario) userObj;

        model.addAttribute("usuario", user);
        model.addAttribute("idEmpresa", user.getIdEmpresa());

        return "dashboard";
    }
}