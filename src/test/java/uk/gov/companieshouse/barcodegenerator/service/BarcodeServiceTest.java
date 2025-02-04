package uk.gov.companieshouse.barcodegenerator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeRequest;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeResponse;
import uk.gov.companieshouse.barcodegenerator.model.Sequence;

class BarcodeServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private BarcodeService barcodeService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateBarcode_whenValidEfsBarcodeTrue_shouldReturnBarcode() {
        BarcodeRequest request = new BarcodeRequest(20250123, true);
        Sequence sequence = new Sequence();
        sequence.setCurrentSequence(1000);

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(
                FindAndModifyOptions.class), eq(Sequence.class))).thenReturn(sequence);

        BarcodeResponse response = barcodeService.generateBarcode(request);

        assertNotNull(response);
        assertTrue(response.getBarcode().startsWith("EFS"));
        assertEquals(10, response.getBarcode().length());
    }

    @Test
    void generateBarcode_whenValidEfsBarcodeFalse_shouldReturnBarcode() {
        BarcodeRequest request = new BarcodeRequest(20250123, false);
        Sequence sequence = new Sequence();
        sequence.setCurrentSequence(500);

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(
                FindAndModifyOptions.class), eq(Sequence.class))).thenReturn(sequence);

        BarcodeResponse response = barcodeService.generateBarcode(request);

        assertNotNull(response);
        assertTrue(response.getBarcode().startsWith("EFILING"));
        assertEquals(14, response.getBarcode().length());
    }

    @Test
    void generateBarcode_whenNullRequest_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> barcodeService.generateBarcode(null));
    }

    @Test
    void generateBarcode_whenDateIsNull_shouldThrowException() {
        BarcodeRequest request = new BarcodeRequest(null, true);
        assertThrows(IllegalArgumentException.class, () -> barcodeService.generateBarcode(request));
    }

    @Test
    void generateBarcode_whenZeroDateReceived_shouldThrowException() {
        BarcodeRequest request = new BarcodeRequest(0, true);
        assertThrows(IllegalArgumentException.class, () -> barcodeService.generateBarcode(request));
    }

    @Test
    void generateBarcode_whenInvalidDateFormat_shouldThrowException() {
        BarcodeRequest request = new BarcodeRequest(20251545, true);
        assertThrows(IllegalArgumentException.class, () -> barcodeService.generateBarcode(request));
    }

    @Test
    void generateBarcode_whenSequenceNotFound_shouldThrowException() {
        BarcodeRequest request = new BarcodeRequest(20250123, true);

        when(mongoTemplate.findAndModify(any(Query.class), any(Update.class), any(
                FindAndModifyOptions.class), eq(Sequence.class))).thenReturn(null);

        assertThrows(IllegalStateException.class,
                () -> barcodeService.generateBarcode(request));
    }
}
