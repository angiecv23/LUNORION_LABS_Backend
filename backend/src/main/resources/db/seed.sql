-- ============================================================
-- SEED DATA - COLUMNAS EXACTAS DE INIT.SQL
-- ============================================================

-- 1. TENANT
INSERT INTO tenant (id, ruc, razon_social, nombre_comercial, domicilio_fiscal, email, telefono, regimen_tributario, plan, estado)
VALUES ('11111111-1111-1111-1111-111111111111', '20123456789', 'LUNORION LABS SAC', 'LUNORION', 'Av. Principal 123, Lima', 'contacto@lunorion.com', '999888777', 'GENERAL', 'PREMIUM', 'ACTIVO');

-- 2. PERMISO
INSERT INTO permiso (id, codigo, nombre, modulo) VALUES (gen_random_uuid(), 'VER_CLIENTES', 'Ver Clientes', 'CLIENTES');
INSERT INTO permiso (id, codigo, nombre, modulo) VALUES (gen_random_uuid(), 'VER_VEHICULOS', 'Ver Vehiculos', 'VEHICULOS');
INSERT INTO permiso (id, codigo, nombre, modulo) VALUES (gen_random_uuid(), 'VER_OT', 'Ver Ordenes de Trabajo', 'TALLER');

-- 3. USUARIO
INSERT INTO usuario (id, tenant_id, email, password_hash, nombres, apellidos, dni, telefono, rol)
VALUES ('22222222-2222-2222-2222-222222222201', '11111111-1111-1111-1111-111111111111', 'admin@lunorion.com', crypt('admin123', gen_salt('bf')), 'Admin', 'Principal', '12345678', '999111000', 'ADMIN');

INSERT INTO usuario (id, tenant_id, email, password_hash, nombres, apellidos, dni, telefono, rol)
VALUES ('22222222-2222-2222-2222-222222222202', '11111111-1111-1111-1111-111111111111', 'mecanico1@lunorion.com', crypt('meca123', gen_salt('bf')), 'Carlos', 'Lopez', '87654321', '999222111', 'MECANICO');

INSERT INTO usuario (id, tenant_id, email, password_hash, nombres, apellidos, dni, telefono, rol)
VALUES ('22222222-2222-2222-2222-222222222203', '11111111-1111-1111-1111-111111111111', 'cajero@lunorion.com', crypt('caja123', gen_salt('bf')), 'Maria', 'Garcia', '11223344', '999333222', 'CAJERO');

-- 4. CLIENTE
INSERT INTO cliente (id, tenant_id, tipo_documento, numero_documento, nombres, apellidos, razon_social, direccion, telefono, email, activo)
VALUES ('33333333-3333-3333-3333-333333333301', '11111111-1111-1111-1111-111111111111', 'DNI', '12345678', 'Juan', 'Perez', NULL, 'Jr. Las Flores 456', '987654321', 'juan@email.com', true);

INSERT INTO cliente (id, tenant_id, tipo_documento, numero_documento, nombres, apellidos, razon_social, direccion, telefono, email, activo)
VALUES ('33333333-3333-3333-3333-333333333302', '11111111-1111-1111-1111-111111111111', 'DNI', '23456789', 'Ana', 'Martinez', NULL, 'Av. Los Olivos 789', '976543210', 'ana@email.com', true);

INSERT INTO cliente (id, tenant_id, tipo_documento, numero_documento, nombres, apellidos, razon_social, direccion, telefono, email, activo)
VALUES ('33333333-3333-3333-3333-333333333303', '11111111-1111-1111-1111-111111111111', 'RUC', '20567890123', NULL, NULL, 'Tech Solutions EIRL', 'Calle Los Pinos 321', '965432109', 'ventas@techsolutions.pe', true);

INSERT INTO cliente (id, tenant_id, tipo_documento, numero_documento, nombres, apellidos, razon_social, direccion, telefono, email, activo)
VALUES ('33333333-3333-3333-3333-333333333304', '11111111-1111-1111-1111-111111111111', 'DNI', '34567890', 'Pedro', 'Ramirez', NULL, 'Pasaje Sol 555', '954321098', 'pedro@email.com', true);

-- 5. CATEGORIA_PRODUCTO
INSERT INTO categoria_producto (id, tenant_id, nombre)
VALUES ('55555555-5555-5555-5555-555555555501', '11111111-1111-1111-1111-111111111111', 'Lubricantes');
INSERT INTO categoria_producto (id, tenant_id, nombre)
VALUES ('55555555-5555-5555-5555-555555555502', '11111111-1111-1111-1111-111111111111', 'Filtros');
INSERT INTO categoria_producto (id, tenant_id, nombre)
VALUES ('55555555-5555-5555-5555-555555555503', '11111111-1111-1111-1111-111111111111', 'Frenos');

-- 6. PRODUCTO
INSERT INTO producto (id, tenant_id, categoria_id, codigo, nombre, unidad_medida, precio_compra, precio_venta, stock_actual, stock_minimo, tipo, activo)
VALUES ('66666666-6666-6666-6666-666666666601', '11111111-1111-1111-1111-111111111111', '55555555-5555-5555-5555-555555555501', 'ACE-001', 'Aceite 10W40 1L', 'UNIDAD', 18.00, 35.00, 50, 10, 'PRODUCTO', true);

INSERT INTO producto (id, tenant_id, categoria_id, codigo, nombre, unidad_medida, precio_compra, precio_venta, stock_actual, stock_minimo, tipo, activo)
VALUES ('66666666-6666-6666-6666-666666666602', '11111111-1111-1111-1111-111111111111', '55555555-5555-5555-5555-555555555502', 'FIL-001', 'Filtro de Aceite', 'UNIDAD', 15.00, 28.00, 30, 5, 'PRODUCTO', true);

INSERT INTO producto (id, tenant_id, categoria_id, codigo, nombre, unidad_medida, precio_compra, precio_venta, stock_actual, stock_minimo, tipo, activo)
VALUES ('66666666-6666-6666-6666-666666666603', '11111111-1111-1111-1111-111111111111', '55555555-5555-5555-5555-555555555503', 'FREN-001', 'Pastillas de Freno', 'JUEGO', 85.00, 150.00, 20, 5, 'PRODUCTO', true);

INSERT INTO producto (id, tenant_id, categoria_id, codigo, nombre, unidad_medida, precio_compra, precio_venta, stock_actual, stock_minimo, tipo, activo)
VALUES ('66666666-6666-6666-6666-666666666604', '11111111-1111-1111-1111-111111111111', '55555555-5555-5555-5555-555555555503', 'FREN-002', 'Disco de Freno', 'UNIDAD', 45.00, 80.00, 15, 3, 'PRODUCTO', true);

-- 7. TECNICO
INSERT INTO tecnico (id, tenant_id, usuario_id, especialidades, tarifa_hora, numero_contacto, activo)
VALUES ('77777777-7777-7777-7777-777777777701', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222202', 'Motor, Frenos, Suspension', 60.00, '999444333', true);

-- 8. VEHICULO
INSERT INTO vehiculo (id, tenant_id, cliente_id, placa, numero_chasis, numero_motor, marca, modelo, año, color, kilometraje, tipo_combustible, activo)
VALUES ('44444444-4444-4444-4444-444444444401', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333301', 'ABC-123', 'CHASIS-001', 'MOTOR-001', 'Toyota', 'Corolla', 2020, 'Blanco', 50000, 'GASOLINA', true);

INSERT INTO vehiculo (id, tenant_id, cliente_id, placa, numero_chasis, numero_motor, marca, modelo, año, color, kilometraje, tipo_combustible, activo)
VALUES ('44444444-4444-4444-4444-444444444402', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333301', 'DEF-456', 'CHASIS-002', 'MOTOR-002', 'Honda', 'Civic', 2021, 'Negro', 35000, 'GASOLINA', true);

INSERT INTO vehiculo (id, tenant_id, cliente_id, placa, numero_chasis, numero_motor, marca, modelo, año, color, kilometraje, tipo_combustible, activo)
VALUES ('44444444-4444-4444-4444-444444444403', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333302', 'GHI-789', 'CHASIS-003', 'MOTOR-003', 'Nissan', 'Sentra', 2019, 'Rojo', 60000, 'DIESEL', true);

INSERT INTO vehiculo (id, tenant_id, cliente_id, placa, numero_chasis, numero_motor, marca, modelo, año, color, kilometraje, tipo_combustible, activo)
VALUES ('44444444-4444-4444-4444-444444444404', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333304', 'JKL-012', 'CHASIS-004', 'MOTOR-004', 'Kia', 'Rio', 2022, 'Azul', 15000, 'GASOLINA', true);

-- 9. ORDEN_TRABAJO
INSERT INTO orden_trabajo (id, tenant_id, cliente_id, vehiculo_id, tecnico_id, numero_ot, estado, motivo_ingreso, kilometraje_ingreso, fecha_ingreso, fecha_prometida, total_repuestos, total_mano_obra, total, usuario_creo, usuario_cerro)
VALUES ('88888888-8888-8888-8888-888888888801', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333301', '44444444-4444-4444-4444-444444444401', '77777777-7777-7777-7777-777777777701', 'OT-2024-001', 'COMPLETADA', 'Cambio de aceite y filtro, revision general', 50000, CURRENT_TIMESTAMP - INTERVAL '5 days', '2024-06-20', 63.00, 120.00, 183.00, '22222222-2222-2222-2222-222222222201', '22222222-2222-2222-2222-222222222201');

INSERT INTO orden_trabajo (id, tenant_id, cliente_id, vehiculo_id, tecnico_id, numero_ot, estado, motivo_ingreso, kilometraje_ingreso, fecha_ingreso, fecha_prometida, total_repuestos, total_mano_obra, total, usuario_creo, usuario_cerro)
VALUES ('88888888-8888-8888-8888-888888888802', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333302', '44444444-4444-4444-4444-444444444403', '77777777-7777-7777-7777-777777777701', 'OT-2024-002', 'EN_PROCESO', 'Reparacion de frenos delanteros y traseros', 35000, CURRENT_TIMESTAMP - INTERVAL '2 days', '2024-07-05', 170.00, 180.00, 350.00, '22222222-2222-2222-2222-222222222201', NULL);

INSERT INTO orden_trabajo (id, tenant_id, cliente_id, vehiculo_id, tecnico_id, numero_ot, estado, motivo_ingreso, kilometraje_ingreso, fecha_ingreso, fecha_prometida, total_repuestos, total_mano_obra, total, usuario_creo, usuario_cerro)
VALUES ('88888888-8888-8888-8888-888888888803', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333304', '44444444-4444-4444-4444-444444444404', '77777777-7777-7777-7777-777777777701', 'OT-2024-003', 'PENDIENTE', 'Diagnostico de falla en encendido', 12000, CURRENT_TIMESTAMP, NULL, 0.00, 60.00, 60.00, '22222222-2222-2222-2222-222222222202', NULL);

-- 10. OT_INSUMO
INSERT INTO ot_insumo (id, ot_id, orden_trabajo_id, producto_id, cantidad, precio_unitario, subtotal)
VALUES (gen_random_uuid(), '88888888-8888-8888-8888-888888888801', '88888888-8888-8888-8888-888888888801', '66666666-6666-6666-6666-666666666601', 1, 35.00, 35.00);

INSERT INTO ot_insumo (id, ot_id, orden_trabajo_id, producto_id, cantidad, precio_unitario, subtotal)
VALUES (gen_random_uuid(), '88888888-8888-8888-8888-888888888801', '88888888-8888-8888-8888-888888888801', '66666666-6666-6666-6666-666666666602', 1, 28.00, 28.00);

INSERT INTO ot_insumo (id, ot_id, orden_trabajo_id, producto_id, cantidad, precio_unitario, subtotal)
VALUES (gen_random_uuid(), '88888888-8888-8888-8888-888888888802', '88888888-8888-8888-8888-888888888802', '66666666-6666-6666-6666-666666666603', 2, 85.00, 170.00);

-- 11. OT_MANO_OBRA
INSERT INTO ot_mano_obra (id, ot_id, orden_trabajo_id, tecnico_id, servicio_descripcion, horas, tarifa_hora, subtotal)
VALUES (gen_random_uuid(), '88888888-8888-8888-8888-888888888801', '88888888-8888-8888-8888-888888888801', '77777777-7777-7777-7777-777777777701', 'Mantenimiento Preventivo', 2, 60.00, 120.00);

INSERT INTO ot_mano_obra (id, ot_id, orden_trabajo_id, tecnico_id, servicio_descripcion, horas, tarifa_hora, subtotal)
VALUES (gen_random_uuid(), '88888888-8888-8888-8888-888888888802', '88888888-8888-8888-8888-888888888802', '77777777-7777-7777-7777-777777777701', 'Reparacion General', 3, 60.00, 180.00);

INSERT INTO ot_mano_obra (id, ot_id, orden_trabajo_id, tecnico_id, servicio_descripcion, horas, tarifa_hora, subtotal)
VALUES (gen_random_uuid(), '88888888-8888-8888-8888-888888888803', '88888888-8888-8888-8888-888888888803', '77777777-7777-7777-7777-777777777701', 'Diagnostico', 1, 60.00, 60.00);

-- 12. CITA
INSERT INTO cita (id, tenant_id, cliente_id, vehiculo_id, tecnico_id, servicio_descripcion, fecha_hora, duracion_minutos, estado, recordatorio_enviado, notificar_whatsapp, usuario_creo)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333301', '44444444-4444-4444-4444-444444444401', '77777777-7777-7777-7777-777777777701', 'Cambio de aceite', CURRENT_TIMESTAMP + INTERVAL '2 hours', 60, 'CONFIRMADA', false, false, '22222222-2222-2222-2222-222222222201');

INSERT INTO cita (id, tenant_id, cliente_id, vehiculo_id, tecnico_id, servicio_descripcion, fecha_hora, duracion_minutos, estado, recordatorio_enviado, notificar_whatsapp, usuario_creo)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333304', '44444444-4444-4444-4444-444444444404', '77777777-7777-7777-7777-777777777701', 'Revision de frenos', CURRENT_TIMESTAMP + INTERVAL '1 day', 90, 'PENDIENTE', false, false, '22222222-2222-2222-2222-222222222201');

-- 13. VENTA (venta_item)
INSERT INTO venta (id, tenant_id, cliente_id, tipo, subtotal, igv, total, descuento, metodo_pago, estado_pago, usuario_id)
VALUES ('99999999-9999-9999-9999-999999999901', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333301', 'BOLETA', 100.00, 18.00, 118.00, 0.00, 'EFECTIVO', 'PAGADO', '22222222-2222-2222-2222-222222222203');

INSERT INTO venta_item (id, venta_id, producto_id, cantidad, precio_unitario, descuento, subtotal)
VALUES (gen_random_uuid(), '99999999-9999-9999-9999-999999999901', '66666666-6666-6666-6666-666666666601', 2, 35.00, 0.00, 70.00);

INSERT INTO venta_item (id, venta_id, producto_id, cantidad, precio_unitario, descuento, subtotal)
VALUES (gen_random_uuid(), '99999999-9999-9999-9999-999999999901', '66666666-6666-6666-6666-666666666602', 1, 30.00, 0.00, 30.00);

INSERT INTO venta (id, tenant_id, cliente_id, tipo, subtotal, igv, total, descuento, metodo_pago, estado_pago, usuario_id)
VALUES ('99999999-9999-9999-9999-999999999902', '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333301', 'FACTURA', 500.00, 90.00, 590.00, 0.00, 'TRANSFERENCIA', 'PAGADO', '22222222-2222-2222-2222-222222222203');

INSERT INTO venta_item (id, venta_id, producto_id, cantidad, precio_unitario, descuento, subtotal)
VALUES (gen_random_uuid(), '99999999-9999-9999-9999-999999999902', '66666666-6666-6666-6666-666666666603', 2, 250.00, 0.00, 500.00);

INSERT INTO venta (id, tenant_id, cliente_id, tipo, subtotal, igv, total, descuento, metodo_pago, estado_pago, usuario_id)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333302', 'BOLETA', 85.00, 15.30, 100.30, 5.00, 'EFECTIVO', 'PAGADO', '22222222-2222-2222-2222-222222222203');

INSERT INTO venta_item (id, venta_id, producto_id, cantidad, precio_unitario, descuento, subtotal)
VALUES (gen_random_uuid(), (SELECT id FROM venta WHERE cliente_id = '33333333-3333-3333-3333-333333333302' AND total = 100.30 LIMIT 1), '66666666-6666-6666-6666-666666666601', 1, 35.00, 0.00, 35.00);

INSERT INTO venta_item (id, venta_id, producto_id, cantidad, precio_unitario, descuento, subtotal)
VALUES (gen_random_uuid(), (SELECT id FROM venta WHERE cliente_id = '33333333-3333-3333-3333-333333333302' AND total = 100.30 LIMIT 1), '66666666-6666-6666-6666-666666666604', 1, 50.00, 0.00, 50.00);

-- 14. CIERRE_CAJA
INSERT INTO cierre_caja (id, tenant_id, fecha, hora_apertura, saldo_inicial, total_ingresos, total_egresos, saldo_esperado, usuario_apertura)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01', '11111111-1111-1111-1111-111111111111', CURRENT_DATE, CURRENT_TIME, 500.00, 708.00, 50.00, 1158.00, '22222222-2222-2222-2222-222222222203');

-- 15. MOVIMIENTO_CAJA
INSERT INTO movimiento_caja (id, tenant_id, cierre_caja_id, tipo, metodo_pago, monto, referencia, concepto, usuario_id)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01', 'INGRESO', 'EFECTIVO', 118.00, 'VEN-001', 'Venta boleta', '22222222-2222-2222-2222-222222222203');

INSERT INTO movimiento_caja (id, tenant_id, cierre_caja_id, tipo, metodo_pago, monto, referencia, concepto, usuario_id)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01', 'INGRESO', 'TRANSFERENCIA', 590.00, 'VEN-002', 'Venta factura', '22222222-2222-2222-2222-222222222203');

INSERT INTO movimiento_caja (id, tenant_id, cierre_caja_id, tipo, metodo_pago, monto, referencia, concepto, usuario_id)
VALUES (gen_random_uuid(), '11111111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaa01', 'SALIDA', 'EFECTIVO', 50.00, 'GAS-001', 'Compra de suministros', '22222222-2222-2222-2222-222222222203');
