package com.bodega.saas.producto.repository;

import com.bodega.saas.producto.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByIdEmpresa(Long idEmpresa);
     List<Producto> findByCategoria_IdCategoria(Long idCategoria);
     List<Producto> findByIdEmpresaAndCategoria_IdCategoria(
            Long idEmpresa,
            Long idCategoria
    );
}