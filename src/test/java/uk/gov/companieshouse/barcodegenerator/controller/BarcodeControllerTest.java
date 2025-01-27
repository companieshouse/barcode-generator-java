package uk.gov.companieshouse.barcodegenerator.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeRequest;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeResponse;
import uk.gov.companieshouse.barcodegenerator.service.BarcodeService;

class BarcodeControllerTest {

    private BarcodeService barcodeService;
    private BarcodeController barcodeController;

    @BeforeEach
    void setup() {
        barcodeService = mock(BarcodeService.class);
        barcodeController = new BarcodeController(barcodeService);
    }

    @Test
    void generateBarcode_ShouldCallServiceWithCorrectRequest_AndReturnExpectedResponse() {
        BarcodeRequest request = new BarcodeRequest();
        request.setDateReceived(20250101);
        request.setEfsBarcode(true);

        BarcodeResponse expectedResponse = new BarcodeResponse("BARCODE12345");
        when(barcodeService.generateBarcode(request)).thenReturn(expectedResponse);

        BarcodeResponse actualResponse = barcodeController.generateBarcode(request).getBody();

        assertEquals(expectedResponse.getBarcode(), actualResponse.getBarcode());
        verify(barcodeService, times(1)).generateBarcode(request);
    }

    @Test
    void generateBarcode_ShouldThrowIllegalArgumentException_WhenServiceThrowsException() {
        BarcodeRequest invalidRequest = new BarcodeRequest();
        when(barcodeService.generateBarcode(invalidRequest)).thenThrow(
                new IllegalArgumentException("Invalid Request"));

        try {
            barcodeController.generateBarcode(invalidRequest);
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid Request", e.getMessage());
        }

        verify(barcodeService, times(1)).generateBarcode(invalidRequest);
    }

    @Test
    void generateBarcode_ShouldThrowIllegalArgumentException_WhenRequestIsNull() {
        when(barcodeService.generateBarcode(null)).thenThrow(
                new IllegalArgumentException("Request cannot be null"));

        try {
            barcodeController.generateBarcode(null);
        } catch (IllegalArgumentException e) {
            assertEquals("Request cannot be null", e.getMessage());
        }

        verify(barcodeService, times(1)).generateBarcode(null);
    }
}