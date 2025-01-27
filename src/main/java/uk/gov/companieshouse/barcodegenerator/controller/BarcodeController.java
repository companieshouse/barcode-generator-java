package uk.gov.companieshouse.barcodegenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeRequest;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeResponse;
import uk.gov.companieshouse.barcodegenerator.service.BarcodeService;

@RestController
@RequestMapping("/barcode")
public class BarcodeController {

    private final BarcodeService barcodeService;


    @Autowired
    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    @PostMapping
    public ResponseEntity<BarcodeResponse> generateBarcode(@RequestBody BarcodeRequest request){
        try {
            BarcodeResponse response = barcodeService.generateBarcode(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            //Probably should log something here???
            return ResponseEntity.badRequest().build();
        } catch (Exception exception){
            //Probably should log something here???
            return ResponseEntity.internalServerError().build();
        }
    }
}
