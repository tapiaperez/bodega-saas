package com.bodega.saas.reporte.dto;

public class ProductoMasVendidoDTO {

    private String nombre;

    private Long cantidad;

    public ProductoMasVendidoDTO() {
    }

    public ProductoMasVendidoDTO(String nombre, Long cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}