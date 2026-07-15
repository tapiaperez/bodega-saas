

package com.bodega.saas.venta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodega.saas.venta.model.Venta;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

        @Query("""
            SELECT v
            FROM Venta v
            WHERE v.fechaEmision >= :desde
            AND v.fechaEmision <= :hasta
            AND (:origen IS NULL OR :origen = '' OR v.origen = :origen)
            AND (:estado IS NULL OR :estado = '' OR v.estado = :estado)
            ORDER BY v.fechaEmision DESC
            """)
        List<Venta> buscarPorFiltros(
                @Param("desde") LocalDateTime desde,
                @Param("hasta") LocalDateTime hasta,
                @Param("origen") String origen,
                @Param("estado") String estado);

        @Query("""
            SELECT COALESCE(SUM(v.total),0)
            FROM Venta v
            WHERE v.fechaEmision >= :desde
            AND v.fechaEmision <= :hasta
            AND (:origen IS NULL OR :origen = '' OR v.origen = :origen)
            AND (:estado IS NULL OR :estado = '' OR v.estado = :estado)
            """)
        BigDecimal sumarPorFiltros(
                @Param("desde") LocalDateTime desde,
                @Param("hasta") LocalDateTime hasta,
                @Param("origen") String origen,
                @Param("estado") String estado);

        @Query("""
            SELECT COUNT(v)
            FROM Venta v
            WHERE v.fechaEmision >= :desde
            AND v.fechaEmision <= :hasta
            AND (:origen IS NULL OR :origen = '' OR v.origen = :origen)
            AND (:estado IS NULL OR :estado = '' OR v.estado = :estado)
            """)
        Long contarPorFiltros(
                @Param("desde") LocalDateTime desde,
                @Param("hasta") LocalDateTime hasta,
                @Param("origen") String origen,
                @Param("estado") String estado);

        @Query("""
            SELECT COALESCE(SUM(v.total),0)
            FROM Venta v
            WHERE v.fechaEmision >= :desde
            AND v.fechaEmision <= :hasta
            AND v.origen = :origen
            """)
        BigDecimal sumarPorOrigen(
                @Param("desde") LocalDateTime desde,
                @Param("hasta") LocalDateTime hasta,
                @Param("origen") String origen);

        @Query("""
            SELECT COUNT(v)
            FROM Venta v
            WHERE v.idPedido IS NOT NULL
            AND DATE(v.fechaEmision) = CURRENT_DATE
            """)
        Long contarPedidosEntregadosHoy();

}
