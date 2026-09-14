package com.lunorion.labs.core.ordencompra.application.service;

import com.lunorion.labs.core.ordencompra.application.dto.out.OrdenCompraItemResponse;
import com.lunorion.labs.core.ordencompra.application.dto.out.OrdenCompraResponse;
import com.lunorion.labs.core.ordencompra.application.dto.out.ReporteGastosResponse;
import com.lunorion.labs.core.ordencompra.application.mapper.OrdenCompraMapper;
import com.lunorion.labs.core.ordencompra.application.service.query.OrdenCompraQueryService;
import com.lunorion.labs.core.ordencompra.domain.entity.OrdenCompra;
import com.lunorion.labs.core.ordencompra.domain.entity.OrdenCompraItem;
import com.lunorion.labs.core.ordencompra.domain.ports.out.IOrdenCompraItemRepositoryPort;
import com.lunorion.labs.core.ordencompra.domain.ports.out.IOrdenCompraRepositoryPort;
import com.lunorion.labs.shared.test.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdenCompraQueryServiceTest {

    @Mock
    private IOrdenCompraRepositoryPort repository;

    @Mock
    private IOrdenCompraItemRepositoryPort itemRepository;

    private OrdenCompraMapper mapper = new OrdenCompraMapper();
    private OrdenCompraQueryService service;

    @BeforeEach
    void setUp() {
        service = new OrdenCompraQueryService(repository, itemRepository, mapper);
    }

    @Test
    void deberiaEncontrarOrdenPorId() {
        OrdenCompra orden = TestDataFactory.createOrdenCompra(TestDataFactory.tenantId(), "PENDIENTE");
        when(repository.findById(orden.getId().toString())).thenReturn(Optional.of(orden));

        Optional<OrdenCompraResponse> result = service.findById(orden.getId().toString());

        assertThat(result).isPresent();
        assertThat(result.get().getNumeroOrden()).isEqualTo(orden.getNumeroOrden());
    }

    @Test
    void deberiaRetornarVacio_CuandoOrdenNoExiste() {
        when(repository.findById("id-inexistente")).thenReturn(Optional.empty());

        Optional<OrdenCompraResponse> result = service.findById("id-inexistente");

        assertThat(result).isNotPresent();
    }

    @Test
    void deberiaListarOrdenesPorTenant() {
        String tenantId = TestDataFactory.tenantId();
        OrdenCompra o1 = TestDataFactory.createOrdenCompra(tenantId, "PENDIENTE");
        OrdenCompra o2 = TestDataFactory.createOrdenCompra(tenantId, "APROBADA");
        when(repository.findByTenantId(tenantId)).thenReturn(List.of(o1, o2));

        List<OrdenCompraResponse> result = service.findByTenantId(tenantId);

        assertThat(result).hasSize(2);
    }

    @Test
    void deberiaListarOrdenesPorProveedor() {
        String proveedorId = TestDataFactory.uniqueId();
        OrdenCompra o1 = TestDataFactory.createOrdenCompra(TestDataFactory.tenantId(), "PENDIENTE");
        when(repository.findByProveedorId(proveedorId)).thenReturn(List.of(o1));

        List<OrdenCompraResponse> result = service.findByProveedorId(proveedorId);

        assertThat(result).hasSize(1);
    }

    @Test
    void deberiaListarItemsDeOrden() {
        String ordenId = TestDataFactory.uniqueId();
        OrdenCompraItem item = TestDataFactory.createOrdenCompraItem(
                ordenId, TestDataFactory.uniqueId(), BigDecimal.valueOf(10), BigDecimal.valueOf(50));
        when(itemRepository.findByOrdenCompraId(ordenId)).thenReturn(List.of(item));

        List<OrdenCompraItemResponse> result = service.findItemsByOrdenCompraId(ordenId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrdenCompraId()).isEqualTo(ordenId);
    }

    @Test
    void deberiaGenerarReporteGastos() {
        String tenantId = TestDataFactory.tenantId();
        LocalDate desde = LocalDate.now().minusDays(30);
        LocalDate hasta = LocalDate.now().plusDays(1);
        OrdenCompra o1 = TestDataFactory.createOrdenCompra(tenantId, "COMPLETADA");
        OrdenCompra o2 = TestDataFactory.createOrdenCompra(tenantId, "APROBADA");
        when(repository.findByTenantId(tenantId)).thenReturn(List.of(o1, o2));

        ReporteGastosResponse reporte = service.spendingReport(tenantId, desde, hasta);

        assertThat(reporte).isNotNull();
        assertThat(reporte.getTenantId()).isEqualTo(tenantId);
        assertThat(reporte.getDesde()).isEqualTo(desde);
        assertThat(reporte.getHasta()).isEqualTo(hasta);
        assertThat(reporte.getGastos()).hasSize(2);
        assertThat(reporte.getTotalGastos()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void reporteGastosDeberiaFiltrarPorRango() {
        String tenantId = TestDataFactory.tenantId();
        OrdenCompra antigua = TestDataFactory.createOrdenCompra(tenantId, "COMPLETADA");
        OrdenCompra reciente = TestDataFactory.createOrdenCompra(tenantId, "COMPLETADA");
        when(repository.findByTenantId(tenantId)).thenReturn(List.of(antigua, reciente));

        LocalDate desde = LocalDate.now().minusDays(1);
        LocalDate hasta = LocalDate.now().plusDays(1);

        ReporteGastosResponse reporte = service.spendingReport(tenantId, desde, hasta);

        assertThat(reporte.getGastos())
                .allMatch(g -> !g.getFechaEmision().isBefore(desde) && !g.getFechaEmision().isAfter(hasta));
    }
}
