package com.bodega.saas.rol.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bodega.saas.rol.model.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);
}
