package com.bodega.saas.pedido.controller;

import com.bodega.saas.pedido.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminPedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/admin/pedidos")
    public String listar(Model model) {

        model.addAttribute("pedidos", pedidoRepository.findAll());

        return "admin_pedidos";
    }
}