package uk.gov.companieshouse.barcodegenerator.dto;

public class BarcodeResponse {

    private final String barcode;

    public BarcodeResponse(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }
}
