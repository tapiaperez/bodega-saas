package com.bodega.saas.producto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bodega.saas.producto.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /*====================================================
     * CATÁLOGO WEB
     *====================================================*/

    List<Producto> findByIdEmpresa(Long idEmpresa);

    List<Producto> findByCategoria_IdCategoria(Long idCategoria);

    List<Producto> findByIdEmpresaAndCategoria_IdCategoria(
            Long idEmpresa,
            Long idCategoria);

    @Query("""
            SELECT p
            FROM Producto p
            WHERE p.idEmpresa = :idEmpresa
            AND p.estado = 1
            AND COALESCE(p.stockActual, 0) > 0
            ORDER BY
                CASE WHEN COALESCE(p.descuento, 0) > 0 THEN 0 ELSE 1 END,
                p.nombre ASC
            """)
    List<Producto> obtenerCatalogoDisponible(
            @Param("idEmpresa") Long idEmpresa);

    @Query("""
            SELECT p
            FROM Producto p
            WHERE p.idEmpresa = :idEmpresa
            AND p.categoria.idCategoria = :idCategoria
            AND p.estado = 1
            AND COALESCE(p.stockActual, 0) > 0
            ORDER BY
                CASE WHEN COALESCE(p.descuento, 0) > 0 THEN 0 ELSE 1 END,
                p.nombre ASC
            """)
    List<Producto> obtenerCatalogoDisponiblePorCategoria(
            @Param("idEmpresa") Long idEmpresa,
            @Param("idCategoria") Long idCategoria);

    /*====================================================
     * INVENTARIO (HU03)
     *====================================================*/

    // Listar productos activos
    List<Producto> findByEstadoOrderByNombreAsc(Integer estado);

    // Buscar por nombre
    List<Producto> findByEstadoAndNombreContainingIgnoreCaseOrderByNombreAsc(
            Integer estado,
            String nombre);

    // Filtrar por categoría
    List<Producto> findByEstadoAndCategoria_IdCategoriaOrderByNombreAsc(
            Integer estado,
            Long idCategoria);

    // Buscar + Categoría
    @Query("""
            SELECT p
            FROM Producto p
            WHERE p.estado = 1
            AND (:nombre IS NULL OR :nombre = ''
                 OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
            AND (:categoria IS NULL
                 OR p.categoria.idCategoria = :categoria)
            ORDER BY p.nombre
            """)
    List<Producto> buscarProductos(
            @Param("nombre") String nombre,
            @Param("categoria") Long categoria);

    /*====================================================
     * DASHBOARD INVENTARIO
     *====================================================*/

    // Productos con stock bajo
    @Query("""
            SELECT p
            FROM Producto p
            WHERE p.estado = 1
            AND COALESCE(p.stockActual, 0) > 0
            AND COALESCE(p.stockActual, 0) <= COALESCE(p.stockMinimo, 0)
            ORDER BY p.stockActual ASC
            """)
    List<Producto> obtenerStockBajo();

    // Productos activos
    Long countByEstado(Integer estado);

    Long countByEstadoAndStockActualGreaterThan(Integer estado, Integer stockActual);

    Long countByEstadoAndStockActualLessThanEqual(Integer estado, Integer stockActual);

    @Query("""
            SELECT p
            FROM Producto p
            WHERE p.estado = 1
            AND COALESCE(p.descuento, 0) > 0
            ORDER BY p.descuento DESC, p.nombre ASC
            """)
    List<Producto> obtenerProductosConOferta();

}
