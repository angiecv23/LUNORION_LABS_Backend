package com.lunorion.labs.core.producto.domain.ports.in;

import com.lunorion.labs.core.producto.application.dto.in.CreateCategoriaRequest;
import com.lunorion.labs.core.producto.application.dto.in.CreateProductoRequest;
import com.lunorion.labs.core.producto.application.dto.in.UpdateStockRequest;
import com.lunorion.labs.core.producto.application.dto.out.CategoriaResponse;
import com.lunorion.labs.core.producto.application.dto.out.ProductoResponse;

public interface IProductoCommandPort {
    ProductoResponse create(CreateProductoRequest request);
    ProductoResponse update(String id, CreateProductoRequest request);
    ProductoResponse quickCreate(CreateProductoRequest request);
    void updateStock(String id, UpdateStockRequest request);
    CategoriaResponse createCategoria(CreateCategoriaRequest request);
}
