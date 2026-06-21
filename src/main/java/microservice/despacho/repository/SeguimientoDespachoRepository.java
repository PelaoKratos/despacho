package microservice.despacho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import microservice.despacho.model.SeguimientoDespacho;

public interface SeguimientoDespachoRepository extends JpaRepository<SeguimientoDespacho, Long> {
	List<SeguimientoDespacho> findByIdDespacho(Long idDespacho);
}
