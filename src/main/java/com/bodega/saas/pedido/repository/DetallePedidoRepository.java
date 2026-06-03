package com.bodega.saas.pedido.repository;

import com.bodega.saas.pedido.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}