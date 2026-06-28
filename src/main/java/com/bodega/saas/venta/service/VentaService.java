package com.bodega.saas.venta.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bodega.saas.pedido.model.Pedido;
import com.bodega.saas.pedido.repository.DetallePedidoRepository;
import com.bodega.saas.pedido.repository.PedidoRepository;
import com.bodega.saas.producto.repository.ProductoRepository;
import com.bodega.saas.venta.model.Venta;
import com.bodega.saas.venta.repository.DetalleVentaRepository;
import com.bodega.saas.venta.repository.VentaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import com.bodega.saas.pedido.model.DetallePedido;
import com.bodega.saas.producto.model.Producto;
import com.bodega.saas.venta.model.DetalleVenta;
import com.bodega.saas.venta.dto.VentaItem;

@Service
public class VentaService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    

    @Transactional
    
    public void generarVentaDesdePedido(Long idPedido) {

        Pedido pedido = pedidoRepository
                .findById(idPedido)
                .orElse(null);

        if (pedido == null) {
            return;
        }

        Venta venta = new Venta();

        venta.setIdEmpresa(pedido.getIdEmpresa());

        venta.setIdUsuario(1L);

        venta.setIdPedido(pedido.getIdPedido());

        venta.setTipoComprobante("TICKET");

        // Por ahora las ventas WEB siempre generan Ticket
        String tipoComprobante = "TICKET";

        String serie = obtenerSerie(tipoComprobante);

        Integer ultimo =
                ventaRepository.obtenerUltimoCorrelativo(serie);

        venta.setTipoComprobante(tipoComprobante);

        venta.setSerie(serie);

        venta.setCorrelativo(ultimo + 1);

        venta.setSubtotal(pedido.getTotal());

        venta.setIgv(BigDecimal.ZERO);

        venta.setTotal(pedido.getTotal());

        venta.setEstado("REGISTRADA");

        venta.setOrigen("WEB");

        ventaRepository.save(venta);

        List<DetallePedido> detalles =
        detallePedidoRepository.findByIdPedido(idPedido);

        for (DetallePedido dp : detalles) {

            // Crear detalle de venta
            DetalleVenta dv = new DetalleVenta();

            dv.setIdVenta(venta.getIdVenta());

            dv.setIdProducto(dp.getIdProducto());

            dv.setCantidad(dp.getCantidad());

            dv.setPrecioUnitario(dp.getPrecioUnitario());

            dv.setSubtotal(dp.getSubtotal());

            detalleVentaRepository.save(dv);

        }

    }


        @Transactional
        public void reservarStock(Long idPedido) {

        List<DetallePedido> detalles =
                detallePedidoRepository.findByIdPedido(idPedido);

        for (DetallePedido dp : detalles) {

            Producto producto = productoRepository
                    .findById(dp.getIdProducto())
                    .orElse(null);

            if (producto == null) {
                continue;
            }

            // Validar stock disponible
            if (producto.getStockActual() < dp.getCantidad()) {

                throw new RuntimeException(
                        "Stock insuficiente para el producto: "
                        + producto.getNombre());

            }

            // Reservar stock
            int nuevoStock =
                    producto.getStockActual() - dp.getCantidad();

            producto.setStockActual(nuevoStock);

            productoRepository.save(producto);

        }

    }


    @Transactional
    public Venta generarVentaPOS(Long idEmpresa,
                                Long idUsuario,
                                List<VentaItem> items) {

        Venta venta = new Venta();

        venta.setIdEmpresa(idEmpresa);

        venta.setIdUsuario(idUsuario);

        venta.setTipoComprobante("TICKET");

        String serie = obtenerSerie("TICKET");

        Integer ultimo =
                ventaRepository.obtenerUltimoCorrelativo(serie);

        venta.setSerie(serie);

        venta.setCorrelativo(ultimo + 1);

        BigDecimal total = BigDecimal.ZERO;

        for (VentaItem item : items) {

            total = total.add(item.getSubtotal());

        }

        venta.setSubtotal(total);

        venta.setIgv(BigDecimal.ZERO);

        venta.setTotal(total);

        venta.setEstado("REGISTRADA");

        venta.setOrigen("POS");

        ventaRepository.save(venta);

        for (VentaItem item : items) {

            DetalleVenta dv = new DetalleVenta();

            dv.setIdVenta(venta.getIdVenta());

            dv.setIdProducto(item.getIdProducto());

            dv.setCantidad(item.getCantidad());

            dv.setPrecioUnitario(item.getPrecio());

            dv.setSubtotal(item.getSubtotal());

            detalleVentaRepository.save(dv);

            Producto producto =
                    productoRepository.findById(item.getIdProducto())
                            .orElseThrow();

            if (producto.getStockActual() < item.getCantidad()) {

                throw new RuntimeException(
                        "Stock insuficiente para " +
                        producto.getNombre());

            }

            producto.setStockActual(
                    producto.getStockActual() -
                    item.getCantidad());

            productoRepository.save(producto);

        }

        return venta;

    }


    private String obtenerSerie(String tipoComprobante) {

    switch (tipoComprobante) {

        case "BOLETA":
            return "B001";

        case "FACTURA":
            return "F001";

        default:
            return "T001";
            }

        }



}