package com.bodega.saas.venta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bodega.saas.producto.repository.ProductoRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import com.bodega.saas.producto.model.Producto;
import com.bodega.saas.venta.dto.VentaItem;
import com.bodega.saas.venta.service.VentaService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bodega.saas.venta.service.VentaService;

@Controller
public class VentaController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VentaService ventaService;

    @GetMapping("/admin/ventas")
    public String ventas(Model model,
                        HttpSession session) {

        model.addAttribute(
                "productos",
                productoRepository.findAll());

        List<VentaItem> venta =
                (List<VentaItem>) session.getAttribute("venta");

        if (venta == null) {

            venta = new ArrayList<>();

        }

        BigDecimal total = BigDecimal.ZERO;

        for (VentaItem item : venta) {

            total = total.add(item.getSubtotal());

        }

        model.addAttribute("venta", venta);

        model.addAttribute("total", total);

        return "admin_ventas";
    }



    @PostMapping("/admin/ventas/agregar")
    public String agregarProductoVenta(

            @RequestParam Long idProducto,
            @RequestParam Integer cantidad,
            HttpSession session) {

        Producto producto = productoRepository
                .findById(idProducto)
                .orElse(null);

        if (producto == null) {

            return "redirect:/admin/ventas";

        }

        List<VentaItem> venta =
                (List<VentaItem>) session.getAttribute("venta");

        if (venta == null) {

            venta = new ArrayList<>();

        }

        // Verificar si el producto ya existe en la venta
        boolean encontrado = false;

        for (VentaItem item : venta) {

            if (item.getIdProducto().equals(idProducto)) {

                item.setCantidad(item.getCantidad() + cantidad);

                item.setSubtotal(
                        item.getPrecio().multiply(
                                BigDecimal.valueOf(item.getCantidad())));

                encontrado = true;

                break;

            }

        }

        // Si no existe, se agrega como nuevo
        if (!encontrado) {

            VentaItem item = new VentaItem();

            item.setIdProducto(producto.getIdProducto());

            item.setNombre(producto.getNombre());

            item.setPrecio(producto.getPrecioVenta());

            item.setCantidad(cantidad);

            item.setSubtotal(
                    producto.getPrecioVenta()
                            .multiply(BigDecimal.valueOf(cantidad)));

            venta.add(item);

        }

        session.setAttribute("venta", venta);

        return "redirect:/admin/ventas";

    }

        @PostMapping("/admin/ventas/registrar")
        public String registrarVenta(HttpSession session,
                                    RedirectAttributes redirectAttributes) {

            List<VentaItem> venta =
                    (List<VentaItem>) session.getAttribute("venta");

            if (venta == null || venta.isEmpty()) {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "No existen productos para registrar.");

                return "redirect:/admin/ventas";

            }

            try {

                // Por ahora usamos valores fijos igual que en Pedidos
                ventaService.generarVentaPOS(
                        1L,   // idEmpresa
                        1L,   // idUsuario
                        venta);

                // Limpiar la venta temporal
                session.removeAttribute("venta");

                redirectAttributes.addFlashAttribute(
                        "success",
                        "Venta registrada correctamente.");

            } catch (RuntimeException ex) {

                redirectAttributes.addFlashAttribute(
                        "error",
                        ex.getMessage());

            }

            return "redirect:/admin/ventas";

        }

        @PostMapping("/admin/ventas/eliminar")
            public String eliminarProductoVenta(

                    @RequestParam Long idProducto,
                    HttpSession session) {

                List<VentaItem> venta =
                        (List<VentaItem>) session.getAttribute("venta");

                if (venta != null) {

                    for (int i = 0; i < venta.size(); i++) {

                        VentaItem item = venta.get(i);

                        if (item.getIdProducto().equals(idProducto)) {

                            if (item.getCantidad() > 1) {

                                item.setCantidad(item.getCantidad() - 1);

                                item.setSubtotal(
                                        item.getPrecio().multiply(
                                                BigDecimal.valueOf(item.getCantidad())));

                            } else {

                                venta.remove(i);

                            }

                            break;

                        }

                    }

                }

                session.setAttribute("venta", venta);

                return "redirect:/admin/ventas";

            }

}