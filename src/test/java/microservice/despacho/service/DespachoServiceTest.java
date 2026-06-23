package microservice.despacho.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import microservice.despacho.exception.ResourceNotFoundException;
import microservice.despacho.model.Despacho;
import microservice.despacho.model.DetalleDespacho;
import microservice.despacho.model.ParadaRuta;
import microservice.despacho.model.SeguimientoDespacho;
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
	void listarYObtenerDeleganEnRepositorio() {
		Despacho despacho = crearDespacho(1L);
		when(despachoRepository.findAll()).thenReturn(List.of(despacho));
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));

		assertThat(despachoService.listar()).containsExactly(despacho);
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
	void busquedasDeleganEnRepositorio() {
		List<Despacho> despachos = List.of(crearDespacho(1L));
		when(despachoRepository.findByIdPedido(10L)).thenReturn(despachos);
		when(despachoRepository.findByIdCliente(20L)).thenReturn(despachos);
		when(despachoRepository.findByIdSucursal(30L)).thenReturn(despachos);
		when(despachoRepository.findByEstado("PENDIENTE")).thenReturn(despachos);

		assertThat(despachoService.buscarPorPedido(10L)).isEqualTo(despachos);
		assertThat(despachoService.buscarPorCliente(20L)).isEqualTo(despachos);
		assertThat(despachoService.buscarPorSucursal(30L)).isEqualTo(despachos);
		assertThat(despachoService.buscarPorEstado(" pendiente ")).isEqualTo(despachos);
	}

	@Test
	void crearAsignaEstadoYFechaDespachoCuandoNoVienen() {
		Despacho despacho = crearDespacho(null);
		despacho.setEstado(null);
		despacho.setFechaDespacho(null);
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.crear(despacho);

		assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");
		assertThat(resultado.getFechaDespacho()).isNotNull();
	}

	@Test
	void crearAsignaPendienteCuandoEstadoVieneEnBlanco() {
		Despacho despacho = crearDespacho(null);
		despacho.setEstado(" ");
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		assertThat(despachoService.crear(despacho).getEstado()).isEqualTo("PENDIENTE");
	}

	@Test
	void crearNormalizaEstadoYVinculaRelaciones() {
		Despacho despacho = crearDespacho(null);
		despacho.setEstado(" en_transito ");
		DetalleDespacho detalle = new DetalleDespacho();
		ParadaRuta parada = new ParadaRuta();
		SeguimientoDespacho seguimiento = new SeguimientoDespacho();
		despacho.setDetalles(List.of(detalle));
		despacho.setParadas(List.of(parada));
		despacho.setSeguimientos(List.of(seguimiento));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.crear(despacho);

		assertThat(resultado.getEstado()).isEqualTo("EN_TRANSITO");
		assertThat(detalle.getDespacho()).isSameAs(despacho);
		assertThat(parada.getDespacho()).isSameAs(despacho);
		assertThat(seguimiento.getDespacho()).isSameAs(despacho);
	}

	@Test
	void crearRechazaFechaEntregaAnteriorAFechaEstimada() {
		Despacho despacho = crearDespacho(null);
		despacho.setFechaEstimadaEntrega(LocalDateTime.of(2026, 7, 2, 10, 0));
		despacho.setFechaEntrega(LocalDateTime.of(2026, 7, 1, 10, 0));

		assertThatThrownBy(() -> despachoService.crear(despacho))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("La fecha de entrega no puede ser anterior a la fecha estimada");
	}

	@Test
	void actualizarModificaTodosLosDatos() {
		Despacho existente = crearDespacho(1L);
		Despacho datos = crearDespacho(2L);
		datos.setIdPedido(55L);
		datos.setIdCliente(66L);
		datos.setIdSucursal(77L);
		datos.setDireccionEntrega("Nueva direccion");
		datos.setEstado("cancelado");
		datos.setFechaDespacho(LocalDateTime.of(2026, 8, 1, 9, 0));
		datos.setFechaEstimadaEntrega(LocalDateTime.of(2026, 8, 2, 9, 0));
		datos.setFechaEntrega(LocalDateTime.of(2026, 8, 3, 9, 0));
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(despachoRepository.save(existente)).thenReturn(existente);

		Despacho resultado = despachoService.actualizar(1L, datos);

		assertThat(resultado.getIdPedido()).isEqualTo(55L);
		assertThat(resultado.getIdCliente()).isEqualTo(66L);
		assertThat(resultado.getIdSucursal()).isEqualTo(77L);
		assertThat(resultado.getDireccionEntrega()).isEqualTo("Nueva direccion");
		assertThat(resultado.getEstado()).isEqualTo("CANCELADO");
		assertThat(resultado.getFechaDespacho()).isEqualTo(LocalDateTime.of(2026, 8, 1, 9, 0));
		assertThat(resultado.getFechaEstimadaEntrega()).isEqualTo(LocalDateTime.of(2026, 8, 2, 9, 0));
		assertThat(resultado.getFechaEntrega()).isEqualTo(LocalDateTime.of(2026, 8, 3, 9, 0));
	}

	@Test
	void actualizarPermiteListasNulas() {
		Despacho existente = crearDespacho(1L);
		Despacho datos = crearDespacho(2L);
		datos.setDetalles(null);
		datos.setParadas(null);
		datos.setSeguimientos(null);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(existente));
		when(despachoRepository.save(existente)).thenReturn(existente);

		Despacho resultado = despachoService.actualizar(1L, datos);

		assertThat(resultado.getDetalles()).isNull();
		assertThat(resultado.getParadas()).isNull();
		assertThat(resultado.getSeguimientos()).isNull();
	}

	@Test
	void cambiarEstadoAEntregadoRegistraFechaEntregaSiNoExiste() {
		Despacho despacho = crearDespacho(1L);
		despacho.setFechaEntrega(null);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.cambiarEstado(1L, "entregado");

		assertThat(resultado.getEstado()).isEqualTo("ENTREGADO");
		assertThat(resultado.getFechaEntrega()).isNotNull();
	}

	@Test
	void cambiarEstadoAEntregadoMantieneFechaEntregaSiYaExiste() {
		Despacho despacho = crearDespacho(1L);
		LocalDateTime fechaEntrega = LocalDateTime.of(2026, 7, 5, 12, 0);
		despacho.setFechaEntrega(fechaEntrega);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		assertThat(despachoService.cambiarEstado(1L, "ENTREGADO").getFechaEntrega()).isEqualTo(fechaEntrega);
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
	void buscarPorEstadoRechazaEstadoVacioONoValido() {
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
	void asignarRutaYMarcarEnTransitoActualizanEstado() {
		Despacho despacho = crearDespacho(1L);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		assertThat(despachoService.asignarRuta(1L).getEstado()).isEqualTo("RUTA_ASIGNADA");
		assertThat(despachoService.marcarEnTransito(1L).getEstado()).isEqualTo("EN_TRANSITO");
	}

	@Test
	void confirmarEntregaActualizaEstadoYFecha() {
		Despacho despacho = crearDespacho(1L);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
		when(despachoRepository.save(despacho)).thenReturn(despacho);

		Despacho resultado = despachoService.confirmarEntrega(1L);

		assertThat(resultado.getEstado()).isEqualTo("ENTREGADO");
		assertThat(resultado.getFechaEntrega()).isNotNull();
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

		assertThat(despachoService.cancelar(1L).getEstado()).isEqualTo("CANCELADO");
	}

	@Test
	void eliminarBorraDespachoExistente() {
		Despacho despacho = crearDespacho(1L);
		when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));

		despachoService.eliminar(1L);

		verify(despachoRepository).delete(despacho);
	}

	@Test
	void crearPermiteFechaEstimadaNulaCuandoNoDebeCompararFechas() {
		Despacho despacho = crearDespacho(null);
		despacho.setFechaEstimadaEntrega(null);
		despacho.setFechaEntrega(LocalDateTime.of(2026, 7, 1, 10, 0));
		when(despachoRepository.save(any(Despacho.class))).thenReturn(despacho);

		assertThat(despachoService.crear(despacho)).isEqualTo(despacho);
	}

	@Test
	void cargarDatosDemoRetornaExistentesCuandoYaHayDespachos() {
		List<Despacho> existentes = List.of(crearDespacho(1L));
		when(despachoRepository.findAll()).thenReturn(existentes);

		assertThat(despachoService.cargarDatosDemo()).isEqualTo(existentes);
	}

	@Test
	void cargarDatosDemoInsertaDespachosConRelacionesCuandoNoHayDatos() {
		when(despachoRepository.findAll()).thenReturn(List.of());
		when(despachoRepository.save(any(Despacho.class))).thenAnswer(invocation -> invocation.getArgument(0));

		List<Despacho> resultado = despachoService.cargarDatosDemo();

		assertThat(resultado).hasSize(2);
		assertThat(resultado).extracting(Despacho::getIdPedido).containsExactly(1001L, 1002L);
		assertThat(resultado).extracting(Despacho::getIdCliente).containsExactly(2001L, 2002L);
		assertThat(resultado).extracting(Despacho::getIdSucursal).containsExactly(3001L, 3002L);
		assertThat(resultado).extracting(Despacho::getEstado).containsExactly("PENDIENTE", "RUTA_ASIGNADA");
		assertThat(resultado.get(0).getDetalles()).hasSize(1);
		assertThat(resultado.get(0).getParadas()).hasSize(1);
		assertThat(resultado.get(0).getSeguimientos()).hasSize(1);
		assertThat(resultado.get(0).getDetalles().get(0).getDespacho()).isSameAs(resultado.get(0));
		assertThat(resultado.get(0).getParadas().get(0).getDespacho()).isSameAs(resultado.get(0));
		assertThat(resultado.get(0).getSeguimientos().get(0).getDespacho()).isSameAs(resultado.get(0));
	}

	private Despacho crearDespacho(Long id) {
		return new Despacho(
				id,
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
