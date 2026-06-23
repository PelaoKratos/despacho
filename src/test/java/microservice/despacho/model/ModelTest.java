package microservice.despacho.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class ModelTest {

	@Test
	void despachoCreaValoresPorDefectoSoloCuandoFaltan() {
		Despacho despacho = new Despacho();

		despacho.crearDespacho();

		assertThat(despacho.getFechaDespacho()).isNotNull();
		assertThat(despacho.getEstado()).isEqualTo("PENDIENTE");

		LocalDateTime fecha = LocalDateTime.of(2026, 6, 22, 10, 0);
		despacho.setFechaDespacho(fecha);
		despacho.setEstado("EN_TRANSITO");

		despacho.crearDespacho();

		assertThat(despacho.getFechaDespacho()).isEqualTo(fecha);
		assertThat(despacho.getEstado()).isEqualTo("EN_TRANSITO");

		despacho.setEstado(" ");

		despacho.crearDespacho();

		assertThat(despacho.getEstado()).isEqualTo("PENDIENTE");
	}

	@Test
	void despachoActualizaEstadoEntregaYRelaciones() {
		Despacho despacho = despachoCompleto();
		DetalleDespacho detalle = new DetalleDespacho();
		ParadaRuta parada = new ParadaRuta();
		SeguimientoDespacho seguimiento = new SeguimientoDespacho();

		despacho.actualizarEstado("EN_TRANSITO");
		despacho.asignarRuta();
		despacho.agregarDetalle(detalle);
		despacho.agregarDetalle(null);
		despacho.agregarParada(parada);
		despacho.agregarParada(null);
		despacho.agregarSeguimiento(seguimiento);
		despacho.agregarSeguimiento(null);
		despacho.confirmarEntrega();

		assertThat(despacho.getEstado()).isEqualTo("ENTREGADO");
		assertThat(despacho.getFechaEntrega()).isNotNull();
		assertThat(despacho.getDetalles()).containsExactly(detalle);
		assertThat(despacho.getParadas()).containsExactly(parada);
		assertThat(despacho.getSeguimientos()).containsExactly(seguimiento);
		assertThat(detalle.getDespacho()).isSameAs(despacho);
		assertThat(parada.getDespacho()).isSameAs(despacho);
		assertThat(seguimiento.getDespacho()).isSameAs(despacho);
		assertThat(detalle.getIdDespacho()).isEqualTo(despacho.getIdDespacho());
		assertThat(parada.getIdDespacho()).isEqualTo(despacho.getIdDespacho());
		assertThat(seguimiento.getIdDespacho()).isEqualTo(despacho.getIdDespacho());
	}

	@Test
	void detalleDespachoGestionaEstadoCantidadYDespacho() {
		DetalleDespacho detalle = new DetalleDespacho();
		Despacho despacho = despachoCompleto();

		detalle.agregarDetalle();
		detalle.modificarCantidad(3);
		detalle.setDespacho(despacho);

		assertThat(detalle.getEstado()).isEqualTo("AGREGADO");
		assertThat(detalle.getCantidad()).isEqualTo(3);
		assertThat(detalle.getDespacho()).isSameAs(despacho);
		assertThat(detalle.getIdDespacho()).isEqualTo(1L);

		detalle.quitarDetalle();
		detalle.setDespacho(null);

		assertThat(detalle.getEstado()).isEqualTo("ELIMINADO");
		assertThat(detalle.getDespacho()).isNull();
		assertThat(detalle.getIdDespacho()).isNull();
		assertThatThrownBy(() -> detalle.modificarCantidad(0))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("La cantidad debe ser mayor a cero");
	}

	@Test
	void paradaRutaGestionaLlegadaEstadoDespachoYRuta() {
		ParadaRuta parada = new ParadaRuta();
		Despacho despacho = despachoCompleto();
		RutaEntrega ruta = rutaCompleta();

		parada.registrarParada();
		parada.cambiarEstado("EN_CAMINO");
		parada.setDespacho(despacho);
		parada.setRuta(ruta);
		parada.actualizarLlegada();

		assertThat(parada.getEstado()).isEqualTo("COMPLETADA");
		assertThat(parada.getHoraLlegada()).isNotNull();
		assertThat(parada.getDespacho()).isSameAs(despacho);
		assertThat(parada.getRuta()).isSameAs(ruta);
		assertThat(parada.getIdDespacho()).isEqualTo(1L);
		assertThat(parada.getIdRuta()).isEqualTo(5L);
		assertThat(parada.getIdTransportista()).isEqualTo(9L);

		Transportista transportista = transportistaCompleto();
		parada.setTransportista(transportista);

		assertThat(parada.getTransportista()).isSameAs(transportista);
		assertThat(parada.getIdTransportista()).isEqualTo(9L);

		parada.setDespacho(null);
		parada.setRuta(null);
		parada.setTransportista(null);

		assertThat(parada.getIdDespacho()).isNull();
		assertThat(parada.getIdRuta()).isNull();
		assertThat(parada.getIdTransportista()).isNull();
	}

	@Test
	void rutaEntregaCreaOptimizaCalculaYRelacionaTransportistaParadas() {
		RutaEntrega ruta = new RutaEntrega();
		Transportista transportista = transportistaCompleto();
		ParadaRuta parada = new ParadaRuta();
		ruta.setIdRuta(5L);
		ruta.setDistanciaKm(42.5);

		ruta.crearRuta();
		ruta.optimizarRuta();
		ruta.actualizarEstado("EN_REPARTO");
		ruta.setTransportista(transportista);
		ruta.agregarParada(parada);
		ruta.agregarParada(null);

		assertThat(ruta.getFechaRuta()).isNotNull();
		assertThat(ruta.getEstado()).isEqualTo("EN_REPARTO");
		assertThat(ruta.calcularDistancia()).isEqualTo(42.5);
		assertThat(ruta.getTransportista()).isSameAs(transportista);
		assertThat(ruta.getIdTransportista()).isEqualTo(9L);
		assertThat(ruta.getParadas()).containsExactly(parada);
		assertThat(parada.getRuta()).isSameAs(ruta);
		assertThat(parada.getIdRuta()).isEqualTo(5L);

		ruta.setTransportista(null);

		assertThat(ruta.getTransportista()).isNull();
		assertThat(ruta.getIdTransportista()).isNull();
	}

	@Test
	void rutaEntregaMantieneFechaSiYaExiste() {
		RutaEntrega ruta = new RutaEntrega();
		LocalDate fecha = LocalDate.of(2026, 6, 22);
		ruta.setFechaRuta(fecha);

		ruta.crearRuta();

		assertThat(ruta.getFechaRuta()).isEqualTo(fecha);
		assertThat(ruta.getEstado()).isEqualTo("CREADA");
	}

	@Test
	void seguimientoDespachoRegistraActualizaYConsultaEstado() {
		SeguimientoDespacho seguimiento = new SeguimientoDespacho();
		Despacho despacho = despachoCompleto();

		seguimiento.setEstado("PENDIENTE");
		seguimiento.registrarSeguimiento();
		LocalDateTime primeraFecha = seguimiento.getFechaRegistro();
		seguimiento.actualizarUbicacion("Bodega central");
		seguimiento.setDespacho(despacho);

		assertThat(primeraFecha).isNotNull();
		assertThat(seguimiento.getUbicacion()).isEqualTo("Bodega central");
		assertThat(seguimiento.getFechaRegistro()).isNotNull();
		assertThat(seguimiento.consultarEstado()).isEqualTo("PENDIENTE");
		assertThat(seguimiento.getDespacho()).isSameAs(despacho);
		assertThat(seguimiento.getIdDespacho()).isEqualTo(1L);

		seguimiento.setDespacho(null);

		assertThat(seguimiento.getDespacho()).isNull();
		assertThat(seguimiento.getIdDespacho()).isNull();
	}

	@Test
	void seguimientoDespachoMantieneFechaRegistroSiYaExiste() {
		SeguimientoDespacho seguimiento = new SeguimientoDespacho();
		LocalDateTime fecha = LocalDateTime.of(2026, 6, 22, 11, 0);
		seguimiento.setFechaRegistro(fecha);

		seguimiento.registrarSeguimiento();

		assertThat(seguimiento.getFechaRegistro()).isEqualTo(fecha);
	}

	@Test
	void transportistaGestionaEstadoYDatosEditables() {
		Transportista transportista = new Transportista();

		transportista.registrarTransportista();
		transportista.modificarTransportista("Ana Perez", "+56911111111", "BBCC22", "Camioneta");
		transportista.cambiarEstado("INACTIVO");

		assertThat(transportista.getEstado()).isEqualTo("INACTIVO");
		assertThat(transportista.getNombre()).isEqualTo("Ana Perez");
		assertThat(transportista.getTelefono()).isEqualTo("+56911111111");
		assertThat(transportista.getPatenteVehiculo()).isEqualTo("BBCC22");
		assertThat(transportista.getTipoVehiculo()).isEqualTo("Camioneta");
	}

	@Test
	void constructoresConTodosLosArgumentosAsignanCampos() {
		Despacho despacho = despachoCompleto();
		DetalleDespacho detalle = new DetalleDespacho(2L, 1L, 30L, 4, "AGREGADO", despacho);
		Transportista transportista = transportistaCompleto();
		RutaEntrega ruta = rutaCompleta();
		ParadaRuta parada = new ParadaRuta(6L, 5L, 9L, 1L, "Los Aromos 123", 1,
				LocalDateTime.of(2026, 6, 22, 12, 0), null, "REGISTRADA", despacho, ruta, transportista);
		SeguimientoDespacho seguimiento = new SeguimientoDespacho(7L, 1L,
				LocalDateTime.of(2026, 6, 22, 13, 0), "Sucursal norte", "EN_TRANSITO",
				"Sin novedades", despacho);

		assertThat(despacho.getIdDespacho()).isEqualTo(1L);
		assertThat(detalle.getIdDetalleDespacho()).isEqualTo(2L);
		assertThat(transportista.getIdTransportista()).isEqualTo(9L);
		assertThat(ruta.getIdRuta()).isEqualTo(5L);
		assertThat(parada.getIdParada()).isEqualTo(6L);
		assertThat(parada.getIdTransportista()).isEqualTo(9L);
		assertThat(seguimiento.getIdSeguimiento()).isEqualTo(7L);
	}

	@Test
	void metodosLombokBasicosFuncionanSinRomperRelaciones() {
		Despacho despacho = despachoCompleto();
		Despacho mismoDespacho = despachoCompleto();
		DetalleDespacho detalle = new DetalleDespacho();
		detalle.setIdDetalleDespacho(2L);
		Transportista transportista = transportistaCompleto();

		assertThat(despacho).isEqualTo(mismoDespacho);
		assertThat(despacho.hashCode()).isEqualTo(mismoDespacho.hashCode());
		assertThat(despacho.toString()).contains("idDespacho=1");
		assertThat(detalle.toString()).contains("idDetalleDespacho=2");
		assertThat(transportista.toString()).contains("idTransportista=9");
	}

	private Despacho despachoCompleto() {
		return new Despacho(
				1L,
				10L,
				20L,
				30L,
				"Av. Siempre Viva 123",
				LocalDateTime.of(2026, 6, 22, 9, 0),
				LocalDateTime.of(2026, 6, 23, 18, 0),
				null,
				"PENDIENTE",
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>());
	}

	private RutaEntrega rutaCompleta() {
		return new RutaEntrega(
				5L,
				9L,
				"Ruta norte",
				"Santiago",
				"Valparaiso",
				120.5,
				90,
				LocalDate.of(2026, 6, 22),
				"CREADA",
				null,
				new ArrayList<>());
	}

	private Transportista transportistaCompleto() {
		return new Transportista(
				9L,
				"11.111.111-1",
				"Juan Soto",
				"+56912345678",
				"AABB11",
				"Furgon",
				"ACTIVO",
				new ArrayList<>());
	}
}
