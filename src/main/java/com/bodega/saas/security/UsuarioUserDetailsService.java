package com.bodega.saas.security;

import java.util.List;
import java.util.Locale;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bodega.saas.rol.repository.RolRepository;
import com.bodega.saas.usuario.model.Usuario;
import com.bodega.saas.usuario.repository.UsuarioRepository;

@Service
public class UsuarioUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioUserDetailsService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + email));

        String rol = resolverRol(usuario);
        boolean activo = usuario.getEstado() == null || usuario.getEstado() == 1;

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + rol)))
                .disabled(!activo)
                .build();
    }

    private String resolverRol(Usuario usuario) {
        if (usuario.getIdRol() != null) {
            return rolRepository.findById(usuario.getIdRol())
                    .map(rol -> normalizarRol(rol.getNombre()))
                    .orElse("CAJERO");
        }

        if (usuario.getRol() != null && !usuario.getRol().isBlank()) {
            return normalizarRol(usuario.getRol());
        }

        return "CAJERO";
    }

    private String normalizarRol(String rol) {
        return rol
                .trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_');
    }
}
