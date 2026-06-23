package microservice.despacho.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import microservice.despacho.dto.DatosRelacionadosDespacho;
import microservice.despacho.model.Despacho;
import microservice.despacho.service.DespachoIntegracionService;
import microservice.despacho.service.DespachoService;

@ExtendWith(MockitoExtension.class)
class DespachoControllerTest {

	@Mock
	private DespachoService despachoService;

	@Mock
	private DespachoIntegracionService despachoIntegracionService;

	private DespachoController despachoController;

	@BeforeEach
	void setUp() {
		despachoController = new DespachoController(despachoService, despachoIntegracionService);
	}

	@Test
	void listarRetornaDespachos() {
		List<Despacho> despachos = List.of(crearDespacho());
		when(despachoService.listar()).thenReturn(despachos);

		assertThat(despachoController.listar()).isEqualTo(despachos);
	}

	@Test
	void obtenerPorIdRetornaDespacho() {
		Despacho despacho = crearDespacho();
		when(despachoService.obtenerPorId(1L)).thenReturn(despacho);

		assertThat(despachoController.obtenerPorId(1L)).isEqualTo(despacho);
	}

	@Test
	void obtenerDatosRelacionadosRetornaDatosExternos() {
		Despacho despacho = crearDespacho();
		DatosRelacionadosDespacho datos = new DatosRelacionadosDespacho(
				despacho,
				Map.of("idPedido", 10),
				Map.of("idCliente", 20),
				Map.of("idSucursal", 30));
		when(despachoIntegracionService.obtenerDatosRelacionados(1L)).thenReturn(datos);

		assertThat(despachoController.obtenerDatosRelacionados(1L)).isEqualTo(datos);
	}

	@Test
	void busquedasDeleganEnServicio() {
		List<Despacho> despachos = List.of(crearDespacho());
		when(despachoService.buscarPorPedido(10L)).thenReturn(despachos);
		when(despachoService.buscarPorCliente(20L)).thenReturn(despachos);
		when(despachoService.buscarPorSucursal(30L)).thenReturn(despachos);
		when(despachoService.buscarPorEstado("PENDIENTE")).thenReturn(despachos);

		assertThat(despachoController.buscarPorPedido(10L)).isEqualTo(despachos);
		assertThat(despachoController.buscarPorCliente(20L)).isEqualTo(despachos);
		assertThat(despachoController.buscarPorSucursal(30L)).isEqualTo(despachos);
		assertThat(despachoController.buscarPorEstado("PENDIENTE")).isEqualTo(despachos);
	}

	@Test
	void crearYActualizarDeleganEnServicio() {
		Despacho despacho = crearDespacho();
		when(despachoService.crear(despacho)).thenReturn(despacho);
		when(despachoService.actualizar(1L, despacho)).thenReturn(despacho);
		when(despachoService.cargarDatosDemo()).thenReturn(List.of(despacho));

		assertThat(despachoController.crear(despacho)).isEqualTo(despacho);
		assertThat(despachoController.actualizar(1L, despacho)).isEqualTo(despacho);
		assertThat(despachoController.cargarDatosDemo()).containsExactly(despacho);
	}

	@Test
	void accionesParcialesDeleganEnServicio() {
		Despacho despacho = crearDespacho();
		when(despachoService.cambiarEstado(1L, "ENTREGADO")).thenReturn(despacho);
		when(despachoService.asignarRuta(1L)).thenReturn(despacho);
		when(despachoService.marcarEnTransito(1L)).thenReturn(despacho);
		when(despachoService.confirmarEntrega(1L)).thenReturn(despacho);
		when(despachoService.cancelar(1L)).thenReturn(despacho);

		assertThat(despachoController.cambiarEstado(1L, Map.of("estado", "ENTREGADO"))).isEqualTo(despacho);
		assertThat(despachoController.asignarRuta(1L)).isEqualTo(despacho);
		assertThat(despachoController.marcarEnTransito(1L)).isEqualTo(despacho);
		assertThat(despachoController.confirmarEntrega(1L)).isEqualTo(despacho);
		assertThat(despachoController.cancelar(1L)).isEqualTo(despacho);
	}

	@Test
	void eliminarRetornaSinContenido() {
		ResponseEntity<Void> respuesta = despachoController.eliminar(1L);

		verify(despachoService).eliminar(1L);
		assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
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
