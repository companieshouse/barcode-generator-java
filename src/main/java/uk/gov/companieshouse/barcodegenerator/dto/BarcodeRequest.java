package uk.gov.companieshouse.barcodegenerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class BarcodeRequest {

    @NotNull(message = "DateReceived cannot be null")
    @JsonProperty("dateReceived")
    private Integer dateReceived;

    @JsonProperty("efsBarcode")
    private boolean efsBarcode;

    public @NotNull(message = "DateReceived cannot be null") Integer getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(
            @NotNull(message = "DateReceived cannot be null") Integer dateReceived) {
        this.dateReceived = dateReceived;
    }

    public boolean isEfsBarcode() {
        return efsBarcode;
    }

    public void setEfsBarcode(boolean efsBarcode) {
        this.efsBarcode = efsBarcode;
    }
}
