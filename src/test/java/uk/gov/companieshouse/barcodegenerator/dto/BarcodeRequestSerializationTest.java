package uk.gov.companieshouse.barcodegenerator.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class BarcodeRequestSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Test Serialization: BarcodeRequest -> JSON
    @Test
    void testSerialization() throws Exception {
        BarcodeRequest request = new BarcodeRequest(20250101, true);

        String expectedJson = """
                {
                    "dateReceived" : 20250101,
                    "efsBarcode": true
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
                    "dateReceived" : 20250101,
                    "efsBarcode": true
                }
                """;

        BarcodeRequest request = objectMapper.readValue(json, BarcodeRequest.class);

        assertThat(request.getDateReceived()).isEqualTo(20250101);
        assertThat(request.isEfsBarcode()).isTrue();
    }
}
