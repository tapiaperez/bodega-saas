package com.bodega.saas.reporte.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bodega.saas.pedido.repository.PedidoRepository;
import com.bodega.saas.producto.repository.ProductoRepository;
import com.bodega.saas.venta.repository.VentaRepository;

@Controller
public class ReporteController {



    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/admin/reportes")
public String reportes(Model model) {

        model.addAttribute(
                "ventasHoy",
                ventaRepository.obtenerVentasHoy());

        model.addAttribute(
                "ventasMes",
                ventaRepository.obtenerVentasMes());

        model.addAttribute(
                "cantidadVentas",
                ventaRepository.contarVentas());

        model.addAttribute("productos",
                productoRepository.count());

        model.addAttribute("pedidosPendientes",
                pedidoRepository.count());
        
       
        model.addAttribute(
                "ultimasVentas",
                ventaRepository.findTop10ByOrderByFechaEmisionDesc());                
                return "reportes";

            }

}