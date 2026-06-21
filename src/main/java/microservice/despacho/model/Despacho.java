package microservice.despacho.model;

import java.time.LocalDateTime;
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
import jakarta.validation.constraints.NotNull;
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

	@NotNull(message = "El id del pedido es obligatorio")
	private Long idPedido;

	@NotNull(message = "El id de cliente es obligatorio")
	private Long idCliente;

	@NotNull(message = "El id de sucursal es obligatorio")
	private Long idSucursal;

	@NotBlank(message = "La direccion de entrega no puede estar vacia")
	@Column(nullable = false)
	private String direccionEntrega;

	@NotNull(message = "La fecha de despacho es obligatoria")
	private LocalDateTime fechaDespacho;

	@NotNull(message = "La fecha estimada de entrega es obligatoria")
	private LocalDateTime fechaEstimadaEntrega;

	private LocalDateTime fechaEntrega;

	@NotBlank(message = "El estado no puede estar vacio")
	@Column(nullable = false)
	private String estado;

	@OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("despacho")
	private List<DetalleDespacho> detalles = new ArrayList<>();

	@OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("despacho")
	private List<ParadaRuta> paradas = new ArrayList<>();

	@OneToMany(mappedBy = "despacho", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties("despacho")
	private List<SeguimientoDespacho> seguimientos = new ArrayList<>();

	public void crearDespacho() {
		if (fechaDespacho == null) {
			fechaDespacho = LocalDateTime.now();
		}
		if (estado == null || estado.isBlank()) {
			estado = "PENDIENTE";
		}
	}

	public void actualizarEstado(String estado) {
		this.estado = estado;
	}

	public void asignarRuta() {
		this.estado = "RUTA_ASIGNADA";
	}

	public void confirmarEntrega() {
		this.estado = "ENTREGADO";
		this.fechaEntrega = LocalDateTime.now();
	}

	public void agregarDetalle(DetalleDespacho detalle) {
		if (detalle != null) {
			detalle.setDespacho(this);
			detalles.add(detalle);
		}
	}

	public void agregarParada(ParadaRuta parada) {
		if (parada != null) {
			parada.setDespacho(this);
			paradas.add(parada);
		}
	}

	public void agregarSeguimiento(SeguimientoDespacho seguimiento) {
		if (seguimiento != null) {
			seguimiento.setDespacho(this);
			seguimientos.add(seguimiento);
		}
	}
}
