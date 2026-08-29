package danilo.uk;

import danilo.uk.payment.CardPaymentService;
import danilo.uk.payment.PaymentService;
import danilo.uk.print.PaperPrintService;
import danilo.uk.print.PrintService;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        System.out.println("ciao");
        PrintService paperPrintService = new PaperPrintService();
        PaymentService cardPaymentService = new CardPaymentService();
        ParkingService parkingService = new ParkingService(cardPaymentService, paperPrintService);
        String vehicle = "car";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.of(2026, 8, 31, 15, 0, 0);
        double amount = parkingService.processParkingFlatRate(vehicle, start, 1.3);
        double amountUpgraded = parkingService.processParkingVariableRate(vehicle, start, end);
        System.out.println("amount " + amount);
        System.out.println("amountUpgraded " + amountUpgraded);
    }
}
