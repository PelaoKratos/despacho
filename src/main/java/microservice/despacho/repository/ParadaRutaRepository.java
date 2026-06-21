package microservice.despacho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import microservice.despacho.model.ParadaRuta;

public interface ParadaRutaRepository extends JpaRepository<ParadaRuta, Long> {
	List<ParadaRuta> findByIdDespacho(Long idDespacho);
	List<ParadaRuta> findByIdRuta(Long idRuta);
}
