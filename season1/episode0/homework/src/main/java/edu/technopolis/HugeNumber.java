package edu.technopolis;

public class HugeNumber implements FibonacciAlgorithm{

    final int n = 3500;
    long [] part = new long[n];

    public HugeNumber(){}

    public HugeNumber(long value){

        for (long b: part
                ) {
            b = 0;
        }
        this.part[0] = value;
    }


    @Override
    public String evaluate(int index){
        if(index == 1 || index == 2) return "1";
        return FibonacciNumber(index);
    }

    public static String FibonacciNumber(long value) {

        HugeNumber secondFib = new HugeNumber(0);
        HugeNumber firstFib = new HugeNumber(1);

        HugeNumber currentFib = new HugeNumber(0);

        for (int i = 2; i <= value; i++) {
            currentFib = sumHugeNumbers(secondFib, firstFib);
            secondFib = firstFib;
            firstFib = currentFib;
        }

        return currentFib.HugeNumberToString();
    }

    public static HugeNumber sumHugeNumbers(HugeNumber a, HugeNumber b) {

        HugeNumber sum = new HugeNumber(0);

        final long numOfDigitsOneLong = (long) Math.pow(10,18);

        /*
        В одном лонге 19 цифр.
        Будем считать за переполнение когда цифр становится больше 18.
         */

        int n = sum.n;

        for (int i = 0; i < n-1; i++){

            sum.part[i] += a.part[i] + b.part[i];

            if (getCountOfDigits(sum.part[i]) > 18 ) {

                sum.part[i+1] += 1;
                sum.part[i] -= numOfDigitsOneLong;
            }
        }

        return sum;
    }

    public static int getCountOfDigits(long value){
        int n = 0;
        while (value > 0){
            value = value / 10;
            n++;
        }

        return n;
    }

    String HugeNumberToString(){

        String str = "";

        for (int i = 0; i < n; i++) {
            if (part[n-i-1] != 0) {

                if(getCountOfDigits(part[n-i-1]) == 18)
                    str += Long.toString(part[n-i-1]);

                else {
                    for (int j = 0; j < 18 - getCountOfDigits(part[n-i-1]); j++)
                        str += "0";

                    str += Long.toString(part[n-i-1]);
                }
            }
        }

        return str.replaceFirst ("^0*", "");
    }

}
