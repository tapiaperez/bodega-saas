package com.bodega.saas.usuario.repository;

import com.bodega.saas.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByIdEmpresaOrderByNombreCompletoAsc(Long idEmpresa);

    boolean existsByEmail(String email);
}
