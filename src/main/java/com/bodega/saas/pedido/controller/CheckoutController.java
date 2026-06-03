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

@Controller
public class CheckoutController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detalleRepository;

    @Autowired
    private ProductoRepository productoRepository; 
    @PostMapping("/checkout")
    public String procesar(HttpSession session) {

        Object obj = session.getAttribute("carrito");

        if (!(obj instanceof List<?>)) {
            return "redirect:/";
        }

        List<ItemCarrito> carrito = (List<ItemCarrito>) obj;
        Long idEmpresa = (Long) session.getAttribute("idEmpresa");

        if (carrito.isEmpty()) return "redirect:/";

        Pedido pedido = new Pedido();
        pedido.setIdEmpresa(idEmpresa);
        pedido.setEstado("PENDIENTE");

        pedidoRepository.save(pedido);

        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrito item : carrito) {

            // BUSCAR PRODUCTO REAL
            Producto producto = productoRepository
                    .findById(item.getIdProducto())
                    .orElse(null);

            // VALIDACIÓN
            if (producto == null) continue;

            BigDecimal precio = producto.getPrecioVenta();

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