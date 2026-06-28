

package com.bodega.saas.venta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodega.saas.venta.model.Venta;
import java.util.List;


public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("""
        SELECT COALESCE(MAX(v.correlativo),0)
        FROM Venta v
        WHERE v.serie = :serie
    """)
    Integer obtenerUltimoCorrelativo(
            @Param("serie") String serie);

    @Query("""
        SELECT COALESCE(SUM(v.total),0)
        FROM Venta v
        WHERE DATE(v.fechaEmision)=CURRENT_DATE
        """)
        java.math.BigDecimal obtenerVentasHoy();


        @Query("""
        SELECT COALESCE(SUM(v.total),0)
        FROM Venta v
        WHERE YEAR(v.fechaEmision)=YEAR(CURRENT_DATE)
        AND MONTH(v.fechaEmision)=MONTH(CURRENT_DATE)
        """)
        java.math.BigDecimal obtenerVentasMes();


        @Query("""
        SELECT COUNT(v)
        FROM Venta v
        """)
        Long contarVentas();

        

        List<Venta> findTop10ByOrderByFechaEmisionDesc();

}