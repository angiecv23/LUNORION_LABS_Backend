package com.lunorion.labs.core.producto.application.service.command;

import com.lunorion.labs.core.producto.application.dto.in.CreateCategoriaRequest;
import com.lunorion.labs.core.producto.application.dto.in.CreateProductoRequest;
import com.lunorion.labs.core.producto.application.dto.in.UpdateStockRequest;
import com.lunorion.labs.core.producto.application.dto.out.CategoriaResponse;
import com.lunorion.labs.core.producto.application.dto.out.ProductoResponse;
import com.lunorion.labs.core.producto.application.mapper.ProductoMapper;
import com.lunorion.labs.core.producto.domain.entity.CategoriaProducto;
import com.lunorion.labs.core.producto.domain.entity.Producto;
import com.lunorion.labs.core.producto.domain.ports.in.IProductoCommandPort;
import com.lunorion.labs.core.producto.domain.ports.out.IProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductoCommandService implements IProductoCommandPort {

    private final IProductoRepositoryPort repository;
    private final ProductoMapper mapper;

    public ProductoCommandService(IProductoRepositoryPort repository, ProductoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ProductoResponse create(CreateProductoRequest request) {
        Producto producto = mapper.toDomain(request);
        Producto saved = repository.save(producto);
        return mapper.toResponse(saved);
    }

    @Override
    public ProductoResponse update(String id, CreateProductoRequest request) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto not found with id: " + id));
        producto.actualizar(
            request.getCategoriaId(),
            request.getCodigo(),
            request.getCodigoBarra(),
            request.getNombre(),
            request.getDescripcion(),
            request.getMarca(),
            request.getModelo(),
            request.getUnidadMedida(),
            request.getPrecioCompra(),
            request.getPrecioVenta(),
            request.getStockActual(),
            request.getStockMinimo(),
            request.getUbicacion(),
            request.getTipo()
        );
        Producto saved = repository.save(producto);
        return mapper.toResponse(saved);
    }

    @Override
    public ProductoResponse quickCreate(CreateProductoRequest request) {
        if (request.getCodigo() == null || request.getCodigo().isBlank()) {
            request.setCodigo("Q-" + System.currentTimeMillis());
        }
        if (request.getStockActual() == null) {
            request.setStockActual(0);
        }
        if (request.getStockMinimo() == null) {
            request.setStockMinimo(0);
        }
        Producto producto = mapper.toDomain(request);
        Producto saved = repository.save(producto);
        return mapper.toResponse(saved);
    }

    @Override
    public void updateStock(String id, UpdateStockRequest request) {
        repository.findById(id).ifPresent(producto -> {
            producto.updateStock(request.getCantidad());
            repository.save(producto);
        });
    }

    @Override
    public CategoriaResponse createCategoria(CreateCategoriaRequest request) {
        CategoriaProducto categoria = mapper.toCategoriaDomain(request);
        CategoriaProducto saved = repository.saveCategoria(categoria);
        return mapper.toCategoriaResponse(saved);
    }
}
