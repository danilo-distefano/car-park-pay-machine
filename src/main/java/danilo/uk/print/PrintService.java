package danilo.uk.print;

public interface PrintService {

    /**
     * Enters the payment process
     * @param vehicleType The type of vehicle
     * @param rate The rate applied e.g. x0.5 for a motorbike
     * @param amount The amount to taken during payment in pounds and pence
     */
    void requestPrinting(String vehicleType, String rate, String amount);
}
