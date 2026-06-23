package microservice.despacho.service;

import org.springframework.stereotype.Service;

import microservice.despacho.dto.DatosRelacionadosDespacho;
import microservice.despacho.model.Despacho;

@Service
public class DespachoIntegracionService {

	private final DespachoService despachoService;
	private final MicroservicioExternoService microservicioExternoService;

	public DespachoIntegracionService(
			DespachoService despachoService,
			MicroservicioExternoService microservicioExternoService) {
		this.despachoService = despachoService;
		this.microservicioExternoService = microservicioExternoService;
	}

	public DatosRelacionadosDespacho obtenerDatosRelacionados(Long idDespacho) {
		Despacho despacho = despachoService.obtenerPorId(idDespacho);
		return new DatosRelacionadosDespacho(
				despacho,
				microservicioExternoService.obtenerPedido(despacho.getIdPedido()),
				microservicioExternoService.obtenerCliente(despacho.getIdCliente()),
				microservicioExternoService.obtenerSucursal(despacho.getIdSucursal()));
	}
}
