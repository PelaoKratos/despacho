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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SeguimientoDespacho {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idSeguimiento;

	@Column(name = "id_despacho", insertable = false, updatable = false)
	private Long idDespacho;

	private LocalDateTime fechaRegistro;

	@NotBlank(message = "La ubicacion es obligatoria")
	@Column(nullable = false)
	private String ubicacion;

	@NotBlank(message = "El estado no puede estar vacio")
	@Column(nullable = false)
	private String estado;

	private String observacion;

	@ManyToOne
	@JoinColumn(name = "id_despacho")
	private Despacho despacho;

	public void registrarSeguimiento() {
		if (fechaRegistro == null) {
			fechaRegistro = LocalDateTime.now();
		}
	}

	public void actualizarUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
		this.fechaRegistro = LocalDateTime.now();
	}

	public String consultarEstado() {
		return estado;
	}

	public void setDespacho(Despacho despacho) {
		this.despacho = despacho;
		this.idDespacho = despacho != null ? despacho.getIdDespacho() : null;
	}
}
