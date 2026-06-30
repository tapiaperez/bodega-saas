package com.bodega.saas.venta.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VentaItem {

    private Long idProducto;

    private String nombre;

    private BigDecimal precio;

    private BigDecimal precioOriginal;

    private BigDecimal descuento;

    private BigDecimal ahorroUnitario;

    private Integer cantidad;

    private BigDecimal subtotal;

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getPrecioOriginal() {
        return precioOriginal;
    }

    public void setPrecioOriginal(BigDecimal precioOriginal) {
        this.precioOriginal = precioOriginal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getAhorroUnitario() {
        return ahorroUnitario;
    }

    public void setAhorroUnitario(BigDecimal ahorroUnitario) {
        this.ahorroUnitario = ahorroUnitario;
    }

    public BigDecimal getAhorroTotal() {
        if (ahorroUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }

        return ahorroUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    public boolean isTieneDescuento() {
        return ahorroUnitario != null && ahorroUnitario.compareTo(BigDecimal.ZERO) > 0;
    }

    public Integer getDescuentoRedondeado() {
        if (descuento == null) {
            return 0;
        }

        return descuento.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

}
