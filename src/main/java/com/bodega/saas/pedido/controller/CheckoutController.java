package com.bodega.saas.pedido.controller;

import com.bodega.saas.common.ItemCarrito;
import com.bodega.saas.pedido.model.*;
import com.bodega.saas.pedido.repository.*;

import com.bodega.saas.producto.model.Producto;
import com.bodega.saas.producto.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CheckoutController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detalleRepository;

    @Autowired
    private ProductoRepository productoRepository; 
    
    @PostMapping("/checkout")
    public String procesar(

            @RequestParam String nombreContacto,
            @RequestParam String telefonoContacto,
            @RequestParam(required = false) String direccionEnvio,
            @RequestParam String tipoEntrega,

            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Object obj = session.getAttribute("carrito");

        if (!(obj instanceof List<?>)) {
            return "redirect:/";
        }

        List<ItemCarrito> carrito = (List<ItemCarrito>) obj;
        Long idEmpresa = (Long) session.getAttribute("idEmpresa");

        if (carrito.isEmpty()) return "redirect:/";

        // VALIDAR STOCK ANTES DE CREAR EL PEDIDO
        for (ItemCarrito item : carrito) {

            Producto producto = productoRepository
                    .findById(item.getIdProducto())
                    .orElse(null);

            if (producto == null) {
                return "redirect:/carrito";
            }

            if (item.getCantidad() > producto.getStockActual()) {

                redirectAttributes.addFlashAttribute(
                        "error",
                        "⚠ El producto \"" + producto.getNombre()
                        + "\" solo tiene "
                        + producto.getStockActual()
                        + " unidad(es) disponibles.");

                return "redirect:/carrito";

            }

        }

        Pedido pedido = new Pedido();
        pedido.setIdEmpresa(idEmpresa);
        pedido.setEstado("PENDIENTE");

        pedido.setNombreContacto(nombreContacto);

        pedido.setTelefonoContacto(telefonoContacto);

        if ("RECOJO".equals(tipoEntrega)) {

            pedido.setDireccionEnvio("RECOJO EN TIENDA");

        } else {

            pedido.setDireccionEnvio(direccionEnvio);
        }   
        pedidoRepository.save(pedido);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrito item : carrito) {

            // BUSCAR PRODUCTO REAL
            Producto producto = productoRepository
                    .findById(item.getIdProducto())
                    .orElse(null);

            // VALIDACIÓN
            if (producto == null) continue;

            BigDecimal precio = producto.getPrecioConDescuento();

            BigDecimal subtotal = precio.multiply(
                    BigDecimal.valueOf(item.getCantidad())
            );

            DetallePedido det = new DetallePedido();
            det.setIdPedido(pedido.getIdPedido());
            det.setIdProducto(item.getIdProducto());
            det.setCantidad(item.getCantidad());
            det.setPrecioUnitario(precio);
            det.setSubtotal(subtotal);

            detalleRepository.save(det);

            total = total.add(subtotal);
        }

        pedido.setTotal(total);
        pedidoRepository.save(pedido);

        session.removeAttribute("carrito");

        return "redirect:/pedido-exitoso";
    }
}
