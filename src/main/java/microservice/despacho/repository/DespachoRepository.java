package microservice.despacho.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import microservice.despacho.model.Despacho;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {

	List<Despacho> findByIdPedido(Long idPedido);

	List<Despacho> findByIdCliente(Long idCliente);

	List<Despacho> findByIdSucursal(Long idSucursal);

	List<Despacho> findByEstado(String estado);
}
