package danilo.uk;

import danilo.uk.payment.PaymentService;
import danilo.uk.print.PrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

//Implement Parking Service that makes use of the payment and printing services
public class ParkingService {

    private final PaymentService cardPaymentService;
    private final PrintService paperPrintService;

    Logger logger = LoggerFactory.getLogger(ParkingService.class);
    private static final Map<String, Double> TIME_RATE = Map.of(
            "day", 2.0,
            "night", 1.0,
            "weekend", 0.5
    );
    private static final double WEEK_CHARGE =
            48*TIME_RATE.get("weekend")+
            40*TIME_RATE.get("day")+
            80*TIME_RATE.get("night");

    private static final Map<String, Double> VEHICLE_RATE = Map.of(
            "car", 1.0,
            "motorbike", 0.5,
            "van", 2.0
    );
    private static final List<DayOfWeek> workingDays = List.of(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY);

    public ParkingService(PaymentService cardPaymentService, PrintService paperPrintService) {
        this.cardPaymentService = cardPaymentService;
        this.paperPrintService = paperPrintService;
    }

    public double processParkingFlatRate(String vehicleType, LocalDateTime date, Double duration) {

        String timeRate = getRate(date);
        int numberOfHours = (int) Math.ceil(duration);
        double totalAmount =
                TIME_RATE.get(timeRate.toLowerCase()) * VEHICLE_RATE.get(vehicleType.toLowerCase()) * numberOfHours;

        BigDecimal bigTotalAmount = BigDecimal.valueOf(totalAmount).setScale(2, RoundingMode.HALF_UP);
        double roundTotalAmount = bigTotalAmount.doubleValue();
        cardPaymentService.requestPayment((float) totalAmount);

        paperPrintService.requestPrinting(
                vehicleType,
                "x%2f".formatted(VEHICLE_RATE.get(vehicleType.toLowerCase())),
                String.valueOf(roundTotalAmount)
        );

        return roundTotalAmount;
    }

    public double processParkingVariableRate(String vehicleType, LocalDateTime start, LocalDateTime end) {
        // calculate the difference in terms of days, hours
        // discard minutes (first hour free)
        // if days difference > 7 =>
        //          1. add the number of weeks (nWeeks) charge considering 48 hours weekend rate, 40 hours day rate, 80 hours night rate
        //          2. move the start date to nWeeks*7 days later
        //          3. calculate remaining week charge
        // else =>
        //          3. calculate remaining week charge
        // to calculate remaining week charge
        //          1. check start day and time to get time rate
        //          2. get next checkpoint (either same day 17, or next day 9, or next Saturday midnight, or next Monday midnight)
        //          3. if end before next checkpoint,
        //                  calculate hours
        //                  apply the time rate
        //                  sum to previous total
        //          4. else (end after checkpoint)
        //                  calculate hours till next checkpoint
        //                  apply the time rate
        //                  sum to previous total
        //                  move start date to the check point
        //                  continue till next checkpoint is after end
        double charge = 0.0;
        Duration parkDuration = Duration.between(start, end);
        long days = parkDuration.toDays();
        if (days < 7) {
            charge += getWeekCharge(start, end);
        } else {
            long nWeeks = days/7;
            charge += nWeeks * WEEK_CHARGE;
            LocalDateTime newStart = start.plusDays(nWeeks*7);
            charge += getWeekCharge(newStart, end);
        }
        return charge*VEHICLE_RATE.get(vehicleType);
    }

    private double getWeekCharge(LocalDateTime start, LocalDateTime end) {
        LocalDateTime checkPoint = getNextCheckPoint(start);
        LocalDateTime current = start;
        double weekCharge = 0.0;
        while(checkPoint.isBefore(end)){
            weekCharge += TIME_RATE.get(getRate(current)) * (Duration.between(current, checkPoint).toHours());
            current = checkPoint;
            checkPoint = getNextCheckPoint(current.plusHours(1));
        }
        // make sure current is still before end
        if (current.isAfter(end)){
            throw new RuntimeException("Something went wrong");
        }
        return current.equals(end) ? weekCharge : weekCharge + TIME_RATE.get(getRate(current.plusMinutes(1))) * (Duration.between(current, end).toHours());
    }

    private LocalDateTime getNextCheckPoint(LocalDateTime start) {
        DayOfWeek dayOfWeek = start.getDayOfWeek();
        int hour = start.getHour();
        boolean isWeekDay = workingDays.contains(dayOfWeek);
        if(!isWeekDay) {
            return start.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
                    .toLocalDate()
                    .atStartOfDay();
        }
        if (hour<9){
            return start.withHour(9).withMinute(0).withSecond(0);
        } else if (hour < 17) {
            return start.withHour(17).withMinute(0).withSecond(0);
        }
        if (dayOfWeek.equals(DayOfWeek.FRIDAY)) {
            return start.plusDays(1).withHour(0).withMinute(0).withSecond(0);
        }
        return start.plusDays(1).withHour(9).withMinute(0).withSecond(0);
    }

    /**
     * Get rate from a date
     * @param date the day and time of entry
     * @return "weekend"/"day"/"night"
     */
    private String getRate(LocalDateTime date) {
        if (!workingDays.contains(date.getDayOfWeek())) {
            logger.info("Lucky you, getting the weekend discount rate");
            return "weekend";
        } else {
            logger.info("Boring working day");
            return switch (Integer.valueOf(date.getHour())) {
                case Integer h when h < 9 -> "night";
                case Integer h when h >= 17 -> "night";
                default -> "day";
            };
        }
    }
}