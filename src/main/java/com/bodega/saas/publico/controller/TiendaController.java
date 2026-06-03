package com.bodega.saas.publico.controller;

import com.bodega.saas.producto.repository.ProductoRepository;
import com.bodega.saas.common.ItemCarrito;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TiendaController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/tienda/{idEmpresa}")
    public String verTienda(@PathVariable Long idEmpresa,
                            Model model,
                            HttpSession session) {

        // Productos por empresa
        model.addAttribute("productos",
                productoRepository.findByIdEmpresa(idEmpresa));

        model.addAttribute("idEmpresa", idEmpresa);

        // CARRITO (contador flotante)
        Object obj = session.getAttribute("carrito");

        int cantidad = 0;

        if (obj instanceof List<?>) {
            List<ItemCarrito> carrito = (List<ItemCarrito>) obj;

            cantidad = carrito.stream()
                    .mapToInt(ItemCarrito::getCantidad)
                    .sum();
        }

        model.addAttribute("cantidadCarrito", cantidad);

        return "tienda";
    }
}