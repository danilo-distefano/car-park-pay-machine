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
        double result = parkingService.processParkingVariableRate("car", start, end);
        assertEquals(0, Double.compare(expected, result), "expected: %f, actual: %f".formatted(expected, result));
    }

    @ParameterizedTest
    @MethodSource("dates")
    public void testTimeRates(LocalDateTime date, double expected) {
        ParkingService parkingService = new ParkingService(cardPaymentService, paperPrintService);
        double result = parkingService.processParkingFlatRate("car", date, 1.0);
        assertEquals(0, Double.compare(expected, result));
    }

    @ParameterizedTest
    @MethodSource("vehicles")
    public void testVehicleRates(String vehicle, double expected) {
        ParkingService parkingService = new ParkingService(cardPaymentService, paperPrintService);
        double result = parkingService.processParkingFlatRate(
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
                Arguments.of(
                        LocalDateTime.of(2026, 8, 17, 10, 0, 0),
                        LocalDateTime.of(2026, 8, 24, 10, 0, 0),
                        184.0), // 1 week
                Arguments.of(
                        LocalDateTime.of(2026, 8, 17, 10, 0, 0),
                        LocalDateTime.of(2026, 9, 7, 10, 0, 0),
                        184.0*3), // 3 weeks going to the next month
                Arguments.of(
                        LocalDateTime.of(2026, 8, 17, 10, 0, 0),
                        LocalDateTime.of(2026, 10, 5, 10, 0, 0),
                        184.0*7),
                Arguments.of(
                        LocalDateTime.of(2026, 8, 17, 10, 0, 0),
                        LocalDateTime.of(2026, 8, 20, 10, 0, 0),
                        96.0), // 2 full week days (32*2=64) + 10-24 Monday (7*2.0+7*1.0=21) + 00-10 Thursday (9*1.0+1*2.0=11)
                Arguments.of(
                        LocalDateTime.of(2026, 8, 17, 10, 0, 0),
                        LocalDateTime.of(2026, 8, 27, 9, 0, 0),
                        278.0), // 1 full week (184.0 GBP) to get to 24th + (7 hours day tariff + 16 hours night tariff) to get to 25th (30.0 GBP) + (8 hours day tariff + 16 hours night tariff) to get to 26th (32 GBP) + (8 hours day tariff + 16 hours night tariff) to get to 27 (32 GBP)
                Arguments.of(
                        LocalDateTime.of(2026, 8, 21, 10, 0, 0),
                        LocalDateTime.of(2026, 8, 29, 14, 0, 0),
                        212.0) // 1 full week (184.0 GBP) to get to 28th + (7hours day+7 hours night) to get to 29th midnight (21 GBP) + 14 hours weekend (7 GBP)
        );
    }
}