package com.bodega.saas.pedido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodega.saas.pedido.model.Pedido;

public interface PedidoRepository
        extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstadoOrderByIdPedidoDesc(String estado);

    List<Pedido> findByEstadoInOrderByIdPedidoDesc(
            List<String> estados);

}