package com.bodega.saas.pedido.repository;

import com.bodega.saas.pedido.model.DetallePedido;
import com.bodega.saas.pedido.model.Pedido;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetallePedidoRepository
        extends JpaRepository<DetallePedido, Long> {

    List<DetallePedido> findByIdPedido(Long idPedido);


}