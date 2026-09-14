package com.lunorion.labs.shared.test;

import com.lunorion.labs.core.inventario.application.dto.in.CreateMovimientoStockRequest;
import com.lunorion.labs.core.inventario.application.dto.out.MovimientoStockResponse;
import com.lunorion.labs.core.inventario.domain.entity.MovimientoStock;
import com.lunorion.labs.core.ordencompra.application.dto.in.CreateOrdenCompraRequest;
import com.lunorion.labs.core.ordencompra.application.dto.in.RecibirOrdenRequest;
import com.lunorion.labs.core.ordencompra.application.dto.out.OrdenCompraResponse;
import com.lunorion.labs.core.ordencompra.domain.entity.OrdenCompra;
import com.lunorion.labs.core.ordencompra.domain.entity.OrdenCompraItem;
import com.lunorion.labs.core.producto.application.dto.in.CreateProductoRequest;
import com.lunorion.labs.core.producto.application.dto.in.UpdateStockRequest;
import com.lunorion.labs.core.producto.application.dto.out.ProductoResponse;
import com.lunorion.labs.core.producto.domain.entity.Producto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TestDataFactory {

    private static final AtomicLong counter = new AtomicLong(0);

    public static String uniqueId() {
        return UUID.randomUUID().toString();
    }

    public static String tenantId() {
        return "tenant-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static Producto createProducto(String tenantId, int stockActual, int stockMinimo) {
        long n = counter.incrementAndGet();
        return Producto.create(
                tenantId,
                UUID.randomUUID(),
                "PROD-" + n,
                "BARRA-" + n,
                "Producto Test " + n,
                "Descripcion test " + n,
                "Marca Test",
                "Modelo X",
                "UNIDAD",
                BigDecimal.valueOf(10.50 + n),
                BigDecimal.valueOf(25.99 + n),
                stockActual,
                stockMinimo,
                "A-1",
                "REPUESTO"
        );
    }

    public static Producto createProducto() {
        return createProducto(tenantId(), 100, 10);
    }

    public static CreateProductoRequest createProductoRequest() {
        long n = counter.incrementAndGet();
        CreateProductoRequest req = new CreateProductoRequest();
        req.setTenantId(tenantId());
        req.setCategoriaId(UUID.randomUUID());
        req.setCodigo("PROD-" + n);
        req.setCodigoBarra("BARRA-" + n);
        req.setNombre("Producto Request " + n);
        req.setDescripcion("Desc request " + n);
        req.setMarca("Marca");
        req.setModelo("Modelo");
        req.setUnidadMedida("UNIDAD");
        req.setPrecioCompra(BigDecimal.valueOf(15.00));
        req.setPrecioVenta(BigDecimal.valueOf(35.00));
        req.setStockActual(50);
        req.setStockMinimo(5);
        req.setUbicacion("B-2");
        req.setTipo("REPUESTO");
        return req;
    }

    public static ProductoResponse createProductoResponse() {
        Producto p = createProducto();
        ProductoResponse r = new ProductoResponse();
        r.setId(p.getId().toString());
        r.setCategoriaId(p.getCategoriaId());
        r.setCodigo(p.getCodigo());
        r.setNombre(p.getNombre());
        r.setStockActual(p.getStockActual());
        r.setStockMinimo(p.getStockMinimo());
        r.setPrecioCompra(p.getPrecioCompra());
        r.setPrecioVenta(p.getPrecioVenta());
        r.setActivo(p.isActivo());
        return r;
    }

    public static UpdateStockRequest updateStockRequest(int cantidad) {
        UpdateStockRequest req = new UpdateStockRequest();
        req.setCantidad(cantidad);
        return req;
    }

    public static MovimientoStock createMovimientoStock(String tenantId, String productoId,
                                                         String tipo, BigDecimal cantidad,
                                                         BigDecimal stockAnterior) {
        BigDecimal stockPosterior = "INGRESO".equalsIgnoreCase(tipo)
                ? stockAnterior.add(cantidad)
                : stockAnterior.subtract(cantidad);
        return MovimientoStock.create(
                tenantId, productoId, tipo, "AJUSTE",
                cantidad, BigDecimal.valueOf(20.00),
                stockAnterior, stockPosterior,
                "DOC-" + UUID.randomUUID().toString().substring(0, 6),
                "Movimiento test", "user-" + UUID.randomUUID().toString().substring(0, 6)
        );
    }

    public static CreateMovimientoStockRequest createMovimientoStockRequest(
            String tenantId, String productoId, String tipo, int cantidad, int stockAnterior) {
        CreateMovimientoStockRequest req = new CreateMovimientoStockRequest();
        req.setTenantId(tenantId);
        req.setProductoId(productoId);
        req.setTipo(tipo);
        req.setSubtipo("AJUSTE");
        req.setCantidad(BigDecimal.valueOf(cantidad));
        req.setCostoUnitario(BigDecimal.valueOf(20.00));
        req.setStockAnterior(BigDecimal.valueOf(stockAnterior));
        req.setStockPosterior(BigDecimal.valueOf(stockAnterior));
        req.setDocumentoOrigen("DOC-" + UUID.randomUUID().toString().substring(0, 6));
        req.setObservacion("Movimiento test");
        req.setUsuarioId("user-" + UUID.randomUUID().toString().substring(0, 6));
        return req;
    }

    public static MovimientoStockResponse createMovimientoStockResponse() {
        MovimientoStockResponse r = new MovimientoStockResponse();
        r.setId(uniqueId());
        r.setTenantId(tenantId());
        r.setProductoId(uniqueId());
        r.setTipo("INGRESO");
        r.setSubtipo("COMPRA");
        r.setCantidad(BigDecimal.TEN);
        r.setCostoUnitario(BigDecimal.valueOf(20));
        r.setStockAnterior(BigDecimal.valueOf(50));
        r.setStockPosterior(BigDecimal.valueOf(60));
        r.setDocumentoOrigen("DOC-001");
        r.setObservacion("Test");
        r.setUsuarioId("user-001");
        return r;
    }

    public static OrdenCompra createOrdenCompra(String tenantId, String estado) {
        long n = counter.incrementAndGet();
        OrdenCompra oc = OrdenCompra.create(
                tenantId,
                uniqueId(),
                "OC-" + n,
                LocalDate.now(),
                estado,
                BigDecimal.valueOf(500.00 * n),
                "Observacion test",
                uniqueId()
        );
        return oc;
    }

    public static OrdenCompraResponse createOrdenCompraResponse(String estado) {
        OrdenCompra oc = createOrdenCompra(tenantId(), estado);
        OrdenCompraResponse r = new OrdenCompraResponse();
        r.setId(oc.getId().toString());
        r.setTenantId(oc.getTenantId());
        r.setProveedorId(oc.getProveedorId());
        r.setNumeroOrden(oc.getNumeroOrden());
        r.setFechaEmision(oc.getFechaEmision());
        r.setEstado(oc.getEstado());
        r.setTotal(oc.getTotal());
        r.setObservacion(oc.getObservacion());
        r.setUsuarioId(oc.getUsuarioId());
        return r;
    }

    public static CreateOrdenCompraRequest createOrdenCompraRequest() {
        long n = counter.incrementAndGet();
        CreateOrdenCompraRequest req = new CreateOrdenCompraRequest();
        req.setTenantId(tenantId());
        req.setProveedorId(uniqueId());
        req.setNumeroOrden("OC-" + n);
        req.setFechaEmision(LocalDate.now());
        req.setEstado("PENDIENTE");
        req.setTotal(BigDecimal.valueOf(1000.00));
        req.setObservacion("Orden test " + n);
        req.setUsuarioId(uniqueId());
        return req;
    }

    public static OrdenCompraItem createOrdenCompraItem(String ordenCompraId, String productoId,
                                                          BigDecimal cantidad, BigDecimal precioUnitario) {
        return OrdenCompraItem.create(
                ordenCompraId, productoId, cantidad, BigDecimal.ZERO,
                precioUnitario, cantidad.multiply(precioUnitario)
        );
    }

    public static RecibirOrdenRequest createRecibirOrdenRequest(String itemId, int cantidadRecibida) {
        RecibirOrdenRequest req = new RecibirOrdenRequest();
        RecibirOrdenRequest.RecibirItem item = new RecibirOrdenRequest.RecibirItem();
        item.setItemId(itemId);
        item.setCantidadRecibida(BigDecimal.valueOf(cantidadRecibida));
        req.setItems(List.of(item));
        return req;
    }

    public static List<Producto> createProductosMasivos(int cantidad, String tenantId) {
        List<Producto> lista = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            lista.add(createProducto(tenantId, 50 + i, 5));
        }
        return lista;
    }

    public static String randomCodigo() {
        return "CODE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
