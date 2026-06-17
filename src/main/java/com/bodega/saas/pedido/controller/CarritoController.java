package com.bodega.saas.pedido.controller;

import com.bodega.saas.common.ItemCarrito;
import com.bodega.saas.producto.model.Producto;
import com.bodega.saas.producto.repository.ProductoRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private ProductoRepository productoRepository;

    @PostMapping("/agregar")
    public String agregar(@RequestParam Long idProducto,
                          @RequestParam int cantidad,
                          @RequestParam Long idEmpresa,
                          HttpSession session) {

        Object obj = session.getAttribute("carrito");

        List<ItemCarrito> carrito;

        if (obj instanceof List<?>) {
            carrito = (List<ItemCarrito>) obj;
        } else {
            carrito = new ArrayList<>();
        }

        // BUSCAR PRODUCTO EN BD
        Producto producto = productoRepository.findById(idProducto).orElse(null);

        if (producto == null) {
            return "redirect:/tienda/" + idEmpresa;
        }

        // VER SI YA EXISTE
        Optional<ItemCarrito> existente = carrito.stream()
                .filter(i -> i.getIdProducto().equals(idProducto))
                .findFirst();

        if (existente.isPresent()) {

            ItemCarrito item = existente.get();

            int nuevaCantidad = item.getCantidad() + cantidad;
            item.setCantidad(nuevaCantidad);

            // RECALCULAR SUBTOTAL
            BigDecimal subtotal = producto.getPrecioVenta()
                    .multiply(BigDecimal.valueOf(nuevaCantidad));

            item.setSubtotal(subtotal);

        } else {

            ItemCarrito item = new ItemCarrito(idProducto, cantidad);

            // LLENAR DATOS COMPLETOS
            item.setNombre(producto.getNombre());
            item.setPrecio(producto.getPrecioVenta());

            BigDecimal subtotal = producto.getPrecioVenta()
                    .multiply(BigDecimal.valueOf(cantidad));

            item.setSubtotal(subtotal);

            carrito.add(item);
        }

        session.setAttribute("carrito", carrito);
        session.setAttribute("idEmpresa", idEmpresa);

        return "redirect:/tienda/" + idEmpresa;
    }

    @PostMapping("/eliminar")
        public String eliminar(
                @RequestParam Long idProducto,
                HttpSession session) {

            Object obj = session.getAttribute("carrito");

            if (!(obj instanceof List<?>)) {
                return "redirect:/carrito";
            }

            List<ItemCarrito> carrito = (List<ItemCarrito>) obj;

            carrito.removeIf(item ->
                    item.getIdProducto().equals(idProducto));

            session.setAttribute("carrito", carrito);

            return "redirect:/carrito";
        }

        @PostMapping("/vaciar")
            public String vaciar(HttpSession session) {

                session.removeAttribute("carrito");

                return "redirect:/carrito";
            }

            @PostMapping("/actualizar")
            public String actualizar(
                    @RequestParam Long idProducto,
                    @RequestParam Integer cantidad,
                    HttpSession session) {

                Object obj = session.getAttribute("carrito");

                if (!(obj instanceof List<?>)) {
                    return "redirect:/carrito";
                }

                List<ItemCarrito> carrito =
                        (List<ItemCarrito>) obj;

                for (ItemCarrito item : carrito) {

                    if (item.getIdProducto().equals(idProducto)) {
                        Producto producto = productoRepository
                                .findById(idProducto)
                                .orElse(null);

                        if(producto != null &&
                        cantidad > producto.getStockActual()){

                            cantidad = producto.getStockActual();
                        }
                        item.setCantidad(cantidad);

                        BigDecimal subtotal =
                                item.getPrecio()
                                    .multiply(
                                        BigDecimal.valueOf(cantidad)
                                    );

                        item.setSubtotal(subtotal);

                        break;
                    }
                }

                session.setAttribute("carrito", carrito);

                return "redirect:/carrito";
            }
}