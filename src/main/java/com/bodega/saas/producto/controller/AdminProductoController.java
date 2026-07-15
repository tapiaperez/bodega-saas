package com.bodega.saas.producto.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bodega.saas.categoria.model.Categoria;
import com.bodega.saas.categoria.repository.CategoriaRepository;
import com.bodega.saas.producto.model.Producto;
import com.bodega.saas.producto.repository.ProductoRepository;
import com.bodega.saas.usuario.model.Usuario;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/productos")
public class AdminProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * HU03
     * Gestión de Inventario
     */
    @GetMapping
    public String listarProductos(

            @RequestParam(required = false)
            String buscar,

            @RequestParam(required = false)
            Long categoria,

            Model model) {

        // Productos
        model.addAttribute(
                "productos",
                productoRepository.buscarProductos(
                        buscar,
                        categoria));

        // Categorías
        model.addAttribute(
                "categorias",
                categoriaRepository.findAll());

        // Valores seleccionados
        model.addAttribute(
                "buscar",
                buscar);

        model.addAttribute(
                "categoriaSeleccionada",
                categoria);

        // Dashboard
        model.addAttribute(
                "totalProductos",
                productoRepository.countByEstado(1));

        model.addAttribute(
                "stockBajo",
                productoRepository.obtenerStockBajo().size());

        model.addAttribute(
                "productosDisponibles",
                productoRepository.countByEstadoAndStockActualGreaterThan(1, 0));

        model.addAttribute(
                "productosAgotados",
                productoRepository.countByEstadoAndStockActualLessThanEqual(1, 0));

        return "admin_productos";
    }


    /**
     * HU04
     * Registro y edición de productos.
     */
    @PostMapping("/guardar")
    public String guardarProducto(

            @ModelAttribute Producto producto,

            @RequestParam(required = false)
            Long idCategoria,

            HttpSession session,

            RedirectAttributes redirectAttributes) {

        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Debe ingresar el nombre del producto.");
            return "redirect:/admin/productos";
        }

        if (producto.getPrecioVenta() == null
                || producto.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El precio de venta debe ser mayor que cero.");
            return "redirect:/admin/productos";
        }

        if (producto.getStockActual() == null || producto.getStockActual() < 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El stock no puede ser negativo.");
            return "redirect:/admin/productos";
        }

        if (producto.getPrecioCompra() != null
                && producto.getPrecioCompra().compareTo(BigDecimal.ZERO) < 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El precio de compra no puede ser negativo.");
            return "redirect:/admin/productos";
        }

        Producto productoGuardar = producto;

        if (producto.getIdProducto() != null) {
            productoGuardar = productoRepository
                    .findById(producto.getIdProducto())
                    .orElse(producto);

            productoGuardar.setNombre(producto.getNombre());
            productoGuardar.setCodigoBarras(producto.getCodigoBarras());
            productoGuardar.setDescripcion(producto.getDescripcion());
            productoGuardar.setImagenUrl(producto.getImagenUrl());
            productoGuardar.setPrecioCompra(producto.getPrecioCompra());
            productoGuardar.setPrecioVenta(producto.getPrecioVenta());
            productoGuardar.setStockActual(producto.getStockActual());
            productoGuardar.setStockMinimo(producto.getStockMinimo());
            productoGuardar.setFechaVencimiento(producto.getFechaVencimiento());
            productoGuardar.setEstado(producto.getEstado());
        }

        if (productoGuardar.getEstado() == null) {
            productoGuardar.setEstado(1);
        }

        if (productoGuardar.getStockMinimo() == null) {
            productoGuardar.setStockMinimo(0);
        }

        if (productoGuardar.getDescuento() == null) {
            productoGuardar.setDescuento(BigDecimal.ZERO);
        }

        if (productoGuardar.getIdEmpresa() == null) {
            Object userObj = session.getAttribute("usuario");

            if (userObj instanceof Usuario usuario && usuario.getIdEmpresa() != null) {
                productoGuardar.setIdEmpresa(usuario.getIdEmpresa());
            } else {
                productoGuardar.setIdEmpresa(1L);
            }
        }

        if (idCategoria != null) {
            Categoria categoria = categoriaRepository
                    .findById(idCategoria)
                    .orElse(null);
            productoGuardar.setCategoria(categoria);
        }

        productoRepository.save(productoGuardar);

        redirectAttributes.addFlashAttribute(
                "success",
                "Producto guardado correctamente.");

        return "redirect:/admin/productos";
    }

    @PostMapping("/oferta")
    public String guardarOferta(

            @RequestParam Long idProducto,

            @RequestParam BigDecimal descuento,

            RedirectAttributes redirectAttributes) {

        if (descuento.compareTo(BigDecimal.ZERO) < 0
                || descuento.compareTo(BigDecimal.valueOf(100)) > 0) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "El descuento debe estar entre 0 y 100.");
            return "redirect:/admin/productos";
        }

        Producto producto = productoRepository
                .findById(idProducto)
                .orElse(null);

        if (producto == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se encontró el producto seleccionado.");
            return "redirect:/admin/productos";
        }

        producto.setDescuento(descuento);
        productoRepository.save(producto);

        redirectAttributes.addFlashAttribute(
                "success",
                "Oferta actualizada correctamente.");

        return "redirect:/admin/productos";
    }

    @PostMapping("/eliminar")
    public String desactivarProducto(

            @RequestParam Long idProducto,

            RedirectAttributes redirectAttributes) {

        Producto producto = productoRepository
                .findById(idProducto)
                .orElse(null);

        if (producto == null) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No se encontró el producto seleccionado.");
            return "redirect:/admin/productos";
        }

        producto.setEstado(0);
        productoRepository.save(producto);

        redirectAttributes.addFlashAttribute(
                "success",
                "Producto desactivado correctamente.");

        return "redirect:/admin/productos";
    }
}
