import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by stephan on 8/23/16.
 */
public class Main {
    public static void main(String[] args) {
        BigDecimal revenue;
        BigDecimal expense;
        ExampleCreator exampleCreator = new ExampleCreator(29, 4, 5, 4, 3, 4);

        int numberLate = 3;
        int numberBad = 3;
        int numberBoth = 3;
        boolean noTicketNeg = true;

        int ticket;
        Random rand = new Random();

        for (int i = 0; i < 20; i++) {
            revenue = BigDecimal.valueOf(100 + (800 - 100) * rand.nextDouble()).setScale(2,BigDecimal.ROUND_HALF_UP);
            expense = revenue.multiply(BigDecimal.ONE.subtract( BigDecimal.valueOf((8 + (12 - 8) * rand.nextDouble()) / 100))).setScale(2,BigDecimal.ROUND_HALF_UP);;
            if (numberBoth > 0) {
                ticket = 3;
                numberBoth--;
            } else if(numberBad > 0) {
                ticket = 1;
                numberBad--;
            } else if(numberLate > 0) {
                ticket = 2;
                numberLate--;
            } else {
                if (noTicketNeg) {
                    revenue = BigDecimal.valueOf(100 + (800 - 100) * rand.nextDouble()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    expense = revenue.multiply(BigDecimal.ONE.add( BigDecimal.valueOf((8 + (12 - 8) * rand.nextDouble()) / 100))).setScale(2,BigDecimal.ROUND_HALF_UP);;
                    noTicketNeg = false;
                }
                ticket = 0;
            }
            exampleCreator.start(i, ticket, revenue, expense);
        }
        exampleCreator.appendAllFiles(20);
    }
}
