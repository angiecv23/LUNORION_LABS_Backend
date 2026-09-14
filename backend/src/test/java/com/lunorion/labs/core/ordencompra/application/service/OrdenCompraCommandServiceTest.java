package com.lunorion.labs.core.ordencompra.application.service;

import com.lunorion.labs.core.inventario.domain.entity.MovimientoStock;
import com.lunorion.labs.core.inventario.domain.ports.out.IMovimientoStockRepositoryPort;
import com.lunorion.labs.core.ordencompra.application.dto.in.CreateOrdenCompraRequest;
import com.lunorion.labs.core.ordencompra.application.dto.in.RecibirOrdenRequest;
import com.lunorion.labs.core.ordencompra.application.dto.out.OrdenCompraResponse;
import com.lunorion.labs.core.ordencompra.application.mapper.OrdenCompraMapper;
import com.lunorion.labs.core.ordencompra.application.service.command.OrdenCompraCommandService;
import com.lunorion.labs.core.ordencompra.domain.entity.OrdenCompra;
import com.lunorion.labs.core.ordencompra.domain.entity.OrdenCompraItem;
import com.lunorion.labs.core.ordencompra.domain.ports.in.IOrdenCompraCommandPort;
import com.lunorion.labs.core.ordencompra.domain.ports.out.IOrdenCompraItemRepositoryPort;
import com.lunorion.labs.core.ordencompra.domain.ports.out.IOrdenCompraRepositoryPort;
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
class OrdenCompraCommandServiceTest {

    @Mock
    private IOrdenCompraRepositoryPort repository;

    @Mock
    private IOrdenCompraItemRepositoryPort itemRepository;

    @Mock
    private IMovimientoStockRepositoryPort movimientoStockRepository;

    @Mock
    private IProductoRepositoryPort productRepository;

    private OrdenCompraMapper mapper = new OrdenCompraMapper();
    private OrdenCompraCommandService service;

    @Captor
    private ArgumentCaptor<MovimientoStock> movimientoCaptor;

    @BeforeEach
    void setUp() {
        service = new OrdenCompraCommandService(
                repository, itemRepository, mapper,
                movimientoStockRepository, productRepository);
    }

    @Test
    void deberiaCrearOrdenCompra() {
        CreateOrdenCompraRequest request = TestDataFactory.createOrdenCompraRequest();
        OrdenCompra orden = mapper.toDomain(request);
        when(repository.save(any(OrdenCompra.class))).thenReturn(orden);

        OrdenCompraResponse response = service.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getNumeroOrden()).isEqualTo(request.getNumeroOrden());
        verify(repository).save(any(OrdenCompra.class));
    }

    @Test
    void deberiaAprobarOrdenCompra() {
        String id = TestDataFactory.uniqueId();
        OrdenCompra orden = TestDataFactory.createOrdenCompra(TestDataFactory.tenantId(), "PENDIENTE");
        when(repository.findById(id)).thenReturn(Optional.of(orden));
        when(repository.save(any(OrdenCompra.class))).thenReturn(orden);

        service.aprobar(id);

        assertThat(orden.getEstado()).isEqualTo("APROBADA");
        verify(repository).save(orden);
    }

    @Test
    void deberiaCompletarOrdenCompra() {
        String id = TestDataFactory.uniqueId();
        OrdenCompra orden = TestDataFactory.createOrdenCompra(TestDataFactory.tenantId(), "APROBADA");
        when(repository.findById(id)).thenReturn(Optional.of(orden));
        when(repository.save(any(OrdenCompra.class))).thenReturn(orden);

        service.completar(id);

        assertThat(orden.getEstado()).isEqualTo("COMPLETADA");
    }

    @Test
    void deberiaAnularOrdenCompra() {
        String id = TestDataFactory.uniqueId();
        OrdenCompra orden = TestDataFactory.createOrdenCompra(TestDataFactory.tenantId(), "PENDIENTE");
        when(repository.findById(id)).thenReturn(Optional.of(orden));
        when(repository.save(any(OrdenCompra.class))).thenReturn(orden);

        service.anular(id);

        assertThat(orden.getEstado()).isEqualTo("ANULADA");
    }

    @Test
    void deberiaRecibirOrdenCompra_CreandoMovimientoYActualizandoStock() {
        String tenantId = TestDataFactory.tenantId();
        String productoId = TestDataFactory.uniqueId();
        OrdenCompra orden = TestDataFactory.createOrdenCompra(tenantId, "APROBADA");
        String ordenId = orden.getId().toString();
        String itemId = TestDataFactory.uniqueId();

        OrdenCompraItem item = TestDataFactory.createOrdenCompraItem(
                ordenId, productoId, BigDecimal.valueOf(10), BigDecimal.valueOf(50));

        Producto producto = TestDataFactory.createProducto(tenantId, 100, 10);

        when(repository.findById(ordenId)).thenReturn(Optional.of(orden));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(productRepository.findById(productoId)).thenReturn(Optional.of(producto));
        when(movimientoStockRepository.save(any(MovimientoStock.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.save(any(OrdenCompra.class))).thenReturn(orden);

        RecibirOrdenRequest request = TestDataFactory.createRecibirOrdenRequest(itemId, 5);

        OrdenCompraResponse response = service.recibir(ordenId, request);

        assertThat(response).isNotNull();
        assertThat(response.getEstado()).isEqualTo("COMPLETADA");

        verify(movimientoStockRepository).save(movimientoCaptor.capture());
        MovimientoStock mov = movimientoCaptor.getValue();
        assertThat(mov.getTipo()).isEqualTo("INGRESO");
        assertThat(mov.getSubtipo()).isEqualTo("COMPRA");
        assertThat(mov.getCantidad()).isEqualByComparingTo(BigDecimal.valueOf(5));
        assertThat(mov.getStockAnterior()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(mov.getStockPosterior()).isEqualByComparingTo(BigDecimal.valueOf(105));
        assertThat(mov.getProductoId()).isEqualTo(productoId);

        verify(productRepository).save(any(Producto.class));
        verify(itemRepository).save(item);
        assertThat(item.getCantidadRecibida()).isEqualByComparingTo(BigDecimal.valueOf(5));
    }

    @Test
    void deberiaLanzarExcepcion_CuandoOrdenNoExisteAlRecibir() {
        String id = "id-inexistente";
        when(repository.findById(id)).thenReturn(Optional.empty());

        RecibirOrdenRequest request = TestDataFactory.createRecibirOrdenRequest(
                TestDataFactory.uniqueId(), 5);

        assertThrows(RuntimeException.class, () -> service.recibir(id, request));
        verify(movimientoStockRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }
}
