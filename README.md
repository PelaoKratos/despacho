# Microservicio Despacho

Despacho se encarga del seguimiento de entregas. Trabaja con pedidos ya creados y permite registrar datos de envio, ruta, paradas, transportista y estado del despacho.

## Que gestiona

- Despachos asociados a pedidos.
- Detalle de productos del despacho.
- Rutas y paradas de entrega.
- Transportistas.
- Seguimiento y cambios de estado.

## Configuracion local

```properties
spring.application.name=despacho
server.port=8089
spring.datasource.url=jdbc:mysql://localhost:3307/despacho_bd?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
```

Base de datos:

```sql
CREATE DATABASE despacho_bd;
```

## Microservicios que consulta

```properties
microservices.pedidos.url=http://localhost:8086/api/pedidos
microservices.clientes.url=http://localhost:8082/api/v1/clientes
microservices.sucursales.url=http://localhost:8083/api/v1/sucursales
```

Esto permite revisar datos relacionados sin duplicar informacion en la base de despacho.

## Endpoints principales

- `GET /api/despachos`
- `GET /api/despachos/{id}`
- `GET /api/despachos/{id}/datos-relacionados`
- `GET /api/despachos/pedido/{idPedido}`
- `GET /api/despachos/cliente/{idCliente}`
- `GET /api/despachos/sucursal/{idSucursal}`
- `GET /api/despachos/estado/{estado}`
- `POST /api/despachos`
- `POST /api/despachos/datos-demo`
- `PUT /api/despachos/{id}`
- `DELETE /api/despachos/{id}`

## Ejecutar

```powershell
mvn spring-boot:run
```

## Probar

```powershell
mvn test
```
