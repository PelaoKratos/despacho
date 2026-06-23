package microservice.despacho.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import microservice.despacho.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class MicroservicioExternoServiceTest {

	@Mock
	private RestTemplate restTemplate;

	private MicroservicioExternoService microservicioExternoService;

	@BeforeEach
	void setUp() {
		microservicioExternoService = new MicroservicioExternoService(
				restTemplate,
				"http://pedidos/api",
				"http://clientes/api",
				"http://sucursales/api");
	}

	@Test
	void obtenerPedidoRetornaRespuestaDeApiExterna() {
		Map<String, Object> pedido = Map.of("idPedido", 10);
		when(restTemplate.exchange(
				eq("http://pedidos/api/10"),
				eq(HttpMethod.GET),
				isNull(),
				any(ParameterizedTypeReference.class)))
				.thenReturn(ResponseEntity.ok(pedido));

		assertThat(microservicioExternoService.obtenerPedido(10L)).isEqualTo(pedido);
	}

	@Test
	void obtenerClienteRetornaMapaVacioCuandoApiNoTraeCuerpo() {
		when(restTemplate.exchange(
				eq("http://clientes/api/20"),
				eq(HttpMethod.GET),
				isNull(),
				any(ParameterizedTypeReference.class)))
				.thenReturn(ResponseEntity.ok(null));

		assertThat(microservicioExternoService.obtenerCliente(20L)).isEmpty();
	}

	@Test
	void obtenerSucursalLanzaErrorCuandoApiExternaFalla() {
		when(restTemplate.exchange(
				eq("http://sucursales/api/30"),
				eq(HttpMethod.GET),
				isNull(),
				any(ParameterizedTypeReference.class)))
				.thenThrow(new RestClientException("No disponible"));

		assertThatThrownBy(() -> microservicioExternoService.obtenerSucursal(30L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("No se pudo rescatar sucursal con id 30");
	}

	@Test
	void obtenerSucursalRetornaRespuestaDeApiExterna() {
		Map<String, Object> sucursal = Map.of("idSucursal", 31);
		when(restTemplate.exchange(
				eq("http://sucursales/api/31"),
				eq(HttpMethod.GET),
				isNull(),
				any(ParameterizedTypeReference.class)))
				.thenReturn(ResponseEntity.ok(sucursal));

		assertThat(microservicioExternoService.obtenerSucursal(31L)).isEqualTo(sucursal);
	}
}
