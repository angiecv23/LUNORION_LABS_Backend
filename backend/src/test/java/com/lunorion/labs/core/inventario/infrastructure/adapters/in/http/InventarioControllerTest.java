package com.lunorion.labs.core.inventario.infrastructure.adapters.in.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunorion.labs.core.inventario.application.dto.in.CreateMovimientoStockRequest;
import com.lunorion.labs.core.inventario.application.dto.out.MovimientoStockResponse;
import com.lunorion.labs.core.inventario.domain.ports.in.IMovimientoStockCommandPort;
import com.lunorion.labs.core.inventario.domain.ports.in.IMovimientoStockQueryPort;
import com.lunorion.labs.shared.test.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IMovimientoStockCommandPort commandService;

    @MockBean
    private IMovimientoStockQueryPort queryService;

    @Test
    void deberiaCrearMovimiento() throws Exception {
        CreateMovimientoStockRequest request = TestDataFactory.createMovimientoStockRequest(
                TestDataFactory.tenantId(), TestDataFactory.uniqueId(), "INGRESO", 10, 50);
        MovimientoStockResponse response = TestDataFactory.createMovimientoStockResponse();

        when(commandService.create(any(CreateMovimientoStockRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/inventario/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    void deberiaObtenerMovimientoPorId() throws Exception {
        MovimientoStockResponse response = TestDataFactory.createMovimientoStockResponse();
        when(queryService.findById(response.getId())).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/inventario/movimientos/{id}", response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    void deberiaRetornar404_CuandoMovimientoNoExiste() throws Exception {
        when(queryService.findById("id-inexistente")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/inventario/movimientos/{id}", "id-inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaListarTodosLosMovimientos() throws Exception {
        MovimientoStockResponse m1 = TestDataFactory.createMovimientoStockResponse();
        MovimientoStockResponse m2 = TestDataFactory.createMovimientoStockResponse();
        when(queryService.findAll()).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/inventario/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(m1.getId()));
    }

    @Test
    void deberiaListarMovimientosPorProducto() throws Exception {
        String productoId = TestDataFactory.uniqueId();
        MovimientoStockResponse mov = TestDataFactory.createMovimientoStockResponse();
        when(queryService.findByProductoId(productoId)).thenReturn(List.of(mov));

        mockMvc.perform(get("/api/inventario/movimientos/producto/{productoId}", productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productoId").value(mov.getProductoId()));
    }

    @Test
    void deberiaListarMovimientosPorTenant() throws Exception {
        String tenantId = TestDataFactory.tenantId();
        MovimientoStockResponse mov = TestDataFactory.createMovimientoStockResponse();
        when(queryService.findByTenantId(tenantId)).thenReturn(List.of(mov));

        mockMvc.perform(get("/api/inventario/movimientos/tenant/{tenantId}", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenantId").value(mov.getTenantId()));
    }

    @Test
    void deberiaEliminarMovimiento() throws Exception {
        String id = TestDataFactory.uniqueId();
        doNothing().when(commandService).delete(id);

        mockMvc.perform(delete("/api/inventario/movimientos/{id}", id))
                .andExpect(status().isNoContent());
    }
}
