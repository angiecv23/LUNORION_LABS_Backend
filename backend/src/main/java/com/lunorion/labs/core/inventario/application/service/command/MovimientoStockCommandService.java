package com.lunorion.labs.core.inventario.application.service.command;

import com.lunorion.labs.core.inventario.application.dto.in.CreateMovimientoStockRequest;
import com.lunorion.labs.core.inventario.application.dto.out.MovimientoStockResponse;
import com.lunorion.labs.core.inventario.application.mapper.MovimientoStockMapper;
import com.lunorion.labs.core.inventario.domain.entity.MovimientoStock;
import com.lunorion.labs.core.inventario.domain.ports.in.IMovimientoStockCommandPort;
import com.lunorion.labs.core.inventario.domain.ports.out.IMovimientoStockRepositoryPort;
import com.lunorion.labs.core.producto.domain.entity.Producto;
import com.lunorion.labs.core.producto.domain.ports.out.IProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class MovimientoStockCommandService implements IMovimientoStockCommandPort {

    private final IMovimientoStockRepositoryPort repository;
    private final IProductoRepositoryPort productRepository;
    private final MovimientoStockMapper mapper;

    public MovimientoStockCommandService(IMovimientoStockRepositoryPort repository,
                                          IProductoRepositoryPort productRepository,
                                          MovimientoStockMapper mapper) {
        this.repository = repository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    public MovimientoStockResponse create(CreateMovimientoStockRequest request) {
        Producto producto = productRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + request.getProductoId()));

        BigDecimal stockAnterior = BigDecimal.valueOf(
                producto.getStockActual() != null ? producto.getStockActual() : 0);
        BigDecimal cantidad = request.getCantidad() != null ? request.getCantidad() : BigDecimal.ZERO;
        BigDecimal stockPosterior;

        if ("INGRESO".equalsIgnoreCase(request.getTipo())) {
            stockPosterior = stockAnterior.add(cantidad);
        } else if ("SALIDA".equalsIgnoreCase(request.getTipo())) {
            stockPosterior = stockAnterior.subtract(cantidad);
        } else {
            stockPosterior = stockAnterior;
        }

        MovimientoStock movimiento = mapper.toDomain(request);
        movimiento.setStockAnterior(stockAnterior);
        movimiento.setStockPosterior(stockPosterior);
        MovimientoStock saved = repository.save(movimiento);

        producto.updateStock(stockPosterior.intValue());
        productRepository.save(producto);

        return mapper.toResponse(saved);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}
