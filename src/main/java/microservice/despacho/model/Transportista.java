package microservice.despacho.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transportista {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idTransportista;

	@NotBlank(message = "El rut no puede estar vacio")
	@Column(nullable = false, unique = true)
	private String rut;

	@NotBlank(message = "El nombre no puede estar vacio")
	@Column(nullable = false)
	private String nombre;

	@NotBlank(message = "El telefono no puede estar vacio")
	@Column(nullable = false)
	private String telefono;

	@NotBlank(message = "La patente del vehiculo no puede estar vacia")
	@Column(nullable = false)
	private String patenteVehiculo;

	@NotBlank(message = "El tipo de vehiculo no puede estar vacio")
	@Column(nullable = false)
	private String tipoVehiculo;

	@NotBlank(message = "El estado no puede estar vacio")
	@Column(nullable = false)
	private String estado;

	@OneToMany(mappedBy = "transportista", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("transportista")
	private List<RutaEntrega> rutas = new ArrayList<>();

	public void registrarTransportista() {
		estado = "ACTIVO";
	}

	public void modificarTransportista(String nombre, String telefono, String patenteVehiculo, String tipoVehiculo) {
		this.nombre = nombre;
		this.telefono = telefono;
		this.patenteVehiculo = patenteVehiculo;
		this.tipoVehiculo = tipoVehiculo;
	}

	public void cambiarEstado(String estado) {
		this.estado = estado;
	}
}
