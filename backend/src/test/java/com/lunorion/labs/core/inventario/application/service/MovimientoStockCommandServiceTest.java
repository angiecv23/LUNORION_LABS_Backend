package com.lunorion.labs.core.inventario.application.service;

import com.lunorion.labs.core.inventario.application.dto.in.CreateMovimientoStockRequest;
import com.lunorion.labs.core.inventario.application.dto.out.MovimientoStockResponse;
import com.lunorion.labs.core.inventario.application.mapper.MovimientoStockMapper;
import com.lunorion.labs.core.inventario.application.service.command.MovimientoStockCommandService;
import com.lunorion.labs.core.inventario.domain.entity.MovimientoStock;
import com.lunorion.labs.core.inventario.domain.ports.out.IMovimientoStockRepositoryPort;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoStockCommandServiceTest {

    @Mock
    private IMovimientoStockRepositoryPort repository;

    @Mock
    private IProductoRepositoryPort productRepository;

    private MovimientoStockMapper mapper = new MovimientoStockMapper();
    private MovimientoStockCommandService service;

    @Captor
    private ArgumentCaptor<MovimientoStock> movimientoCaptor;

    @Captor
    private ArgumentCaptor<Producto> productCaptor;

    @BeforeEach
    void setUp() {
        service = new MovimientoStockCommandService(repository, productRepository, mapper);
    }

    @Test
    void deberiaCrearMovimientoIngreso() {
        String tenantId = TestDataFactory.tenantId();
        Producto producto = TestDataFactory.createProducto(tenantId, 50, 10);
        String productoId = producto.getId().toString();

        when(productRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(repository.save(any(MovimientoStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.save(any(Producto.class))).thenReturn(producto);

        CreateMovimientoStockRequest request = TestDataFactory.createMovimientoStockRequest(
                tenantId, productoId, "INGRESO", 25, 50);

        MovimientoStockResponse response = service.create(request);

        assertThat(response).isNotNull();
        verify(repository).save(movimientoCaptor.capture());
        verify(productRepository).save(productCaptor.capture());

        MovimientoStock savedMov = movimientoCaptor.getValue();
        assertThat(savedMov.getStockAnterior()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(savedMov.getStockPosterior()).isEqualByComparingTo(BigDecimal.valueOf(75));

        Producto savedProduct = productCaptor.getValue();
        assertThat(savedProduct.getStockActual()).isEqualTo(75);
    }

    @Test
    void deberiaCrearMovimientoSalida() {
        String tenantId = TestDataFactory.tenantId();
        Producto producto = TestDataFactory.createProducto(tenantId, 100, 10);
        String productoId = producto.getId().toString();

        when(productRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(repository.save(any(MovimientoStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.save(any(Producto.class))).thenReturn(producto);

        CreateMovimientoStockRequest request = TestDataFactory.createMovimientoStockRequest(
                tenantId, productoId, "SALIDA", 30, 100);

        MovimientoStockResponse response = service.create(request);

        assertThat(response).isNotNull();
        verify(repository).save(movimientoCaptor.capture());
        verify(productRepository).save(productCaptor.capture());

        MovimientoStock savedMov = movimientoCaptor.getValue();
        assertThat(savedMov.getStockAnterior()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(savedMov.getStockPosterior()).isEqualByComparingTo(BigDecimal.valueOf(70));

        Producto savedProduct = productCaptor.getValue();
        assertThat(savedProduct.getStockActual()).isEqualTo(70);
    }

    @Test
    void noDeberiaCambiarStock_CuandoTipoDesconocido() {
        String tenantId = TestDataFactory.tenantId();
        Producto producto = TestDataFactory.createProducto(tenantId, 50, 10);
        String productoId = producto.getId().toString();

        when(productRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(repository.save(any(MovimientoStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.save(any(Producto.class))).thenReturn(producto);

        CreateMovimientoStockRequest request = TestDataFactory.createMovimientoStockRequest(
                tenantId, productoId, "TRANSFERENCIA", 10, 50);

        MovimientoStockResponse response = service.create(request);

        assertThat(response).isNotNull();
        verify(productRepository).save(productCaptor.capture());
        assertThat(productCaptor.getValue().getStockActual()).isEqualTo(50);
    }

    @Test
    void deberiaLanzarExcepcion_CuandoProductoNoExiste() {
        String productoId = "id-inexistente";
        when(productRepository.findById(productoId)).thenReturn(Optional.empty());

        CreateMovimientoStockRequest request = TestDataFactory.createMovimientoStockRequest(
                TestDataFactory.tenantId(), productoId, "INGRESO", 10, 0);

        assertThrows(RuntimeException.class, () -> service.create(request));
        verify(repository, never()).save(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void deberiaEliminarMovimiento() {
        String id = TestDataFactory.uniqueId();
        doNothing().when(repository).deleteById(id);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    void deberiaManejarMovimientosConsecutivos() {
        String tenantId = TestDataFactory.tenantId();
        Producto producto = TestDataFactory.createProducto(tenantId, 100, 10);
        String productoId = producto.getId().toString();

        when(productRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(repository.save(any(MovimientoStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.save(any(Producto.class))).thenReturn(producto);

        service.create(TestDataFactory.createMovimientoStockRequest(tenantId, productoId, "INGRESO", 50, 100));
        producto.updateStock(150);
        service.create(TestDataFactory.createMovimientoStockRequest(tenantId, productoId, "SALIDA", 30, 150));
        producto.updateStock(120);
        service.create(TestDataFactory.createMovimientoStockRequest(tenantId, productoId, "INGRESO", 20, 120));

        verify(repository, times(3)).save(any(MovimientoStock.class));
        verify(productRepository, times(3)).save(any(Producto.class));
    }
}
