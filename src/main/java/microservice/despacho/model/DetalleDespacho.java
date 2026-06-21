package microservice.despacho.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DetalleDespacho {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDetalleDespacho;

	@Column(name = "id_despacho", insertable = false, updatable = false)
	private Long idDespacho;

	@NotNull(message = "El id del producto es obligatorio")
	private Long idProducto;

	@Positive(message = "La cantidad debe ser mayor a cero")
	private int cantidad;

	@NotBlank(message = "El estado no puede estar vacio")
	@Column(nullable = false)
	private String estado;

	@ManyToOne
	@JoinColumn(name = "id_despacho")
	private Despacho despacho;

	public void agregarDetalle() {
		estado = "AGREGADO";
	}

	public void modificarCantidad(int cantidad) {
		if (cantidad <= 0) {
			throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
		}
		this.cantidad = cantidad;
	}

	public void quitarDetalle() {
		estado = "ELIMINADO";
	}

	public void setDespacho(Despacho despacho) {
		this.despacho = despacho;
		this.idDespacho = despacho != null ? despacho.getIdDespacho() : null;
	}
}
