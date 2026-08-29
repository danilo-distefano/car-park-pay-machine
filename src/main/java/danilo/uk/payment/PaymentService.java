package danilo.uk.payment;

public interface PaymentService {

    /**
     * Enters the payment process
     * @param amount The amount to take in pounds and pence
     * @return True if successful
     */
    boolean requestPayment(float amount);
}
