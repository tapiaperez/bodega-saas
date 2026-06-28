package com.bodega.saas.venta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bodega.saas.venta.model.DetalleVenta;

public interface DetalleVentaRepository
        extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByIdVenta(Long idVenta);

    @Query("""
        SELECT d.idProducto,
            SUM(d.cantidad)
        FROM DetalleVenta d
        GROUP BY d.idProducto
        ORDER BY SUM(d.cantidad) DESC
        """)
        List<Object[]> obtenerProductosMasVendidos();

}