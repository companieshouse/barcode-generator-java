package uk.gov.companieshouse.barcodegenerator.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeRequest;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeResponse;
import uk.gov.companieshouse.barcodegenerator.service.BarcodeService;

@WebMvcTest(controllers = BarcodeController.class)
class BarcodeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BarcodeService barcodeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGenerateBarcode_Success() throws Exception {
        BarcodeRequest request = new BarcodeRequest(20250101, true);
        BarcodeResponse response = new BarcodeResponse("GeneratedBarcode123");

        when(barcodeService.generateBarcode(any(BarcodeRequest.class))).thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);
        String responseJson = objectMapper.writeValueAsString(response);

        mockMvc.perform(
                        post("/barcode").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(responseJson));
    }

    @Test
    void testGenerateBarcode_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        String invalidRequestJson = """
                {
                    "dateReceived":"2025-01-01"
                }
                """;

        mockMvc.perform(post("/barcode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGenerateBarcode_ShouldReturnInternalServerError_WhenBarcodeServiceThrows() throws Exception {
        BarcodeRequest request = new BarcodeRequest(20250101, true);

        when(barcodeService.generateBarcode(any(BarcodeRequest.class))).thenThrow(new RuntimeException("Something went wrong"));

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/barcode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError());
    }
}
