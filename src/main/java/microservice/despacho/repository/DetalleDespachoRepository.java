package microservice.despacho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import microservice.despacho.model.DetalleDespacho;

public interface DetalleDespachoRepository extends JpaRepository<DetalleDespacho, Long> {
	List<DetalleDespacho> findByIdDespacho(Long idDespacho);
}
