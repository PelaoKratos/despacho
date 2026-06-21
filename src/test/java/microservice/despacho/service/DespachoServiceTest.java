package microservice.despacho.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import microservice.despacho.exception.ResourceNotFoundException;
import microservice.despacho.model.Despacho;
import microservice.despacho.repository.DespachoRepository;

@ExtendWith(MockitoExtension.class)
class DespachoServiceTest {

	@Mock
	private DespachoRepository despachoRepository;

	private DespachoService despachoService;

	@BeforeEach
	void setUp() {
		despachoService = new DespachoService(despachoRepository);
	}

	@Test
	void listarRetornaTodosLosDespachos() {
		List<Despacho> despachos = List.of(crearDespacho(1L));
		when(despachoRepository.findAll()).thenReturn(despachos);

		assertThat(despachoService.listar()).isEqualTo(despachos);
	}

	@Test
	void obtenerPorIdRetornaDespachoExistente() {
		Despacho despacho = crearDespacho(1L);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));

		assertThat(despachoService.obtenerPorId(1L)).isEqualTo(despacho);
	}

	@Test
	void obtenerPorIdLanzaErrorCuandoNoExiste() {
		when(despachoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> despachoService.obtenerPorId(99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("No existe el despacho con id 99");
	}

	@Test
	void buscarPorVentaClienteSucursalYEstadoDeleganEnRepositorio() {
		List<Despacho> despachos = List.of(crearDespacho(1L));
		when(despachoRepository.findByIdVenta(10L)).thenReturn(despachos);
		when(despachoRepository.findByIdCliente(20L)).thenReturn(despachos);
		when(despachoRepository.findByIdSucursal(30L)).thenReturn(despachos);
		when(despachoRepository.findByEstado("PENDIENTE")).thenReturn(despachos);

		assertThat(despachoService.buscarPorVenta(10L)).isEqualTo(despachos);
		assertThat(despachoService.buscarPorCliente(20L)).isEqualTo(despachos);
		assertThat(despachoService.buscarPorSucursal(30L)).isEqualTo(despachos);
		assertThat(despachoService.buscarPorEstado(" pendiente ")).isEqualTo(despachos);
	}

	@Test
	void crearAsignaEstadoPendienteCuandoNoVieneEstado() {
		Despacho despacho = crearDespacho(null);
		despacho.setEstado(null);
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.crear(despacho);

		assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");
	}

	@Test
	void crearNormalizaEstadoInformado() {
		Despacho despacho = crearDespacho(null);
		despacho.setEstado(" en_transito ");
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.crear(despacho);

		assertThat(resultado.getEstado()).isEqualTo("EN_TRANSITO");
	}

	@Test
	void crearAsignaPendienteCuandoEstadoVieneEnBlanco() {
		Despacho despacho = crearDespacho(null);
		despacho.setEstado(" ");
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.crear(despacho);

		assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");
	}

	@Test
	void crearRechazaFechaEntregaAnteriorAFechaEstimada() {
		Despacho despacho = crearDespacho(null);
		despacho.setFechaEstimada(LocalDate.of(2026, 7, 2));
		despacho.setFechaEntrega(LocalDate.of(2026, 7, 1));

		assertThatThrownBy(() -> despachoService.crear(despacho))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("La fecha de entrega no puede ser anterior a la fecha estimada");
	}

	@Test
	void actualizarModificaTodosLosDatos() {
		Despacho existente = crearDespacho(1L);
		Despacho datos = crearDespacho(2L);
		datos.setIdVenta(55L);
		datos.setIdCliente(66L);
		datos.setIdSucursal(77L);
		datos.setDireccionEntrega("Nueva direccion");
		datos.setComuna("Providencia");
		datos.setCiudad("Santiago");
		datos.setEstado("cancelado");
		datos.setTransportista("Chilexpress");
		datos.setCostoDespacho(3990);
		datos.setFechaEstimada(LocalDate.of(2026, 8, 1));
		datos.setFechaEntrega(LocalDate.of(2026, 8, 2));
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(despachoRepository.save(existente)).thenReturn(existente);

		Despacho resultado = despachoService.actualizar(1L, datos);

		assertThat(resultado.getIdVenta()).isEqualTo(55L);
		assertThat(resultado.getIdCliente()).isEqualTo(66L);
		assertThat(resultado.getIdSucursal()).isEqualTo(77L);
		assertThat(resultado.getDireccionEntrega()).isEqualTo("Nueva direccion");
		assertThat(resultado.getComuna()).isEqualTo("Providencia");
		assertThat(resultado.getCiudad()).isEqualTo("Santiago");
		assertThat(resultado.getEstado()).isEqualTo("CANCELADO");
		assertThat(resultado.getTransportista()).isEqualTo("Chilexpress");
		assertThat(resultado.getCostoDespacho()).isEqualTo(3990);
		assertThat(resultado.getFechaEstimada()).isEqualTo(LocalDate.of(2026, 8, 1));
		assertThat(resultado.getFechaEntrega()).isEqualTo(LocalDate.of(2026, 8, 2));
	}

	@Test
	void cambiarEstadoAEntregadoRegistraFechaEntregaSiNoExiste() {
		Despacho despacho = crearDespacho(1L);
		despacho.setFechaEntrega(null);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.cambiarEstado(1L, "entregado");

		assertThat(resultado.getEstado()).isEqualTo("ENTREGADO");
		assertThat(resultado.getFechaEntrega()).isEqualTo(LocalDate.now());
	}

	@Test
	void cambiarEstadoAEntregadoMantieneFechaEntregaSiYaExiste() {
		Despacho despacho = crearDespacho(1L);
		LocalDate fechaEntrega = LocalDate.of(2026, 7, 5);
		despacho.setFechaEntrega(fechaEntrega);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.cambiarEstado(1L, "ENTREGADO");

		assertThat(resultado.getFechaEntrega()).isEqualTo(fechaEntrega);
	}

	@Test
	void cambiarEstadoNoEntregadoNoRegistraFechaEntrega() {
		Despacho despacho = crearDespacho(1L);
		despacho.setFechaEntrega(null);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.cambiarEstado(1L, "EN_TRANSITO");

		assertThat(resultado.getEstado()).isEqualTo("EN_TRANSITO");
		assertThat(resultado.getFechaEntrega()).isNull();
	}

	@Test
	void cambiarEstadoRechazaEstadoVacioONoValido() {
		assertThatThrownBy(() -> despachoService.buscarPorEstado(" "))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("El estado no puede estar vacio");

		assertThatThrownBy(() -> despachoService.buscarPorEstado(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("El estado no puede estar vacio");

		assertThatThrownBy(() -> despachoService.buscarPorEstado("PERDIDO"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Estado de despacho no valido: PERDIDO");
	}

	@Test
	void asignarTransportistaActualizaTransportistaYEstadoPendiente() {
		Despacho despacho = crearDespacho(1L);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.asignarTransportista(1L, "  Starken  ");

		assertThat(resultado.getTransportista()).isEqualTo("Starken");
		assertThat(resultado.getEstado()).isEqualTo("EN_PREPARACION");
	}

	@Test
	void asignarTransportistaRechazaValorVacio() {
		assertThatThrownBy(() -> despachoService.asignarTransportista(1L, " "))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("El transportista no puede estar vacio");

		assertThatThrownBy(() -> despachoService.asignarTransportista(1L, null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("El transportista no puede estar vacio");
	}

	@Test
	void asignarTransportistaNoCambiaEstadoSiNoEstaPendiente() {
		Despacho despacho = crearDespacho(1L);
		despacho.setEstado("EN_TRANSITO");
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.asignarTransportista(1L, "Starken");

		assertThat(resultado.getEstado()).isEqualTo("EN_TRANSITO");
	}

	@Test
	void marcarEnTransitoRequiereTransportista() {
		Despacho despacho = crearDespacho(1L);
		despacho.setTransportista(null);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));

		assertThatThrownBy(() -> despachoService.marcarEnTransito(1L))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("No se puede iniciar el despacho sin transportista");
	}

	@Test
	void marcarEnTransitoRechazaTransportistaEnBlanco() {
		Despacho despacho = crearDespacho(1L);
		despacho.setTransportista(" ");
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));

		assertThatThrownBy(() -> despachoService.marcarEnTransito(1L))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("No se puede iniciar el despacho sin transportista");
	}

	@Test
	void marcarEnTransitoActualizaEstado() {
		Despacho despacho = crearDespacho(1L);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.marcarEnTransito(1L);

		assertThat(resultado.getEstado()).isEqualTo("EN_TRANSITO");
	}

	@Test
	void confirmarEntregaActualizaEstadoYFecha() {
		Despacho despacho = crearDespacho(1L);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.confirmarEntrega(1L);

		assertThat(resultado.getEstado()).isEqualTo("ENTREGADO");
		assertThat(resultado.getFechaEntrega()).isEqualTo(LocalDate.now());
	}

	@Test
	void cancelarRechazaDespachoEntregado() {
		Despacho despacho = crearDespacho(1L);
		despacho.setEstado("ENTREGADO");
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));

		assertThatThrownBy(() -> despachoService.cancelar(1L))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("No se puede cancelar un despacho entregado");
	}

	@Test
	void cancelarActualizaEstado() {
		Despacho despacho = crearDespacho(1L);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.cancelar(1L);

		assertThat(resultado.getEstado()).isEqualTo("CANCELADO");
	}

	@Test
	void eliminarBorraDespachoExistente() {
		Despacho despacho = crearDespacho(1L);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));

		despachoService.eliminar(1L);

		verify(despachoRepository).delete(despacho);
	}

	@Test
	void crearGuardaDespachoConFechasValidas() {
		Despacho despacho = crearDespacho(null);
		despacho.setFechaEntrega(null);
		when(despachoRepository.save(any(Despacho.class))).thenReturn(despacho);

		assertThat(despachoService.crear(despacho)).isEqualTo(despacho);
	}

	@Test
	void crearPermiteFechaEstimadaNulaCuandoNoDebeCompararFechas() {
		Despacho despacho = crearDespacho(null);
		despacho.setFechaEstimada(null);
		despacho.setFechaEntrega(LocalDate.of(2026, 7, 1));
		when(despachoRepository.save(any(Despacho.class))).thenReturn(despacho);

		assertThat(despachoService.crear(despacho)).isEqualTo(despacho);
	}

	private Despacho crearDespacho(Long id) {
		return new Despacho(
				id,
				10L,
				20L,
				30L,
				"Av. Siempre Viva 123",
				"Santiago",
				"Santiago",
				"PENDIENTE",
				"Blue Express",
				2990,
				LocalDate.of(2026, 7, 1),
				null);
	}
}
