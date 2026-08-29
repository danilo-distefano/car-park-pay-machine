package danilo.uk.print;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PaperPrintServiceTest {

    @ParameterizedTest
    @MethodSource("testDifferentVehicleTypes")
    public void testPrint(String vehicleType, String rate, String amount) {
        LogCaptor logCaptor = LogCaptor.forClass(PaperPrintService.class);

        PrintService paperPrintService = new PaperPrintService();
        paperPrintService.requestPrinting(vehicleType, rate, amount);
        assertTrue(logCaptor.getInfoLogs().contains("Print called, vehicleType: %s, Rate: %s, Amount: £%s".formatted(vehicleType, rate, amount)));
    }

    private static Stream<Arguments> testDifferentVehicleTypes() {
        return Stream.of(
                Arguments.of("Motorbike", "x0.5", "3.00"),
                Arguments.of("Car", "x1", "3.00"),
                Arguments.of("Van", "x2", "3.00"));
    }
}