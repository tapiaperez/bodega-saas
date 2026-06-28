

package com.bodega.saas.venta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodega.saas.venta.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("""
        SELECT COALESCE(MAX(v.correlativo),0)
        FROM Venta v
        WHERE v.serie = :serie
    """)
    Integer obtenerUltimoCorrelativo(
            @Param("serie") String serie);

}