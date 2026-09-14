package com.lunorion.labs.core.ordencompra.application.service.command;

import com.lunorion.labs.core.inventario.domain.entity.MovimientoStock;
import com.lunorion.labs.core.inventario.domain.ports.out.IMovimientoStockRepositoryPort;
import com.lunorion.labs.core.ordencompra.application.dto.in.CreateOrdenCompraRequest;
import com.lunorion.labs.core.ordencompra.application.dto.in.RecibirOrdenRequest;
import com.lunorion.labs.core.ordencompra.application.dto.out.OrdenCompraResponse;
import com.lunorion.labs.core.ordencompra.application.mapper.OrdenCompraMapper;
import com.lunorion.labs.core.ordencompra.domain.entity.OrdenCompra;
import com.lunorion.labs.core.ordencompra.domain.entity.OrdenCompraItem;
import com.lunorion.labs.core.ordencompra.domain.ports.in.IOrdenCompraCommandPort;
import com.lunorion.labs.core.ordencompra.domain.ports.out.IOrdenCompraItemRepositoryPort;
import com.lunorion.labs.core.ordencompra.domain.ports.out.IOrdenCompraRepositoryPort;
import com.lunorion.labs.core.producto.domain.entity.Producto;
import com.lunorion.labs.core.producto.domain.ports.out.IProductoRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class OrdenCompraCommandService implements IOrdenCompraCommandPort {

    private final IOrdenCompraRepositoryPort repository;
    private final IOrdenCompraItemRepositoryPort itemRepository;
    private final OrdenCompraMapper mapper;
    private final IMovimientoStockRepositoryPort movimientoStockRepository;
    private final IProductoRepositoryPort productRepository;

    public OrdenCompraCommandService(IOrdenCompraRepositoryPort repository,
                                      IOrdenCompraItemRepositoryPort itemRepository,
                                      OrdenCompraMapper mapper,
                                      IMovimientoStockRepositoryPort movimientoStockRepository,
                                      IProductoRepositoryPort productRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
        this.movimientoStockRepository = movimientoStockRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrdenCompraResponse create(CreateOrdenCompraRequest request) {
        OrdenCompra ordenCompra = mapper.toDomain(request);
        OrdenCompra saved = repository.save(ordenCompra);
        return mapper.toResponse(saved);
    }

    @Override
    public void aprobar(String id) {
        repository.findById(id).ifPresent(oc -> {
            oc.aprobar();
            repository.save(oc);
        });
    }

    @Override
    public void completar(String id) {
        repository.findById(id).ifPresent(oc -> {
            oc.completar();
            repository.save(oc);
        });
    }

    @Override
    public void anular(String id) {
        repository.findById(id).ifPresent(oc -> {
            oc.anular();
            repository.save(oc);
        });
    }

    @Override
    public OrdenCompraResponse recibir(String id, RecibirOrdenRequest request) {
        return repository.findById(id).map(oc -> {
            if (request.getItems() != null) {
                request.getItems().forEach(item -> {
                    itemRepository.findById(item.getItemId()).ifPresent(oci -> {
                        BigDecimal recibido = item.getCantidadRecibida();
                        oci.setCantidadRecibida(recibido);
                        itemRepository.save(oci);

                        Producto producto = productRepository.findById(oci.getProductoId())
                                .orElse(null);
                        if (producto != null) {
                            BigDecimal stockAnterior = BigDecimal.valueOf(
                                    producto.getStockActual() != null ? producto.getStockActual() : 0);
                            BigDecimal stockPosterior = stockAnterior.add(recibido);

                            MovimientoStock movimiento = MovimientoStock.create(
                                    oc.getTenantId(),
                                    oci.getProductoId(),
                                    "INGRESO",
                                    "COMPRA",
                                    recibido,
                                    oci.getPrecioUnitario(),
                                    stockAnterior,
                                    stockPosterior,
                                    "OC-" + oc.getNumeroOrden(),
                                    "Ingreso por recepcion de orden de compra",
                                    oc.getUsuarioId()
                            );
                            movimientoStockRepository.save(movimiento);

                            producto.updateStock(stockPosterior.intValue());
                            productRepository.save(producto);
                        }
                    });
                });
            }
            oc.completar();
            OrdenCompra saved = repository.save(oc);
            return mapper.toResponse(saved);
        }).orElseThrow(() -> new RuntimeException("Orden de compra no encontrada: " + id));
    }
}
