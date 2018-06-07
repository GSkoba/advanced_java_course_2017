package edu.technopolis;

import java.math.BigInteger;

/**
 * Это <b>неправильный</b> с точки зрения задания алгоритм.
 *
 */
public class DummyFibonacciAlgorithm implements FibonacciAlgorithm {
    @Override
    public String evaluate(int index) {
        if (index < 3) {
           return "1";
        }
        BigInteger one = BigInteger.ONE;
        BigInteger two = BigInteger.ONE;
        BigInteger result = one;
        for (int i = 2; i < index; i++) {
            result = one.add(two);
            one = two;
            two = result;
        }
        return result.toString();
    }
}
