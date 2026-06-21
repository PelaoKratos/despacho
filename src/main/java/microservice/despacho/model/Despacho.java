package microservice.despacho.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Despacho {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDespacho;

	@NotNull(message = "El id de venta es obligatorio")
	private Long idVenta;

	@NotNull(message = "El id de cliente es obligatorio")
	private Long idCliente;

	@NotNull(message = "El id de sucursal es obligatorio")
	private Long idSucursal;

	@NotBlank(message = "La direccion de entrega no puede estar vacia")
	@Column(nullable = false)
	private String direccionEntrega;

	@NotBlank(message = "La comuna no puede estar vacia")
	@Column(nullable = false)
	private String comuna;

	@NotBlank(message = "La ciudad no puede estar vacia")
	@Column(nullable = false)
	private String ciudad;

	@NotBlank(message = "El estado no puede estar vacio")
	@Column(nullable = false)
	private String estado;

	private String transportista;

	@PositiveOrZero(message = "El costo no puede ser negativo")
	private Integer costoDespacho;

	@NotNull(message = "La fecha estimada es obligatoria")
	private LocalDate fechaEstimada;

	private LocalDate fechaEntrega;
}
