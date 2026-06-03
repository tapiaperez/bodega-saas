package com.bodega.saas.pedido.controller;

import com.bodega.saas.common.ItemCarrito;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class CarritoViewController {

    @GetMapping("/carrito")
    public String verCarrito(HttpSession session, Model model) {

        Object obj = session.getAttribute("carrito");

        List<ItemCarrito> carrito;

        if (obj instanceof List<?>) {
            carrito = (List<ItemCarrito>) obj;
        } else {
            carrito = new ArrayList<>();
        }

        model.addAttribute("carrito", carrito);

        return "carrito";
    }
}