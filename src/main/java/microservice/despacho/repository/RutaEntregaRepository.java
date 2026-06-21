package microservice.despacho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import microservice.despacho.model.RutaEntrega;

public interface RutaEntregaRepository extends JpaRepository<RutaEntrega, Long> {
	List<RutaEntrega> findByIdTransportista(Long idTransportista);
}
