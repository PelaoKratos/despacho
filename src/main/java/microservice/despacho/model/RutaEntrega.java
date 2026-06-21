package microservice.despacho.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RutaEntrega {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idRuta;

	@Column(name = "id_transportista", insertable = false, updatable = false)
	private Long idTransportista;

	@NotBlank(message = "El nombre de ruta es obligatorio")
	@Column(nullable = false)
	private String nombreRuta;

	@NotBlank(message = "El origen es obligatorio")
	@Column(nullable = false)
	private String origen;

	@NotBlank(message = "El destino es obligatorio")
	@Column(nullable = false)
	private String destino;

	@PositiveOrZero(message = "La distancia no puede ser negativa")
	private double distanciaKm;

	@PositiveOrZero(message = "El tiempo estimado no puede ser negativo")
	private int tiempoEstimado;

	private LocalDate fechaRuta;

	@NotBlank(message = "El estado no puede estar vacio")
	@Column(nullable = false)
	private String estado;

	@ManyToOne
	@JoinColumn(name = "id_transportista")
	private Transportista transportista;

	@OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("ruta")
	private List<ParadaRuta> paradas = new ArrayList<>();

	public void crearRuta() {
		if (fechaRuta == null) {
			fechaRuta = LocalDate.now();
		}
		estado = "CREADA";
	}

	public double calcularDistancia() {
		return distanciaKm;
	}

	public void optimizarRuta() {
		estado = "OPTIMIZADA";
	}

	public void actualizarEstado(String estado) {
		this.estado = estado;
	}

	public void setTransportista(Transportista transportista) {
		this.transportista = transportista;
		this.idTransportista = transportista != null ? transportista.getIdTransportista() : null;
	}

	public void agregarParada(ParadaRuta parada) {
		if (parada != null) {
			parada.setRuta(this);
			paradas.add(parada);
		}
	}
}
