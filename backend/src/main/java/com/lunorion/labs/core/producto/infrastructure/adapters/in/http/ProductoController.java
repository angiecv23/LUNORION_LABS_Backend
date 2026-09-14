package com.lunorion.labs.core.producto.infrastructure.adapters.in.http;

import com.lunorion.labs.core.producto.application.dto.in.CreateProductoRequest;
import com.lunorion.labs.core.producto.application.dto.in.UpdateStockRequest;
import com.lunorion.labs.core.producto.application.dto.out.ProductoResponse;
import com.lunorion.labs.core.producto.application.dto.out.ReporteRotacionResponse;
import com.lunorion.labs.core.producto.domain.ports.in.IProductoCommandPort;
import com.lunorion.labs.core.producto.domain.ports.in.IProductoQueryPort;
import com.lunorion.labs.core.inventario.application.dto.out.MovimientoStockResponse;
import com.lunorion.labs.core.inventario.domain.ports.in.IMovimientoStockQueryPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final IProductoCommandPort commandService;
    private final IProductoQueryPort queryService;
    private final IMovimientoStockQueryPort movimientoQueryService;

    public ProductoController(IProductoCommandPort commandService,
                              IProductoQueryPort queryService,
                              IMovimientoStockQueryPort movimientoQueryService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.movimientoQueryService = movimientoQueryService;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> create(@RequestBody CreateProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> update(@PathVariable String id, @RequestBody CreateProductoRequest request) {
        return ResponseEntity.ok(commandService.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> findById(@PathVariable String id) {
        return queryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProductoResponse> findByCodigo(@PathVariable String codigo) {
        return queryService.findByCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoResponse>> findByCategoriaId(@PathVariable UUID categoriaId) {
        return ResponseEntity.ok(queryService.findByCategoriaId(categoriaId));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<ProductoResponse>> findByTenantId(@PathVariable String tenantId) {
        return ResponseEntity.ok(queryService.findByTenantId(tenantId));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(@PathVariable String id, @RequestBody UpdateStockRequest request) {
        commandService.updateStock(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/movimientos")
    public ResponseEntity<List<MovimientoStockResponse>> findMovimientosByProducto(@PathVariable String id) {
        return ResponseEntity.ok(movimientoQueryService.findByProductoId(id));
    }

    @GetMapping("/stock-alertas")
    public ResponseEntity<List<ProductoResponse>> findStockCritico(@RequestParam String tenantId) {
        return ResponseEntity.ok(queryService.findStockCritico(tenantId));
    }

    @GetMapping("/reporte-rotacion")
    public ResponseEntity<List<ReporteRotacionResponse>> reporteRotacion(@RequestParam String tenantId) {
        List<ProductoResponse> productos = queryService.reporteRotacion(tenantId);
        List<ReporteRotacionResponse> reporte = productos.stream().map(p -> {
            ReporteRotacionResponse item = new ReporteRotacionResponse();
            item.setId(p.getId());
            item.setCategoriaId(p.getCategoriaId());
            item.setCodigo(p.getCodigo());
            item.setNombre(p.getNombre());
            item.setUnidadMedida(p.getUnidadMedida());
            item.setPrecioCompra(p.getPrecioCompra());
            item.setPrecioVenta(p.getPrecioVenta());
            item.setStockActual(p.getStockActual());
            item.setStockMinimo(p.getStockMinimo());
            item.setTipo(p.getTipo());
            item.setTotalMovimientos(movimientoQueryService.findByProductoId(p.getId()).size());
            return item;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(reporte);
    }

    @PostMapping("/creacion-rapida")
    public ResponseEntity<ProductoResponse> quickCreate(@RequestBody CreateProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandService.quickCreate(request));
    }
}
