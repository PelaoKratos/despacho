package microservice.despacho.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import microservice.despacho.dto.DatosRelacionadosDespacho;
import microservice.despacho.model.Despacho;

@ExtendWith(MockitoExtension.class)
class DespachoIntegracionServiceTest {

	@Mock
	private DespachoService despachoService;

	@Mock
	private MicroservicioExternoService microservicioExternoService;

	private DespachoIntegracionService despachoIntegracionService;

	@BeforeEach
	void setUp() {
		despachoIntegracionService = new DespachoIntegracionService(despachoService, microservicioExternoService);
	}

	@Test
	void obtenerDatosRelacionadosCombinaDespachoConApisExternas() {
		Despacho despacho = crearDespacho();
		Map<String, Object> pedido = Map.of("idPedido", 10L);
		Map<String, Object> cliente = Map.of("idCliente", 20L);
		Map<String, Object> sucursal = Map.of("idSucursal", 30L);
		when(despachoService.obtenerPorId(1L)).thenReturn(despacho);
		when(microservicioExternoService.obtenerPedido(10L)).thenReturn(pedido);
		when(microservicioExternoService.obtenerCliente(20L)).thenReturn(cliente);
		when(microservicioExternoService.obtenerSucursal(30L)).thenReturn(sucursal);

		DatosRelacionadosDespacho resultado = despachoIntegracionService.obtenerDatosRelacionados(1L);

		assertThat(resultado.despacho()).isSameAs(despacho);
		assertThat(resultado.pedido()).isEqualTo(pedido);
		assertThat(resultado.cliente()).isEqualTo(cliente);
		assertThat(resultado.sucursal()).isEqualTo(sucursal);
		assertThat(resultado.toString()).contains("pedido");
	}

	private Despacho crearDespacho() {
		return new Despacho(
				1L,
				10L,
				20L,
				30L,
				"Av. Siempre Viva 123",
				LocalDateTime.of(2026, 7, 1, 9, 0),
				LocalDateTime.of(2026, 7, 2, 18, 0),
				null,
				"PENDIENTE",
				List.of(),
				List.of(),
				List.of());
	}
}
