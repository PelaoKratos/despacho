package microservice.despacho;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.client.RestTemplate;

import microservice.despacho.service.DespachoService;

class DespachoApplicationTests {

	@Test
	void restTemplateBeanSeCreaCorrectamente() {
		DespachoApplication application = new DespachoApplication();

		RestTemplate restTemplate = application.restTemplate();

		assertThat(restTemplate).isNotNull();
		assertThat(restTemplate).isInstanceOf(RestTemplate.class);
	}

	@Test
	void cargarDatosDemoEjecutaCargaDesdeServicio() throws Exception {
		DespachoApplication application = new DespachoApplication();
		DespachoService despachoService = org.mockito.Mockito.mock(DespachoService.class);

		CommandLineRunner runner = application.cargarDatosDemo(despachoService);
		runner.run("demo");

		verify(despachoService).cargarDatosDemo();
	}
}
