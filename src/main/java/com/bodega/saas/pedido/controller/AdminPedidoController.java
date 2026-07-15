package com.bodega.saas.pedido.controller;

import com.bodega.saas.pedido.model.DetallePedido;
import com.bodega.saas.pedido.model.Pedido;
import com.bodega.saas.pedido.repository.DetallePedidoRepository;
import com.bodega.saas.pedido.repository.PedidoRepository;
import com.bodega.saas.producto.repository.ProductoRepository;
import com.bodega.saas.venta.service.VentaService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminPedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VentaService ventaService;


    @GetMapping("/admin/pedidos")
    public String listar(Model model) {

        model.addAttribute(
            "pedidos",
            pedidoRepository.findByEstadoInOrderByIdPedidoDesc(
                List.of(
                    "PENDIENTE",
                    "CONFIRMADO",
                    "EN_PREPARACION",
                    "EN_CAMINO"
                )
            )
        );

        return "admin_pedidos";
    }

    @GetMapping("/admin/pedidos/{id}")
    public String detallePedido(
            @PathVariable Long id,
            Model model) {

        Pedido pedido = pedidoRepository
                .findById(id)
                .orElse(null);

        if (pedido == null) {
            return "redirect:/admin/pedidos";
        }

        List<DetallePedido> detalles =
                detallePedidoRepository.findByIdPedido(id);

        Map<Long, String> nombresProductos =
                new HashMap<>();

        for (DetallePedido d : detalles) {

            productoRepository.findById(d.getIdProducto())
                    .ifPresent(producto -> {

                        nombresProductos.put(
                                d.getIdProducto(),
                                producto.getNombre()
                        );

                    });
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("detalles", detalles);
        model.addAttribute("nombresProductos", nombresProductos);

        return "pedido_detalle";
    }

    @PostMapping("/admin/pedidos/estado")
    public String actualizarEstado(

            @RequestParam Long idPedido,
            @RequestParam String estado) {

        Pedido pedido = pedidoRepository
                .findById(idPedido)
                .orElse(null);

        if (pedido == null) {
            return "redirect:/admin/pedidos";
        }

        pedido.setEstado(estado);

        pedidoRepository.save(pedido);

        return "redirect:/admin/pedidos/" + idPedido;
    }

   @PostMapping("/admin/pedidos/cambiarEstado")
    public String cambiarEstado(

            @RequestParam Long idPedido,
            @RequestParam String nuevoEstado,
            RedirectAttributes redirectAttributes) {

        Pedido pedido = pedidoRepository
                .findById(idPedido)
                .orElse(null);

        if (pedido == null) {
            return "redirect:/admin/pedidos";
        }

        try {

            switch (nuevoEstado) {

                case "CONFIRMADO":

                    // Validar y reservar stock
                    ventaService.reservarStock(idPedido);

                    break;

                case "ENTREGADO":

                    // Registrar la venta
                    ventaService.generarVentaDesdePedido(idPedido);

                    break;

                default:
                    break;
            }

            // Actualizar el estado del pedido
            pedido.setEstado(nuevoEstado);

            pedidoRepository.save(pedido);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Pedido actualizado correctamente.");

        } catch (RuntimeException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());

            return "redirect:/admin/pedidos/" + idPedido;
        }

        return "redirect:/admin/pedidos/" + idPedido;
    }
}