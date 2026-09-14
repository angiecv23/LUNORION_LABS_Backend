package com.lunorion.labs.core.inventario.application.service;

import com.lunorion.labs.core.inventario.application.dto.out.MovimientoStockResponse;
import com.lunorion.labs.core.inventario.application.mapper.MovimientoStockMapper;
import com.lunorion.labs.core.inventario.application.service.query.MovimientoStockQueryService;
import com.lunorion.labs.core.inventario.domain.entity.MovimientoStock;
import com.lunorion.labs.core.inventario.domain.ports.out.IMovimientoStockRepositoryPort;
import com.lunorion.labs.shared.test.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimientoStockQueryServiceTest {

    @Mock
    private IMovimientoStockRepositoryPort repository;

    private MovimientoStockMapper mapper = new MovimientoStockMapper();
    private MovimientoStockQueryService service;

    @BeforeEach
    void setUp() {
        service = new MovimientoStockQueryService(repository, mapper);
    }

    @Test
    void deberiaEncontrarMovimientoPorId() {
        MovimientoStock mov = TestDataFactory.createMovimientoStock(
                TestDataFactory.tenantId(), TestDataFactory.uniqueId(),
                "INGRESO", BigDecimal.TEN, BigDecimal.valueOf(50));
        when(repository.findById(mov.getId().toString())).thenReturn(Optional.of(mov));

        Optional<MovimientoStockResponse> result = service.findById(mov.getId().toString());

        assertThat(result).isPresent();
        assertThat(result.get().getTipo()).isEqualTo("INGRESO");
    }

    @Test
    void deberiaRetornarVacio_CuandoMovimientoNoExiste() {
        when(repository.findById("id-inexistente")).thenReturn(Optional.empty());

        Optional<MovimientoStockResponse> result = service.findById("id-inexistente");

        assertThat(result).isNotPresent();
    }

    @Test
    void deberiaListarMovimientosPorProducto() {
        String productoId = TestDataFactory.uniqueId();
        String tenantId = TestDataFactory.tenantId();
        MovimientoStock m1 = TestDataFactory.createMovimientoStock(tenantId, productoId, "INGRESO", BigDecimal.valueOf(10), BigDecimal.valueOf(50));
        MovimientoStock m2 = TestDataFactory.createMovimientoStock(tenantId, productoId, "SALIDA", BigDecimal.valueOf(5), BigDecimal.valueOf(60));
        when(repository.findByProductoId(productoId)).thenReturn(List.of(m1, m2));

        List<MovimientoStockResponse> result = service.findByProductoId(productoId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProductoId()).isEqualTo(productoId);
    }

    @Test
    void deberiaListarMovimientosPorTenant() {
        String tenantId = TestDataFactory.tenantId();
        MovimientoStock m1 = TestDataFactory.createMovimientoStock(tenantId, TestDataFactory.uniqueId(), "INGRESO", BigDecimal.valueOf(10), BigDecimal.valueOf(50));
        MovimientoStock m2 = TestDataFactory.createMovimientoStock(tenantId, TestDataFactory.uniqueId(), "INGRESO", BigDecimal.valueOf(20), BigDecimal.valueOf(30));
        when(repository.findByTenantId(tenantId)).thenReturn(List.of(m1, m2));

        List<MovimientoStockResponse> result = service.findByTenantId(tenantId);

        assertThat(result).hasSize(2);
    }

    @Test
    void deberiaListarTodosLosMovimientos() {
        MovimientoStock m1 = TestDataFactory.createMovimientoStock(TestDataFactory.tenantId(), TestDataFactory.uniqueId(), "INGRESO", BigDecimal.valueOf(10), BigDecimal.valueOf(50));
        MovimientoStock m2 = TestDataFactory.createMovimientoStock(TestDataFactory.tenantId(), TestDataFactory.uniqueId(), "SALIDA", BigDecimal.valueOf(5), BigDecimal.valueOf(60));
        when(repository.findAll()).thenReturn(List.of(m1, m2));

        List<MovimientoStockResponse> result = service.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void deberiaRetornarListaVacia_CuandoNoHayMovimientos() {
        when(repository.findAll()).thenReturn(List.of());

        List<MovimientoStockResponse> result = service.findAll();

        assertThat(result).isEmpty();
    }
}
