package microservice.despacho.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import microservice.despacho.exception.ResourceNotFoundException;

@Service
public class MicroservicioExternoService {

	private final RestTemplate restTemplate;
	private final String pedidosUrl;
	private final String clientesUrl;
	private final String sucursalesUrl;

	public MicroservicioExternoService(
			RestTemplate restTemplate,
			@Value("${microservices.pedidos.url}") String pedidosUrl,
			@Value("${microservices.clientes.url}") String clientesUrl,
			@Value("${microservices.sucursales.url}") String sucursalesUrl) {
		this.restTemplate = restTemplate;
		this.pedidosUrl = pedidosUrl;
		this.clientesUrl = clientesUrl;
		this.sucursalesUrl = sucursalesUrl;
	}

	public Map<String, Object> obtenerPedido(Long idPedido) {
		return obtenerRecurso(pedidosUrl, idPedido, "pedido");
	}

	public Map<String, Object> obtenerCliente(Long idCliente) {
		return obtenerRecurso(clientesUrl, idCliente, "cliente");
	}

	public Map<String, Object> obtenerSucursal(Long idSucursal) {
		return obtenerRecurso(sucursalesUrl, idSucursal, "sucursal");
	}

	private Map<String, Object> obtenerRecurso(String baseUrl, Long id, String recurso) {
		try {
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
					baseUrl + "/" + id,
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<>() {
					});
			return response.getBody() == null ? Map.of() : response.getBody();
		} catch (RestClientException exception) {
			throw new ResourceNotFoundException("No se pudo rescatar " + recurso + " con id " + id);
		}
	}
}
