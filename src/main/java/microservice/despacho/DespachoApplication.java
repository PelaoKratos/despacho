package microservice.despacho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import microservice.despacho.service.DespachoService;

@SpringBootApplication
public class DespachoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DespachoApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	CommandLineRunner cargarDatosDemo(DespachoService despachoService) {
		return args -> despachoService.cargarDatosDemo();
	}
}
