package com.bodega.saas.venta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.bodega.saas.venta.model.DetalleVenta;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

}