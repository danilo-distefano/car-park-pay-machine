package danilo.uk.payment;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardPaymentServiceTest {
    @Test
    public void testPayment() {
        LogCaptor logCaptor = LogCaptor.forClass(CardPaymentService.class);
        PaymentService paymentService = new CardPaymentService();

        boolean paymentResult = paymentService.requestPayment(3f);

        assertTrue(paymentResult, "Test the card payment service");
        assertTrue(logCaptor.getInfoLogs().contains("Payment called, amount: £3.0"), "Validating the log message failed");
    }
}