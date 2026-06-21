package microservice.despacho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import microservice.despacho.model.Transportista;

public interface TransportistaRepository extends JpaRepository<Transportista, Long> {
	List<Transportista> findByEstado(String estado);
}
