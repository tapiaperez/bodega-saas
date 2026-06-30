package com.bodega.saas.reporte.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bodega.saas.pedido.model.Pedido;
import com.bodega.saas.pedido.repository.PedidoRepository;
import com.bodega.saas.producto.model.Producto;
import com.bodega.saas.producto.repository.ProductoRepository;
import com.bodega.saas.reporte.dto.ProductoMasVendidoDTO;
import com.bodega.saas.venta.repository.DetalleVentaRepository;
import com.bodega.saas.venta.repository.VentaRepository;

@Controller
public class ReporteController {



    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @GetMapping("/admin/reportes")
    public String reportes(@RequestParam(required = false) LocalDate desde,
                           @RequestParam(required = false) LocalDate hasta,
                           @RequestParam(required = false) String origen,
                           @RequestParam(required = false) String estado,
                           Model model) {

        LocalDate fechaHasta = hasta != null ? hasta : LocalDate.now();
        LocalDate fechaDesde = desde != null ? desde : fechaHasta.withDayOfMonth(1);
        LocalDateTime desdeInicio = fechaDesde.atStartOfDay();
        LocalDateTime hastaFin = fechaHasta.atTime(LocalTime.MAX);

        model.addAttribute(
                "ventasHoy",
                ventaRepository.obtenerVentasHoy());

        model.addAttribute(
                "ventasMes",
                ventaRepository.obtenerVentasMes());

        model.addAttribute(
                "cantidadVentas",
                ventaRepository.contarVentas());

        model.addAttribute("productosActivos",
                productoRepository.countByEstado(1));

        model.addAttribute("pedidosPendientes",
                pedidoRepository.count());

        model.addAttribute(
                "ultimasVentas",
                ventaRepository.buscarPorFiltros(desdeInicio, hastaFin, origen, estado)
                        .stream()
                        .limit(20)
                        .toList());

        model.addAttribute(
                "ventasFiltradas",
                ventaRepository.sumarPorFiltros(desdeInicio, hastaFin, origen, estado));

        model.addAttribute(
                "cantidadVentasFiltradas",
                ventaRepository.contarPorFiltros(desdeInicio, hastaFin, origen, estado));

        model.addAttribute(
                "ventasPos",
                ventaRepository.sumarPorOrigen(desdeInicio, hastaFin, "POS"));

        model.addAttribute(
                "ventasWeb",
                ventaRepository.sumarPorOrigen(desdeInicio, hastaFin, "WEB"));

        model.addAttribute("pedidosPendientesConteo",
                pedidoRepository.countByEstado("PENDIENTE"));

        model.addAttribute("pedidosAtendidosConteo",
                ventaRepository.contarPedidosEntregadosHoy());

        model.addAttribute("pedidosPendientesTotal",
                pedidoRepository.sumarTotalPorEstado("PENDIENTE"));

        model.addAttribute("productosStockBajo",
                productoRepository.obtenerStockBajo());

        model.addAttribute("productosOferta",
                productoRepository.obtenerProductosConOferta());

        model.addAttribute("productosMasVendidos",
                obtenerProductosMasVendidos());

        model.addAttribute("pedidosPorAtender",
                obtenerPedidosPorAtender());

        model.addAttribute("desde", fechaDesde);
        model.addAttribute("hasta", fechaHasta);
        model.addAttribute("origen", origen);
        model.addAttribute("estado", estado);

        return "reportes";

    }

    private List<ProductoMasVendidoDTO> obtenerProductosMasVendidos() {

        List<ProductoMasVendidoDTO> productos = new ArrayList<>();

        for (Object[] fila : detalleVentaRepository.obtenerProductosMasVendidos()) {

            Long idProducto = ((Number) fila[0]).longValue();
            Long cantidad = ((Number) fila[1]).longValue();

            String nombre = productoRepository.findById(idProducto)
                    .map(Producto::getNombre)
                    .orElse("Producto");

            productos.add(new ProductoMasVendidoDTO(nombre, cantidad));

            if (productos.size() == 5) {
                break;
            }

        }

        return productos;

    }

    private List<Pedido> obtenerPedidosPorAtender() {

        return pedidoRepository.findByEstadoInOrderByIdPedidoDesc(
                List.of(
                        "PENDIENTE",
                        "CONFIRMADO",
                        "EN_PREPARACION",
                        "EN_CAMINO"
                )
        );

    }

}
