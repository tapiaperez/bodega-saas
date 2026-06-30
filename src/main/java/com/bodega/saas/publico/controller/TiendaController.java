package com.bodega.saas.publico.controller;

import com.bodega.saas.producto.repository.ProductoRepository;
import com.bodega.saas.common.ItemCarrito;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//import com.bodega.saas.categoria.model.Categoria;
import com.bodega.saas.categoria.repository.CategoriaRepository;
//import com.bodega.saas.empresa.model.Empresa;
import com.bodega.saas.empresa.repository.EmpresaRepository;
import java.util.List;

@Controller
public class TiendaController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping("/tienda/{idEmpresa}")
    public String verTienda(
            @PathVariable Long idEmpresa,
            @RequestParam(required = false) Long categoria,
            Model model,
            HttpSession session) {

        // 1. Validar empresa
        if (!empresaRepository.existsById(idEmpresa)) {
            return "error/404";
        }

        // 2. Obtener productos
        if (categoria != null) {

            // Validar categoría
            if (!categoriaRepository.existsById(categoria)) {
                return "error/404";
            }

            model.addAttribute(
                    "productos",
                    productoRepository.obtenerCatalogoDisponiblePorCategoria(
                            idEmpresa,
                            categoria
                    )
            );

            model.addAttribute("categoriaSeleccionada", categoria);

        } else {

            model.addAttribute(
                    "productos",
                    productoRepository.obtenerCatalogoDisponible(idEmpresa)
            );
        }

        // 3. Cargar categorías para filtros
        model.addAttribute(
                "categorias",
                categoriaRepository.findAll()
        );

        model.addAttribute("idEmpresa", idEmpresa);

        // 4. Contador carrito
        Object obj = session.getAttribute("carrito");

        int cantidad = 0;

        if (obj instanceof List<?>) {

            List<ItemCarrito> carrito = (List<ItemCarrito>) obj;

            cantidad = carrito.stream()
                    .mapToInt(ItemCarrito::getCantidad)
                    .sum();
        }

        model.addAttribute("cantidadCarrito", cantidad);

        return "tienda";
    }
}
