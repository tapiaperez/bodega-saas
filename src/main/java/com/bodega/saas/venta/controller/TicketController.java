package com.bodega.saas.venta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bodega.saas.producto.repository.ProductoRepository;
import com.bodega.saas.venta.repository.DetalleVentaRepository;
import com.bodega.saas.venta.repository.VentaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.bodega.saas.producto.model.Producto;
import com.bodega.saas.venta.dto.TicketItem;
import com.bodega.saas.venta.model.DetalleVenta;

@Controller
public class TicketController {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    

    @GetMapping("/admin/ventas/ticket/{idVenta}")
    public String ticket(
            @PathVariable Long idVenta,
            Model model) {

        model.addAttribute(
                "venta",
                ventaRepository.findById(idVenta).orElse(null));

        List<DetalleVenta> detalles =
                detalleVentaRepository.findByIdVenta(idVenta);

        List<TicketItem> items =
                new ArrayList<>();

        BigDecimal ahorroTotal = BigDecimal.ZERO;

        for (DetalleVenta d : detalles) {

            Producto producto =
                    productoRepository.findById(d.getIdProducto())
                            .orElse(null);

            TicketItem item = new TicketItem();

            item.setNombre(
                    producto != null
                            ? producto.getNombre()
                            : "Producto");

            item.setCantidad(d.getCantidad());

            item.setPrecio(d.getPrecioUnitario());

            item.setSubtotal(d.getSubtotal());

            if (producto != null) {
                BigDecimal precioOriginal = producto.getPrecioVenta();
                BigDecimal precioVendido = d.getPrecioUnitario();

                item.setPrecioOriginal(precioOriginal);

                if (precioOriginal != null
                        && precioVendido != null
                        && precioOriginal.compareTo(precioVendido) > 0) {

                    BigDecimal ahorroUnitario =
                            precioOriginal.subtract(precioVendido)
                                    .setScale(2, RoundingMode.HALF_UP);

                    BigDecimal descuento =
                            ahorroUnitario
                                    .multiply(BigDecimal.valueOf(100))
                                    .divide(precioOriginal, 2, RoundingMode.HALF_UP);

                    item.setAhorroUnitario(ahorroUnitario);
                    item.setDescuento(descuento);

                    ahorroTotal = ahorroTotal.add(item.getAhorroTotal());

                } else {
                    item.setAhorroUnitario(BigDecimal.ZERO);
                    item.setDescuento(BigDecimal.ZERO);
                }
            }

            items.add(item);

        }

        model.addAttribute("items", items);
        model.addAttribute("ahorroTotal", ahorroTotal);
        model.addAttribute("tieneAhorro", ahorroTotal.compareTo(BigDecimal.ZERO) > 0);

        return "ticket";
    }

}
