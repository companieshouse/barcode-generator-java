package uk.gov.companieshouse.barcodegenerator.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeRequest;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeResponse;

@Service
public class BarcodeService {

    public BarcodeResponse generateBarcode(BarcodeRequest barcodeRequest){
        //Some logic here to do barcode stuff
        String generatedBarcode = "123456789";

        return  new BarcodeResponse(generatedBarcode);
    }
}
