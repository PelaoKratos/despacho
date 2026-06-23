package microservice.despacho.dto;

import java.util.Map;

import microservice.despacho.model.Despacho;

public record DatosRelacionadosDespacho(
		Despacho despacho,
		Map<String, Object> pedido,
		Map<String, Object> cliente,
		Map<String, Object> sucursal) {
}
