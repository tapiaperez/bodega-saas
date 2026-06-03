package com.bodega.saas.pedido.controller;

import com.bodega.saas.pedido.model.Pedido;
import com.bodega.saas.pedido.repository.PedidoRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    // Guardar pedido (formulario público)
    @PostMapping("/pedido/guardar")
    public String guardarPedido(@RequestParam String nombre,
                                @RequestParam String telefono,
                                @RequestParam String direccion,
                                @RequestParam Double total,
                                HttpSession session) {

        Pedido pedido = new Pedido();

        // 🧠 Obtener empresa desde sesión (correcto para SaaS)
        Long idEmpresa = (Long) session.getAttribute("idEmpresa");

        if (idEmpresa == null) {
            idEmpresa = 1L; // fallback para pruebas
        }

        pedido.setIdEmpresa(idEmpresa);
        pedido.setNombreContacto(nombre);
        pedido.setTelefonoContacto(telefono);
        pedido.setDireccionEnvio(direccion);

        pedido.setEstado("PENDIENTE");
        pedido.setTotal(BigDecimal.valueOf(total));

        pedidoRepository.save(pedido);

        return "redirect:/pedido-exitoso";
    }

    //  Vista de éxito (IMPORTANTE: pasar idEmpresa)
    @GetMapping("/pedido-exitoso")
    public String exito(HttpSession session, Model model) {

        Long idEmpresa = (Long) session.getAttribute("idEmpresa");

        if (idEmpresa == null) {
            return "redirect:/";
        }

        model.addAttribute("idEmpresa", idEmpresa);

        return "pedido_exitoso";
    }
}