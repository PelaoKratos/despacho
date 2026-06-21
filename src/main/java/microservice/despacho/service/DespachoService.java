package microservice.despacho.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import microservice.despacho.exception.ResourceNotFoundException;
import microservice.despacho.model.Despacho;
import microservice.despacho.repository.DespachoRepository;

@Service
public class DespachoService {

	private static final String ESTADO_PENDIENTE = "PENDIENTE";
	private static final String ESTADO_PREPARACION = "EN_PREPARACION";
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

	public List<Despacho> buscarPorVenta(Long idVenta) {
		return despachoRepository.findByIdVenta(idVenta);
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
		validarFechas(despacho);
		return despachoRepository.save(despacho);
	}

	public Despacho actualizar(Long id, Despacho datosDespacho) {
		Despacho despacho = buscarDespacho(id);
		despacho.setIdVenta(datosDespacho.getIdVenta());
		despacho.setIdCliente(datosDespacho.getIdCliente());
		despacho.setIdSucursal(datosDespacho.getIdSucursal());
		despacho.setDireccionEntrega(datosDespacho.getDireccionEntrega());
		despacho.setComuna(datosDespacho.getComuna());
		despacho.setCiudad(datosDespacho.getCiudad());
		despacho.setEstado(normalizarEstado(datosDespacho.getEstado()));
		despacho.setTransportista(datosDespacho.getTransportista());
		despacho.setCostoDespacho(datosDespacho.getCostoDespacho());
		despacho.setFechaEstimada(datosDespacho.getFechaEstimada());
		despacho.setFechaEntrega(datosDespacho.getFechaEntrega());
		validarFechas(despacho);
		return despachoRepository.save(despacho);
	}

	public Despacho cambiarEstado(Long id, String estado) {
		Despacho despacho = buscarDespacho(id);
		String estadoNormalizado = normalizarEstado(estado);
		despacho.setEstado(estadoNormalizado);
		if (ESTADO_ENTREGADO.equals(estadoNormalizado) && despacho.getFechaEntrega() == null) {
			despacho.setFechaEntrega(LocalDate.now());
		}
		return despachoRepository.save(despacho);
	}

	public Despacho asignarTransportista(Long id, String transportista) {
		if (transportista == null || transportista.isBlank()) {
			throw new IllegalArgumentException("El transportista no puede estar vacio");
		}
		Despacho despacho = buscarDespacho(id);
		despacho.setTransportista(transportista.trim());
		if (ESTADO_PENDIENTE.equals(despacho.getEstado())) {
			despacho.setEstado(ESTADO_PREPARACION);
		}
		return despachoRepository.save(despacho);
	}

	public Despacho marcarEnTransito(Long id) {
		Despacho despacho = buscarDespacho(id);
		if (despacho.getTransportista() == null || despacho.getTransportista().isBlank()) {
			throw new IllegalArgumentException("No se puede iniciar el despacho sin transportista");
		}
		despacho.setEstado(ESTADO_EN_TRANSITO);
		return despachoRepository.save(despacho);
	}

	public Despacho confirmarEntrega(Long id) {
		Despacho despacho = buscarDespacho(id);
		despacho.setEstado(ESTADO_ENTREGADO);
		despacho.setFechaEntrega(LocalDate.now());
		return despachoRepository.save(despacho);
	}

	public Despacho cancelar(Long id) {
		Despacho despacho = buscarDespacho(id);
		if (ESTADO_ENTREGADO.equals(despacho.getEstado())) {
			throw new IllegalArgumentException("No se puede cancelar un despacho entregado");
		}
		despacho.setEstado(ESTADO_CANCELADO);
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
		if (!List.of(ESTADO_PENDIENTE, ESTADO_PREPARACION, ESTADO_EN_TRANSITO, ESTADO_ENTREGADO, ESTADO_CANCELADO)
				.contains(estadoNormalizado)) {
			throw new IllegalArgumentException("Estado de despacho no valido: " + estado);
		}
		return estadoNormalizado;
	}

	private void validarFechas(Despacho despacho) {
		if (despacho.getFechaEntrega() != null && despacho.getFechaEstimada() != null
				&& despacho.getFechaEntrega().isBefore(despacho.getFechaEstimada())) {
			throw new IllegalArgumentException("La fecha de entrega no puede ser anterior a la fecha estimada");
		}
	}
}
