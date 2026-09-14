package com.lunorion.labs.core.producto.application.dto.out;

import java.math.BigDecimal;
import java.util.UUID;

public class ReporteRotacionResponse {
    private String id;
    private UUID categoriaId;
    private String codigo;
    private String nombre;
    private String unidadMedida;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stockActual;
    private Integer stockMinimo;
    private long totalMovimientos;
    private String tipo;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public UUID getCategoriaId() { return categoriaId; }
    public void setCategoriaId(UUID categoriaId) { this.categoriaId = categoriaId; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public long getTotalMovimientos() { return totalMovimientos; }
    public void setTotalMovimientos(long totalMovimientos) { this.totalMovimientos = totalMovimientos; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
