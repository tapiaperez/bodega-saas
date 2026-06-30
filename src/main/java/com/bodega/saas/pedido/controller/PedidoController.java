package com.bodega.saas.pedido.controller;

import com.bodega.saas.pedido.model.Pedido;
import com.bodega.saas.pedido.repository.PedidoRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

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

        redirectAttributes.addFlashAttribute("idPedido", pedido.getIdPedido());
        redirectAttributes.addFlashAttribute("nombreContacto", nombre);
        redirectAttributes.addFlashAttribute("telefonoContacto", telefono);
        redirectAttributes.addFlashAttribute("tipoEntrega", "DELIVERY");
        redirectAttributes.addFlashAttribute("direccionEnvio", direccion);
        redirectAttributes.addFlashAttribute("totalPedido", pedido.getTotal());

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
