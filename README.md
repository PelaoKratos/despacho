# Microservicio Despacho

Gestiona los despachos asociados a ventas, clientes y sucursales.

## Endpoints principales

- `GET /api/despachos`
- `GET /api/despachos/{id}`
- `GET /api/despachos/venta/{idVenta}`
- `GET /api/despachos/cliente/{idCliente}`
- `GET /api/despachos/sucursal/{idSucursal}`
- `GET /api/despachos/estado/{estado}`
- `POST /api/despachos`
- `PUT /api/despachos/{id}`
- `PATCH /api/despachos/{id}/estado`
- `PATCH /api/despachos/{id}/transportista`
- `PATCH /api/despachos/{id}/en-transito`
- `PATCH /api/despachos/{id}/entrega`
- `PATCH /api/despachos/{id}/cancelacion`
- `DELETE /api/despachos/{id}`

## Pruebas

El proyecto usa JaCoCo y exige 100% de cobertura de lineas y ramas sobre la logica del microservicio.

```bash
mvn test
```
