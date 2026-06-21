package microservice.despacho.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import microservice.despacho.model.Despacho;
import microservice.despacho.service.DespachoService;

@RestController
@RequestMapping("/api/despachos")
public class DespachoController {

	private final DespachoService despachoService;

	public DespachoController(DespachoService despachoService) {
		this.despachoService = despachoService;
	}

	@GetMapping
	public List<Despacho> listar() {
		return despachoService.listar();
	}

	@GetMapping("/{id}")
	public Despacho obtenerPorId(@PathVariable Long id) {
		return despachoService.obtenerPorId(id);
	}

	@GetMapping("/venta/{idVenta}")
	public List<Despacho> buscarPorVenta(@PathVariable Long idVenta) {
		return despachoService.buscarPorVenta(idVenta);
	}

	@GetMapping("/cliente/{idCliente}")
	public List<Despacho> buscarPorCliente(@PathVariable Long idCliente) {
		return despachoService.buscarPorCliente(idCliente);
	}

	@GetMapping("/sucursal/{idSucursal}")
	public List<Despacho> buscarPorSucursal(@PathVariable Long idSucursal) {
		return despachoService.buscarPorSucursal(idSucursal);
	}

	@GetMapping("/estado/{estado}")
	public List<Despacho> buscarPorEstado(@PathVariable String estado) {
		return despachoService.buscarPorEstado(estado);
	}

	@PostMapping
	public Despacho crear(@Valid @RequestBody Despacho despacho) {
		return despachoService.crear(despacho);
	}

	@PutMapping("/{id}")
	public Despacho actualizar(@PathVariable Long id, @Valid @RequestBody Despacho despacho) {
		return despachoService.actualizar(id, despacho);
	}

	@PatchMapping("/{id}/estado")
	public Despacho cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> request) {
		return despachoService.cambiarEstado(id, request.get("estado"));
	}

	@PatchMapping("/{id}/transportista")
	public Despacho asignarTransportista(@PathVariable Long id, @RequestBody Map<String, String> request) {
		return despachoService.asignarTransportista(id, request.get("transportista"));
	}

	@PatchMapping("/{id}/en-transito")
	public Despacho marcarEnTransito(@PathVariable Long id) {
		return despachoService.marcarEnTransito(id);
	}

	@PatchMapping("/{id}/entrega")
	public Despacho confirmarEntrega(@PathVariable Long id) {
		return despachoService.confirmarEntrega(id);
	}

	@PatchMapping("/{id}/cancelacion")
	public Despacho cancelar(@PathVariable Long id) {
		return despachoService.cancelar(id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		despachoService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
