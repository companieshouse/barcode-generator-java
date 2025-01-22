package uk.gov.companieshouse.barcodegenerator;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeRequest;

class BarcodeRequestSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Test Serialization: BarcodeRequest -> JSON
    @Test
    void testSerialization() throws Exception {
        BarcodeRequest request = new BarcodeRequest();
        request.setDateReceived(20250101);
        request.setEfsBarcode(true);

        String expectedJson = """
                {
                    "datereceived" : 20250101,
                    "efsbarcode": true
                }
                """;

        String actualJson = objectMapper.writeValueAsString(request);

        JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.STRICT);
    }


    // Test Deserialization: JSON -> BarcodeRequest
    @Test
    void testDeserialization() throws Exception {
        String json = """
                {
                    "datereceived" : 20250101,
                    "efsbarcode": true
                }
                """;

        BarcodeRequest request = objectMapper.readValue(json, BarcodeRequest.class);

        assertThat(request.getDateReceived()).isEqualTo(20250101);
        assertThat(request.isEfsBarcode()).isTrue();
    }
}
