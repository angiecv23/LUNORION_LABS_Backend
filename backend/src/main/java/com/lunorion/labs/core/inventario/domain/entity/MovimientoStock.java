package com.lunorion.labs.core.inventario.domain.entity;

import com.lunorion.labs.shared.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.UUID;

public class MovimientoStock extends BaseEntity {
    private String tenantId;
    private String productoId;
    private String tipo;
    private String subtipo;
    private BigDecimal cantidad;
    private BigDecimal costoUnitario;
    private BigDecimal stockAnterior;
    private BigDecimal stockPosterior;
    private String documentoOrigen;
    private String observacion;
    private String usuarioId;

    public MovimientoStock() {}

    public MovimientoStock(UUID id, String tenantId, String productoId, String tipo, String subtipo,
                           BigDecimal cantidad, BigDecimal costoUnitario, BigDecimal stockAnterior,
                           BigDecimal stockPosterior, String documentoOrigen, String observacion, String usuarioId) {
        super(id);
        this.tenantId = tenantId;
        this.productoId = productoId;
        this.tipo = tipo;
        this.subtipo = subtipo;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
        this.stockAnterior = stockAnterior;
        this.stockPosterior = stockPosterior;
        this.documentoOrigen = documentoOrigen;
        this.observacion = observacion;
        this.usuarioId = usuarioId;
    }

    public static MovimientoStock create(String tenantId, String productoId, String tipo, String subtipo,
                                          BigDecimal cantidad, BigDecimal costoUnitario,
                                          BigDecimal stockAnterior, BigDecimal stockPosterior,
                                          String documentoOrigen, String observacion, String usuarioId) {
        return new MovimientoStock(UUID.randomUUID(), tenantId, productoId, tipo, subtipo,
                cantidad, costoUnitario, stockAnterior, stockPosterior,
                documentoOrigen, observacion, usuarioId);
    }

    public String getTenantId() { return tenantId; }
    public String getProductoId() { return productoId; }
    public String getTipo() { return tipo; }
    public String getSubtipo() { return subtipo; }
    public BigDecimal getCantidad() { return cantidad; }
    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public BigDecimal getStockAnterior() { return stockAnterior; }
    public BigDecimal getStockPosterior() { return stockPosterior; }
    public String getDocumentoOrigen() { return documentoOrigen; }
    public String getObservacion() { return observacion; }
    public String getUsuarioId() { return usuarioId; }
    public void setStockAnterior(BigDecimal stockAnterior) { this.stockAnterior = stockAnterior; markUpdated(); }
    public void setStockPosterior(BigDecimal stockPosterior) { this.stockPosterior = stockPosterior; markUpdated(); }
}
