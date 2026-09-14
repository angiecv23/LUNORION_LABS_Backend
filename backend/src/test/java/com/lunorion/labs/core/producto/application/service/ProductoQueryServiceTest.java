package com.lunorion.labs.core.producto.application.service;

import com.lunorion.labs.core.producto.application.dto.out.ProductoResponse;
import com.lunorion.labs.core.producto.application.mapper.ProductoMapper;
import com.lunorion.labs.core.producto.application.service.query.ProductoQueryService;
import com.lunorion.labs.core.producto.domain.entity.Producto;
import com.lunorion.labs.core.producto.domain.ports.out.IProductoRepositoryPort;
import com.lunorion.labs.shared.test.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoQueryServiceTest {

    @Mock
    private IProductoRepositoryPort repository;

    private ProductoMapper mapper = new ProductoMapper();
    private ProductoQueryService service;

    @BeforeEach
    void setUp() {
        service = new ProductoQueryService(repository, mapper);
    }

    @Test
    void deberiaEncontrarProductoPorId() {
        Producto producto = TestDataFactory.createProducto();
        when(repository.findById(producto.getId().toString())).thenReturn(Optional.of(producto));

        Optional<ProductoResponse> result = service.findById(producto.getId().toString());

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo(producto.getNombre());
    }

    @Test
    void deberiaRetornarVacio_CuandoProductoNoExiste() {
        when(repository.findById("id-inexistente")).thenReturn(Optional.empty());

        Optional<ProductoResponse> result = service.findById("id-inexistente");

        assertThat(result).isNotPresent();
    }

    @Test
    void deberiaEncontrarProductoPorCodigo() {
        Producto producto = TestDataFactory.createProducto();
        when(repository.findByCodigo(producto.getCodigo())).thenReturn(Optional.of(producto));

        Optional<ProductoResponse> result = service.findByCodigo(producto.getCodigo());

        assertThat(result).isPresent();
        assertThat(result.get().getCodigo()).isEqualTo(producto.getCodigo());
    }

    @Test
    void deberiaListarProductosPorTenant() {
        String tenantId = TestDataFactory.tenantId();
        List<Producto> productos = TestDataFactory.createProductosMasivos(10, tenantId);
        when(repository.findByTenantId(tenantId)).thenReturn(productos);

        List<ProductoResponse> result = service.findByTenantId(tenantId);

        assertThat(result).hasSize(10);
        assertThat(result.get(0).getCodigo()).isEqualTo(productos.get(0).getCodigo());
    }

    @Test
    void deberiaListarProductosPorCategoria() {
        UUID categoriaId = UUID.randomUUID();
        Producto p1 = TestDataFactory.createProducto();
        Producto p2 = TestDataFactory.createProducto();
        when(repository.findByCategoriaId(categoriaId)).thenReturn(List.of(p1, p2));

        List<ProductoResponse> result = service.findByCategoriaId(categoriaId);

        assertThat(result).hasSize(2);
    }

    @Test
    void deberiaEncontrarStockCritico() {
        String tenantId = TestDataFactory.tenantId();
        Producto critico = TestDataFactory.createProducto(tenantId, 3, 10);
        when(repository.findStockCritico(tenantId)).thenReturn(List.of(critico));

        List<ProductoResponse> result = service.findStockCritico(tenantId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockActual()).isLessThan(result.get(0).getStockMinimo());
    }

    @Test
    void deberiaRetornarReporteRotacion() {
        String tenantId = TestDataFactory.tenantId();
        List<Producto> productos = TestDataFactory.createProductosMasivos(5, tenantId);
        when(repository.findByTenantId(tenantId)).thenReturn(productos);

        List<ProductoResponse> result = service.reporteRotacion(tenantId);

        assertThat(result).hasSize(5);
        assertThat(result.stream().map(ProductoResponse::getNombre).collect(Collectors.toList()))
                .contains(productos.get(0).getNombre(), productos.get(4).getNombre());
    }
}
