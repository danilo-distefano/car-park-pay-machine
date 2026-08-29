package danilo.uk.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CardPaymentService implements PaymentService {

    Logger logger = LoggerFactory.getLogger(CardPaymentService.class);

    @Override
    public boolean requestPayment(float amount) {
        logger.info("Payment called, amount: £{}", amount);
        return true;
    }
}
