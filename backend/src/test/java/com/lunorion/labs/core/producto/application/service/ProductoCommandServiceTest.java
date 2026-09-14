package com.lunorion.labs.core.producto.application.service;

import com.lunorion.labs.core.producto.application.dto.in.CreateProductoRequest;
import com.lunorion.labs.core.producto.application.dto.in.UpdateStockRequest;
import com.lunorion.labs.core.producto.application.dto.out.ProductoResponse;
import com.lunorion.labs.core.producto.application.mapper.ProductoMapper;
import com.lunorion.labs.core.producto.application.service.command.ProductoCommandService;
import com.lunorion.labs.core.producto.domain.entity.Producto;
import com.lunorion.labs.core.producto.domain.ports.out.IProductoRepositoryPort;
import com.lunorion.labs.shared.test.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoCommandServiceTest {

    @Mock
    private IProductoRepositoryPort repository;

    private ProductoMapper mapper = new ProductoMapper();
    private ProductoCommandService service;

    @Captor
    private ArgumentCaptor<Producto> productCaptor;

    @BeforeEach
    void setUp() {
        service = new ProductoCommandService(repository, mapper);
    }

    @Test
    void deberiaCrearProducto() {
        CreateProductoRequest request = TestDataFactory.createProductoRequest();
        Producto producto = mapper.toDomain(request);
        when(repository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponse response = service.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo(request.getNombre());
        assertThat(response.getCodigo()).isEqualTo(request.getCodigo());
        verify(repository).save(any(Producto.class));
    }

    @Test
    void deberiaActualizarProducto() {
        String id = TestDataFactory.uniqueId();
        CreateProductoRequest request = TestDataFactory.createProductoRequest();
        Producto existente = TestDataFactory.createProducto();
        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any(Producto.class))).thenReturn(existente);

        ProductoResponse response = service.update(id, request);

        assertThat(response).isNotNull();
        assertThat(response.getNombre()).isEqualTo(existente.getNombre());
        verify(repository).findById(id);
        verify(repository).save(any(Producto.class));
    }

    @Test
    void deberiaCreacionRapidaConCodigoAutogenerado() {
        CreateProductoRequest request = TestDataFactory.createProductoRequest();
        request.setCodigo(null);
        Producto producto = mapper.toDomain(request);
        when(repository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponse response = service.quickCreate(request);

        assertThat(response).isNotNull();
        assertThat(request.getCodigo()).startsWith("Q-");
        verify(repository).save(any(Producto.class));
    }

    @Test
    void deberiaActualizarStock() {
        String id = TestDataFactory.uniqueId();
        int nuevoStock = 75;
        UpdateStockRequest request = TestDataFactory.updateStockRequest(nuevoStock);
        Producto producto = TestDataFactory.createProducto();
        when(repository.findById(id)).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class))).thenReturn(producto);

        service.updateStock(id, request);

        verify(repository).findById(id);
        verify(repository).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getStockActual()).isEqualTo(nuevoStock);
    }

    @Test
    void noDeberiaActualizarStock_CuandoProductoNoExiste() {
        String id = "id-inexistente";
        when(repository.findById(id)).thenReturn(Optional.empty());

        service.updateStock(id, TestDataFactory.updateStockRequest(10));

        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    void deberiaManejarCreacionMasiva() {
        CreateProductoRequest request = TestDataFactory.createProductoRequest();
        Producto producto = mapper.toDomain(request);
        when(repository.save(any(Producto.class))).thenReturn(producto);

        for (int i = 0; i < 100; i++) {
            service.create(request);
        }

        verify(repository, times(100)).save(any(Producto.class));
    }
}
