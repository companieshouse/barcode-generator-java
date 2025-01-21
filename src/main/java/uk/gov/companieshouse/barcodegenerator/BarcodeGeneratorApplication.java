package uk.gov.companieshouse.barcodegenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BarcodeGeneratorApplication {

	public static final String APPLICATION_NAME_SPACE = "barcode-generator";

	public static void main(String[] args) {
		SpringApplication.run(BarcodeGeneratorApplication.class, args);
	}

}
