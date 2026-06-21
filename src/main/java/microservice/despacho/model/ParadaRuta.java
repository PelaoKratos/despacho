package microservice.despacho.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ParadaRuta {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idParada;

	@Column(name = "id_ruta", insertable = false, updatable = false)
	private Long idRuta;

	@Column(name = "id_despacho", insertable = false, updatable = false)
	private Long idDespacho;

	@NotBlank(message = "La direccion es obligatoria")
	@Column(nullable = false)
	private String direccion;

	@NotNull(message = "El orden de parada es obligatorio")
	private int ordenParada;

	private LocalDateTime horaEstimada;

	private LocalDateTime horaLlegada;

	@NotBlank(message = "El estado no puede estar vacio")
	@Column(nullable = false)
	private String estado;

	@ManyToOne
	@JoinColumn(name = "id_despacho")
	private Despacho despacho;

	@ManyToOne
	@JoinColumn(name = "id_ruta")
	private RutaEntrega ruta;

	public void registrarParada() {
		estado = "REGISTRADA";
	}

	public void actualizarLlegada() {
		horaLlegada = LocalDateTime.now();
		estado = "COMPLETADA";
	}

	public void cambiarEstado(String estado) {
		this.estado = estado;
	}

	public void setDespacho(Despacho despacho) {
		this.despacho = despacho;
		this.idDespacho = despacho != null ? despacho.getIdDespacho() : null;
	}

	public void setRuta(RutaEntrega ruta) {
		this.ruta = ruta;
		this.idRuta = ruta != null ? ruta.getIdRuta() : null;
	}
}
