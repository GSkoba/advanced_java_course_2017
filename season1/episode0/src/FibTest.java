import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class FibTest {
    @Test
    public void additionTest() throws Exception {
        BigNumber testNumber = new BigNumber();
        testNumber.startEvaluate(400);
        BigDecimal testDecimal = getFibonacci(400);
        Assert.assertEquals(testDecimal.toString(),testNumber.toString());

    }
    private BigDecimal getFibonacci(int index) {
        BigDecimal number1 = new BigDecimal(1);
        BigDecimal number2 = new BigDecimal(1);
        BigDecimal temp;
        for (int i=0; i<index - 2; i++) {
            temp = new BigDecimal(0);
            temp = temp.add(number2);
            number2 = number2.add(number1);
            number1 = new BigDecimal(0);
            number1 = number1.add(temp);
        }

        return number2;
    }
}