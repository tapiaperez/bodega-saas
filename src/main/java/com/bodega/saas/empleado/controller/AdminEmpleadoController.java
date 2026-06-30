package com.bodega.saas.empleado.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bodega.saas.rol.repository.RolRepository;
import com.bodega.saas.usuario.model.Usuario;
import com.bodega.saas.usuario.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/empleados")
public class AdminEmpleadoController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminEmpleadoController(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listarEmpleados(
            Model model,
            HttpSession session) {

        Long idEmpresa = obtenerIdEmpresa(session);

        model.addAttribute(
                "empleados",
                usuarioRepository.findByIdEmpresaOrderByNombreCompletoAsc(idEmpresa));

        model.addAttribute(
                "roles",
                rolRepository.findAll());

        return "admin_empleados";
    }

    @PostMapping("/guardar")
    public String guardarEmpleado(

            @RequestParam(required = false)
            Long idUsuario,

            @RequestParam
            String nombreCompleto,

            @RequestParam
            String email,

            @RequestParam(required = false)
            String password,

            @RequestParam
            Long idRol,

            @RequestParam(defaultValue = "1")
            Integer estado,

            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debe ingresar el nombre del empleado.");
            return "redirect:/admin/empleados";
        }

        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debe ingresar el correo del empleado.");
            return "redirect:/admin/empleados";
        }

        Usuario empleado;

        if (idUsuario == null) {
            if (password == null || password.isBlank()) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "Debe ingresar una contraseña inicial.");
                return "redirect:/admin/empleados";
            }

            if (usuarioRepository.existsByEmail(email.trim())) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "Ya existe un usuario con ese correo.");
                return "redirect:/admin/empleados";
            }

            empleado = new Usuario();
            empleado.setIdEmpresa(obtenerIdEmpresa(session));
        } else {
            empleado = usuarioRepository.findById(idUsuario).orElse(null);

            if (empleado == null) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "No se encontró el empleado seleccionado.");
                return "redirect:/admin/empleados";
            }

            boolean emailUsadoPorOtro = usuarioRepository
                    .findByEmail(email.trim())
                    .filter(usuario -> !usuario.getIdUsuario().equals(idUsuario))
                    .isPresent();

            if (emailUsadoPorOtro) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "Ya existe un usuario con ese correo.");
                return "redirect:/admin/empleados";
            }
        }

        empleado.setNombreCompleto(nombreCompleto.trim());
        empleado.setEmail(email.trim());
        empleado.setIdRol(idRol);
        empleado.setEstado(estado);

        if (password != null && !password.isBlank()) {
            empleado.setPasswordHash(passwordEncoder.encode(password.trim()));
        }

        usuarioRepository.save(empleado);

        redirectAttributes.addFlashAttribute(
                "success",
                "Empleado guardado correctamente.");

        return "redirect:/admin/empleados";
    }

    @PostMapping("/estado")
    public String cambiarEstado(

            @RequestParam
            Long idUsuario,

            @RequestParam
            Integer estado,

            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario empleado = usuarioRepository.findById(idUsuario).orElse(null);

        if (empleado == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se encontró el empleado seleccionado.");
            return "redirect:/admin/empleados";
        }

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        if (usuarioSesion != null
                && usuarioSesion.getIdUsuario().equals(empleado.getIdUsuario())
                && estado == 0) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "No puedes desactivar tu propio usuario.");
            return "redirect:/admin/empleados";
        }

        empleado.setEstado(estado);
        usuarioRepository.save(empleado);

        redirectAttributes.addFlashAttribute(
                "success",
                estado == 1
                        ? "Empleado activado correctamente."
                        : "Empleado desactivado correctamente.");

        return "redirect:/admin/empleados";
    }

    private Long obtenerIdEmpresa(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario != null && usuario.getIdEmpresa() != null) {
            return usuario.getIdEmpresa();
        }

        return 1L;
    }
}
