package danilo.uk;

import danilo.uk.payment.CardPaymentService;
import danilo.uk.payment.PaymentService;
import danilo.uk.print.PaperPrintService;
import danilo.uk.print.PrintService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParkingServiceTest {
    private final PrintService paperPrintService = new PaperPrintService();
    private final PaymentService cardPaymentService = new CardPaymentService();

    @ParameterizedTest
    @MethodSource("datesUpgraded")
    public void testTimeRatesUpgraded(LocalDateTime start, LocalDateTime end, double expected) {
        ParkingService parkingService = new ParkingService(cardPaymentService, paperPrintService);
        double result = parkingService.processParkingUpgraded("car", start, end);
        assertEquals(0, Double.compare(expected, result));
    }

    @ParameterizedTest
    @MethodSource("dates")
    public void testTimeRates(LocalDateTime date, double expected) {
        ParkingService parkingService = new ParkingService(cardPaymentService, paperPrintService);
        double result = parkingService.processParking("car", date, 1.0);
        assertEquals(0, Double.compare(expected, result));
    }

    @ParameterizedTest
    @MethodSource("vehicles")
    public void testVehicleRates(String vehicle, double expected) {
        ParkingService parkingService = new ParkingService(cardPaymentService, paperPrintService);
        double result = parkingService.processParking(
                vehicle,
                LocalDateTime.of(2026, 8, 17, 10, 0, 0),
                1.0);
        assertEquals(0, Double.compare(expected, result));
    }

    private static Stream<Arguments> vehicles() {
        return Stream.of(
                Arguments.of("car", 2.0),
                Arguments.of("CAR", 2.0),
                Arguments.of("van", 4.0),
                Arguments.of("vaN", 4.0),
                Arguments.of("motorbike", 1.0),
                Arguments.of("Motorbike", 1.0)
        );
    }
    private static Stream<Arguments> dates() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2026, 8, 17, 10, 0, 0), 2.0), // Monday 10am
                Arguments.of(LocalDateTime.of(2026, 8, 17, 18, 0, 0), 1.0), // Monday 6pm
                Arguments.of(LocalDateTime.of(2026, 8, 16, 18, 0, 0), 0.5), // Sunday 6pm
                Arguments.of(LocalDateTime.of(2026, 8, 17, 9, 0, 0), 2.0), // Monday 9am
                Arguments.of(LocalDateTime.of(2026, 8, 17, 17, 0, 0), 1.0), // Monday 5pm
                Arguments.of(LocalDateTime.of(2026, 8, 15, 0, 0, 0), 0.5), // Saturday midnight
                Arguments.of(LocalDateTime.of(2026, 8, 17, 0, 0, 0), 1.0)// Monday midnight
        );
    }
    private static Stream<Arguments> datesUpgraded() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2026, 8, 17, 10, 0, 0), LocalDateTime.of(2026, 8, 17, 12, 0, 0), 1.0)
        );
    }
}