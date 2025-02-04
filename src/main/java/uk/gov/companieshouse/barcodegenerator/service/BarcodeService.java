package uk.gov.companieshouse.barcodegenerator.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeRequest;
import uk.gov.companieshouse.barcodegenerator.dto.BarcodeResponse;
import uk.gov.companieshouse.barcodegenerator.model.Sequence;

@Service
public class BarcodeService {

    //Based on the go code? EPOCH starting year is set to 2011
    private static final int EPOCH = 2011;

    @Value("${barcode.batch.size:10}")
    private long batchSize;

    //Variables to cache the current sequence and count of numbers used from current batch
    private long currentSequence = 0;
    private long batchCount = 0;

    private final MongoTemplate mongoTemplate;

    public BarcodeService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public BarcodeResponse generateBarcode(BarcodeRequest barcodeRequest) {
        if (barcodeRequest == null || barcodeRequest.getDateReceived() == null
                || barcodeRequest.getDateReceived() == 0) {
            throw new IllegalArgumentException(
                    "Invalid request: missing required barcodeRequest fields");
        }

        //Converts numeric date to LocalDate
        String dateStr = String.valueOf(barcodeRequest.getDateReceived());
        LocalDate localDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            localDate = LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: expected format yyyyMMdd", e);
        }

        //Determine prefix based on efsBarcode
        String prefix = barcodeRequest.isEfsBarcode() ? "EFS" : "EFILING";

        long barcodeValue = getBarcodeValue(localDate);

        //Convert numeric barcode value into a base 36 string
        String encodedValue = String.format("%7s", Long.toString(barcodeValue, 36))
                .replace(' ', '0').toUpperCase();

        //Combine prefix and encode value to form final barcode
        String finalBarcode = prefix + encodedValue;

        return new BarcodeResponse(finalBarcode);
    }

    private long getBarcodeValue(LocalDate localDate) {
        int dayOfYear = localDate.getDayOfYear();
        int yearOffset = localDate.getYear() - EPOCH;
        long sequence = getSequence();

        //Bitwise stuff
        // - 6 bits for year offset (covers 64 years)
        // - 9 bits for the day of year (covers up to 512?)
        // - 19 bits for sequence (covers up to 524287)
        // Total bits is 34
        long barcodeValue =
                (((long) yearOffset & 0x3F) << (9 + 19)) | (((long) dayOfYear & 0x1FF) << 19) | (
                        sequence & 0x7FFFFL);

        //Compute a simple parity ( go code uses modulo 4?)
        long parity = barcodeValue % 4;
        //Clear the lowest 2 bits and insert parity
        barcodeValue = (barcodeValue & ~0x3L) | (parity & 0x3);
        return barcodeValue;
    }

    //Synchronized method to manage sequence batching
    private synchronized long getSequence() {
        System.out.println("Current Sequence: " + currentSequence + ", Batch Count: " + batchCount + "/" + batchSize);
        if (batchCount == 0 || batchCount >= batchSize){
            currentSequence = getSequenceFromDB();
            batchCount = 1;
            System.out.println("Fetching new batch from DB.....");
        } else {
            currentSequence++;
            batchCount++;
        }
        return currentSequence & 0x7FFFFL;
    }

    //Retrieve the sequence from MongoDB similar to the go code
    private long getSequenceFromDB() {
        Query query = new Query(); //Empty filter
        Update update = new Update().inc("currentSequence", batchSize);

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(false);
        Sequence sequenceDocument = mongoTemplate.findAndModify(query, update, options,
                Sequence.class);
        if (sequenceDocument == null) {
            throw new IllegalStateException("SequenceDocument not found in MongoDB");
        }

        return sequenceDocument.getCurrentSequence() + 1;
    }

}