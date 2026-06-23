package microservice.despacho.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import microservice.despacho.exception.ResourceNotFoundException;
import microservice.despacho.model.Despacho;
import microservice.despacho.model.DetalleDespacho;
import microservice.despacho.model.ParadaRuta;
import microservice.despacho.model.SeguimientoDespacho;
import microservice.despacho.repository.DespachoRepository;

@Service
public class DespachoService {

	private static final String ESTADO_PENDIENTE = "PENDIENTE";
	private static final String ESTADO_RUTA_ASIGNADA = "RUTA_ASIGNADA";
	private static final String ESTADO_EN_TRANSITO = "EN_TRANSITO";
	private static final String ESTADO_ENTREGADO = "ENTREGADO";
	private static final String ESTADO_CANCELADO = "CANCELADO";

	private final DespachoRepository despachoRepository;

	public DespachoService(DespachoRepository despachoRepository) {
		this.despachoRepository = despachoRepository;
	}

	public List<Despacho> listar() {
		return despachoRepository.findAll();
	}

	public Despacho obtenerPorId(Long id) {
		return buscarDespacho(id);
	}

	public List<Despacho> buscarPorPedido(Long idPedido) {
		return despachoRepository.findByIdPedido(idPedido);
	}

	public List<Despacho> buscarPorCliente(Long idCliente) {
		return despachoRepository.findByIdCliente(idCliente);
	}

	public List<Despacho> buscarPorSucursal(Long idSucursal) {
		return despachoRepository.findByIdSucursal(idSucursal);
	}

	public List<Despacho> buscarPorEstado(String estado) {
		return despachoRepository.findByEstado(normalizarEstado(estado));
	}

	public Despacho crear(Despacho despacho) {
		if (despacho.getEstado() == null || despacho.getEstado().isBlank()) {
			despacho.setEstado(ESTADO_PENDIENTE);
		} else {
			despacho.setEstado(normalizarEstado(despacho.getEstado()));
		}
		if (despacho.getFechaDespacho() == null) {
			despacho.setFechaDespacho(LocalDateTime.now());
		}
		vincularRelaciones(despacho);
		validarFechas(despacho);
		return despachoRepository.save(despacho);
	}

	public List<Despacho> cargarDatosDemo() {
		List<Despacho> despachosExistentes = despachoRepository.findAll();
		if (!despachosExistentes.isEmpty()) {
			return despachosExistentes;
		}
		return List.of(
				crear(crearDespachoDemo(1001L, 2001L, 3001L, "Av. Providencia 1234", ESTADO_PENDIENTE)),
				crear(crearDespachoDemo(1002L, 2002L, 3002L, "Gran Avenida 5678", ESTADO_RUTA_ASIGNADA)));
	}

	public Despacho actualizar(Long id, Despacho datosDespacho) {
		Despacho despacho = buscarDespacho(id);
		despacho.setIdPedido(datosDespacho.getIdPedido());
		despacho.setIdCliente(datosDespacho.getIdCliente());
		despacho.setIdSucursal(datosDespacho.getIdSucursal());
		despacho.setDireccionEntrega(datosDespacho.getDireccionEntrega());
		despacho.setFechaDespacho(datosDespacho.getFechaDespacho());
		despacho.setFechaEstimadaEntrega(datosDespacho.getFechaEstimadaEntrega());
		despacho.setFechaEntrega(datosDespacho.getFechaEntrega());
		despacho.setEstado(normalizarEstado(datosDespacho.getEstado()));
		despacho.setDetalles(datosDespacho.getDetalles());
		despacho.setParadas(datosDespacho.getParadas());
		despacho.setSeguimientos(datosDespacho.getSeguimientos());
		vincularRelaciones(despacho);
		validarFechas(despacho);
		return despachoRepository.save(despacho);
	}

	public Despacho cambiarEstado(Long id, String estado) {
		Despacho despacho = buscarDespacho(id);
		String estadoNormalizado = normalizarEstado(estado);
		despacho.actualizarEstado(estadoNormalizado);
		if (ESTADO_ENTREGADO.equals(estadoNormalizado) && despacho.getFechaEntrega() == null) {
			despacho.setFechaEntrega(LocalDateTime.now());
		}
		return despachoRepository.save(despacho);
	}

	public Despacho asignarRuta(Long id) {
		Despacho despacho = buscarDespacho(id);
		despacho.actualizarEstado(ESTADO_RUTA_ASIGNADA);
		return despachoRepository.save(despacho);
	}

	public Despacho marcarEnTransito(Long id) {
		Despacho despacho = buscarDespacho(id);
		despacho.actualizarEstado(ESTADO_EN_TRANSITO);
		return despachoRepository.save(despacho);
	}

	public Despacho confirmarEntrega(Long id) {
		Despacho despacho = buscarDespacho(id);
		despacho.confirmarEntrega();
		return despachoRepository.save(despacho);
	}

	public Despacho cancelar(Long id) {
		Despacho despacho = buscarDespacho(id);
		if (ESTADO_ENTREGADO.equals(despacho.getEstado())) {
			throw new IllegalArgumentException("No se puede cancelar un despacho entregado");
		}
		despacho.actualizarEstado(ESTADO_CANCELADO);
		return despachoRepository.save(despacho);
	}

	public void eliminar(Long id) {
		Despacho despacho = buscarDespacho(id);
		despachoRepository.delete(despacho);
	}

	private Despacho buscarDespacho(Long id) {
		return despachoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el despacho con id " + id));
	}

	private String normalizarEstado(String estado) {
		if (estado == null || estado.isBlank()) {
			throw new IllegalArgumentException("El estado no puede estar vacio");
		}
		String estadoNormalizado = estado.trim().toUpperCase();
		if (!List.of(ESTADO_PENDIENTE, ESTADO_RUTA_ASIGNADA, ESTADO_EN_TRANSITO, ESTADO_ENTREGADO, ESTADO_CANCELADO)
				.contains(estadoNormalizado)) {
			throw new IllegalArgumentException("Estado de despacho no valido: " + estado);
		}
		return estadoNormalizado;
	}

	private void validarFechas(Despacho despacho) {
		if (despacho.getFechaEntrega() != null && despacho.getFechaEstimadaEntrega() != null
				&& despacho.getFechaEntrega().isBefore(despacho.getFechaEstimadaEntrega())) {
			throw new IllegalArgumentException("La fecha de entrega no puede ser anterior a la fecha estimada");
		}
	}

	private void vincularRelaciones(Despacho despacho) {
		if (despacho.getDetalles() != null) {
			despacho.getDetalles().forEach(detalle -> detalle.setDespacho(despacho));
		}
		if (despacho.getParadas() != null) {
			despacho.getParadas().forEach(parada -> parada.setDespacho(despacho));
		}
		if (despacho.getSeguimientos() != null) {
			despacho.getSeguimientos().forEach(seguimiento -> seguimiento.setDespacho(despacho));
		}
	}

	private Despacho crearDespachoDemo(
			Long idPedido,
			Long idCliente,
			Long idSucursal,
			String direccionEntrega,
			String estado) {
		Despacho despacho = new Despacho();
		despacho.setIdPedido(idPedido);
		despacho.setIdCliente(idCliente);
		despacho.setIdSucursal(idSucursal);
		despacho.setDireccionEntrega(direccionEntrega);
		despacho.setFechaDespacho(LocalDateTime.now());
		despacho.setFechaEstimadaEntrega(LocalDateTime.now().plusDays(2));
		despacho.setEstado(estado);
		despacho.setDetalles(new ArrayList<>());
		despacho.setParadas(new ArrayList<>());
		despacho.setSeguimientos(new ArrayList<>());

		DetalleDespacho detalle = new DetalleDespacho();
		detalle.setIdProducto(501L);
		detalle.setCantidad(2);
		detalle.setEstado("AGREGADO");
		despacho.agregarDetalle(detalle);

		ParadaRuta parada = new ParadaRuta();
		parada.setDireccion(direccionEntrega);
		parada.setOrdenParada(1);
		parada.setHoraEstimada(LocalDateTime.now().plusDays(1));
		parada.setEstado("REGISTRADA");
		despacho.agregarParada(parada);

		SeguimientoDespacho seguimiento = new SeguimientoDespacho();
		seguimiento.setFechaRegistro(LocalDateTime.now());
		seguimiento.setUbicacion("Centro de distribucion");
		seguimiento.setEstado(estado);
		seguimiento.setObservacion("Dato demo cargado desde despacho");
		despacho.agregarSeguimiento(seguimiento);
		return despacho;
	}
}
