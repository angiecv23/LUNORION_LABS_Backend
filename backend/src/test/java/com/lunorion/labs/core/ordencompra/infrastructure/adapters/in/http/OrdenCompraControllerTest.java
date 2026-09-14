package com.lunorion.labs.core.ordencompra.infrastructure.adapters.in.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunorion.labs.core.ordencompra.application.dto.in.CreateOrdenCompraRequest;
import com.lunorion.labs.core.ordencompra.application.dto.in.RecibirOrdenRequest;
import com.lunorion.labs.core.ordencompra.application.dto.out.OrdenCompraItemResponse;
import com.lunorion.labs.core.ordencompra.application.dto.out.OrdenCompraResponse;
import com.lunorion.labs.core.ordencompra.application.dto.out.ReporteGastosResponse;
import com.lunorion.labs.core.ordencompra.application.service.query.OrdenCompraQueryService;
import com.lunorion.labs.core.ordencompra.domain.ports.in.IOrdenCompraCommandPort;
import com.lunorion.labs.core.ordencompra.domain.ports.in.IOrdenCompraQueryPort;
import com.lunorion.labs.shared.test.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrdenCompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IOrdenCompraCommandPort commandService;

    @MockBean
    private OrdenCompraQueryService queryService;

    @Test
    void deberiaCrearOrdenCompra() throws Exception {
        CreateOrdenCompraRequest request = TestDataFactory.createOrdenCompraRequest();
        OrdenCompraResponse response = TestDataFactory.createOrdenCompraResponse("PENDIENTE");

        when(commandService.create(any(CreateOrdenCompraRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/ordenes-compra")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroOrden").value(response.getNumeroOrden()));
    }

    @Test
    void deberiaObtenerOrdenPorId() throws Exception {
        OrdenCompraResponse response = TestDataFactory.createOrdenCompraResponse("PENDIENTE");
        when(queryService.findById(response.getId())).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/ordenes-compra/{id}", response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    void deberiaRetornar404_CuandoOrdenNoExiste() throws Exception {
        when(queryService.findById("id-inexistente")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ordenes-compra/{id}", "id-inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaAprobarOrden() throws Exception {
        String id = TestDataFactory.uniqueId();

        mockMvc.perform(post("/api/ordenes-compra/{id}/aprobar", id))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaCompletarOrden() throws Exception {
        String id = TestDataFactory.uniqueId();

        mockMvc.perform(post("/api/ordenes-compra/{id}/completar", id))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaAnularOrden() throws Exception {
        String id = TestDataFactory.uniqueId();

        mockMvc.perform(post("/api/ordenes-compra/{id}/anular", id))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaRecibirOrden() throws Exception {
        String id = TestDataFactory.uniqueId();
        RecibirOrdenRequest request = TestDataFactory.createRecibirOrdenRequest(
                TestDataFactory.uniqueId(), 10);
        OrdenCompraResponse response = TestDataFactory.createOrdenCompraResponse("COMPLETADA");

        when(commandService.recibir(eq(id), any(RecibirOrdenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/ordenes-compra/{id}/recibir", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    void deberiaListarItemsDeOrden() throws Exception {
        String ordenId = TestDataFactory.uniqueId();
        OrdenCompraItemResponse item = new OrdenCompraItemResponse();
        item.setId(TestDataFactory.uniqueId());
        item.setOrdenCompraId(ordenId);
        item.setProductoId(TestDataFactory.uniqueId());
        item.setCantidad(BigDecimal.TEN);
        item.setCantidadRecibida(BigDecimal.ZERO);
        item.setPrecioUnitario(BigDecimal.valueOf(50));
        item.setSubtotal(BigDecimal.valueOf(500));

        when(queryService.findItemsByOrdenCompraId(ordenId)).thenReturn(List.of(item));

        mockMvc.perform(get("/api/ordenes-compra/{id}/items", ordenId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ordenCompraId").value(ordenId));
    }

    @Test
    void deberiaListarOrdenesPorTenant() throws Exception {
        String tenantId = TestDataFactory.tenantId();
        OrdenCompraResponse o1 = TestDataFactory.createOrdenCompraResponse("PENDIENTE");
        when(queryService.findByTenantId(tenantId)).thenReturn(List.of(o1));

        mockMvc.perform(get("/api/ordenes-compra/tenant/{tenantId}", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value(o1.getTenantId()));
    }

    @Test
    void deberiaListarOrdenesPorProveedor() throws Exception {
        String proveedorId = TestDataFactory.uniqueId();
        OrdenCompraResponse o1 = TestDataFactory.createOrdenCompraResponse("PENDIENTE");
        when(queryService.findByProveedorId(proveedorId)).thenReturn(List.of(o1));

        mockMvc.perform(get("/api/ordenes-compra/proveedor/{proveedorId}", proveedorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].proveedorId").value(o1.getProveedorId()));
    }

    @Test
    void deberiaGenerarReporteGastos() throws Exception {
        String tenantId = TestDataFactory.tenantId();
        LocalDate desde = LocalDate.now().minusMonths(1);
        LocalDate hasta = LocalDate.now();

        ReporteGastosResponse reporte = new ReporteGastosResponse();
        reporte.setTenantId(tenantId);
        reporte.setDesde(desde);
        reporte.setHasta(hasta);
        reporte.setTotalGastos(BigDecimal.valueOf(5000));

        when(queryService.spendingReport(eq(tenantId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(reporte);

        mockMvc.perform(get("/api/ordenes-compra/reporte-gastos")
                        .param("tenantId", tenantId)
                        .param("desde", desde.toString())
                        .param("hasta", hasta.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(tenantId))
                .andExpect(jsonPath("$.totalGastos").value(5000));
    }
}
