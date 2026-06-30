package com.bodega.saas.pedido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodega.saas.pedido.model.Pedido;

public interface PedidoRepository
        extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstadoOrderByIdPedidoDesc(String estado);

    List<Pedido> findByEstadoInOrderByIdPedidoDesc(
            List<String> estados);

    Long countByEstado(String estado);

    Long countByEstadoIn(List<String> estados);

    @Query("""
            SELECT COALESCE(SUM(p.total),0)
            FROM Pedido p
            WHERE p.estado = :estado
            """)
    java.math.BigDecimal sumarTotalPorEstado(@Param("estado") String estado);

}
