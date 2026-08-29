package danilo.uk.print;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaperPrintService implements PrintService {

    Logger logger = LoggerFactory.getLogger(PaperPrintService.class);

    @Override
    public void requestPrinting(String vehicleType, String rate, String amount) {
        logger.info("Print called, vehicleType: {}, Rate: {}, Amount: £{}", vehicleType, rate, amount);
    }
}
