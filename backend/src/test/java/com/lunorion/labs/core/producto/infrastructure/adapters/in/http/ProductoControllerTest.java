package com.lunorion.labs.core.producto.infrastructure.adapters.in.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunorion.labs.core.inventario.application.dto.out.MovimientoStockResponse;
import com.lunorion.labs.core.inventario.domain.ports.in.IMovimientoStockQueryPort;
import com.lunorion.labs.core.producto.application.dto.in.CreateProductoRequest;
import com.lunorion.labs.core.producto.application.dto.in.UpdateStockRequest;
import com.lunorion.labs.core.producto.application.dto.out.ProductoResponse;
import com.lunorion.labs.core.producto.domain.ports.in.IProductoCommandPort;
import com.lunorion.labs.core.producto.domain.ports.in.IProductoQueryPort;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IProductoCommandPort commandService;

    @MockBean
    private IProductoQueryPort queryService;

    @MockBean
    private IMovimientoStockQueryPort movimientoQueryService;

    @Test
    void deberiaObtenerProductoPorId() throws Exception {
        ProductoResponse response = TestDataFactory.createProductoResponse();

        when(queryService.findById(response.getId())).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/productos/{id}", response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.nombre").value(response.getNombre()));
    }

    @Test
    void deberiaRetornar404_CuandoProductoNoExiste() throws Exception {
        when(queryService.findById("id-inexistente")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/{id}", "id-inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deberiaCrearProducto() throws Exception {
        CreateProductoRequest request = TestDataFactory.createProductoRequest();
        ProductoResponse response = TestDataFactory.createProductoResponse();

        when(commandService.create(any(CreateProductoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    void deberiaActualizarProducto() throws Exception {
        String id = TestDataFactory.uniqueId();
        CreateProductoRequest request = TestDataFactory.createProductoRequest();
        ProductoResponse response = TestDataFactory.createProductoResponse();

        when(commandService.update(eq(id), any(CreateProductoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/productos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }

    @Test
    void deberiaActualizarStock() throws Exception {
        String id = TestDataFactory.uniqueId();
        UpdateStockRequest request = TestDataFactory.updateStockRequest(50);

        mockMvc.perform(patch("/api/productos/{id}/stock", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaObtenerMovimientosDeProducto() throws Exception {
        String id = TestDataFactory.uniqueId();
        MovimientoStockResponse mov = TestDataFactory.createMovimientoStockResponse();

        when(movimientoQueryService.findByProductoId(id)).thenReturn(List.of(mov));

        mockMvc.perform(get("/api/productos/{id}/movimientos", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].tipo").value(mov.getTipo()));
    }

    @Test
    void deberiaObtenerMovimientosVacio_CuandoNoHayMovimientos() throws Exception {
        String id = TestDataFactory.uniqueId();

        when(movimientoQueryService.findByProductoId(id)).thenReturn(List.of());

        mockMvc.perform(get("/api/productos/{id}/movimientos", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void deberiaObtenerStockAlertas() throws Exception {
        String tenantId = TestDataFactory.tenantId();
        ProductoResponse p1 = TestDataFactory.createProductoResponse();
        when(queryService.findStockCritico(tenantId)).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/productos/stock-alertas")
                        .param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(p1.getCodigo()));
    }

    @Test
    void deberiaObtenerReporteRotacion() throws Exception {
        String tenantId = TestDataFactory.tenantId();
        ProductoResponse p1 = TestDataFactory.createProductoResponse();
        String productoId = p1.getId();

        when(queryService.reporteRotacion(tenantId)).thenReturn(List.of(p1));
        when(movimientoQueryService.findByProductoId(productoId)).thenReturn(List.of());

        mockMvc.perform(get("/api/productos/reporte-rotacion")
                        .param("tenantId", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalMovimientos").value(0));
    }

    @Test
    void deberiaBuscarPorCodigo() throws Exception {
        ProductoResponse response = TestDataFactory.createProductoResponse();
        when(queryService.findByCodigo(response.getCodigo())).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/productos/codigo/{codigo}", response.getCodigo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(response.getCodigo()));
    }

    @Test
    void deberiaBuscarPorCategoria() throws Exception {
        UUID categoriaId = UUID.randomUUID();
        ProductoResponse p1 = TestDataFactory.createProductoResponse();
        when(queryService.findByCategoriaId(categoriaId)).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/productos/categoria/{categoriaId}", categoriaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(p1.getCodigo()));
    }

    @Test
    void deberiaListarProductosPorTenant() throws Exception {
        String tenantId = TestDataFactory.tenantId();
        ProductoResponse p1 = TestDataFactory.createProductoResponse();
        when(queryService.findByTenantId(tenantId)).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/productos/tenant/{tenantId}", tenantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(p1.getCodigo()));
    }

    @Test
    void deberiaCreacionRapida() throws Exception {
        CreateProductoRequest request = TestDataFactory.createProductoRequest();
        request.setCodigo(null);
        ProductoResponse response = TestDataFactory.createProductoResponse();

        when(commandService.quickCreate(any(CreateProductoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/productos/creacion-rapida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()));
    }
}
