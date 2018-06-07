import java.math.BigInteger;
import java.util.Arrays;

class BigNumber implements FibonacciAlgorithm{
    private int numLongs;
    private long bytes[];
    private int number;
    private long mask;

    BigNumber(){
        this.numLongs = 3;
        this.bytes = new long[numLongs];
        Arrays.fill(this.bytes, 0);
        this.mask = 1;
        for(int i = 0; i < 62; ++i){
            mask <<= 1;
            mask |= 1;
        }
    }

    BigNumber(int number){
        this();
        this.number = number;
        this.calc();
    }

    void resize(){
        this.numLongs *= 2;
        long[] arr = new long[this.numLongs];
        for(int j = 0; j < this.numLongs; ++j){
            if(j < this.numLongs - this.bytes.length)
                arr[j] = 0;
            else
                arr[j] = this.bytes[j - this.bytes.length];
        }
        this.bytes = arr;
    }

    void add(long[] otherBytes){
        long current;
        boolean isOverFlown = false;
        int overflown = 0;
        for(int i = Math.min(numLongs - 1,otherBytes.length - 1); i >= 0; --i){
            if(!(bytes[i] + otherBytes[i] < 0)) {
                current = bytes[i] + otherBytes[i] + overflown;
                overflown = 0;
            }
            else{
                if(0 == i){
                    resize();
                    isOverFlown = true;
                }
                //index problem
                if(isOverFlown)
                    current = (bytes[i + this.numLongs / 2] + otherBytes[i] + overflown) & mask;
                else
                    current = (bytes[i] + otherBytes[i] + overflown) & mask;
                overflown = 1;

                //i += this.numLongs / 2;
            }
            if(isOverFlown) {
                this.bytes[i + this.numLongs / 2] = current;
                this.bytes[this.numLongs / 2 - 1] = 1;
            }
            else
                this.bytes[i] = current;
        }
    }

    void add(BigNumber fib){
        long[] otherBytes = fib.getBytes();
        add(otherBytes);
    }

    private long[] getBytes() {
        return bytes;
    }

    private void setBytes(long[] bytes){
        this.bytes = bytes;
    }

    public void startEvaluate(int number) {
        this.number = number;
        this.calc();
    }

    public String evaluate(int number){
        this.number = number;
        this.calc();
        return this.toString();
    }

    private void calc(){
        long[] maxBackUp = new long[numLongs], max;
        boolean overflown = false;
        for(int i = 0; i < numLongs; ++i){
            maxBackUp[i] = 0;
        }
        maxBackUp[numLongs - 1] = 2;
        this.add(maxBackUp);
        maxBackUp[numLongs - 1] = 1;
        max = maxBackUp.clone();
        if(this.number == 1 || this.number == 2){
            this.setBytes(maxBackUp);
            return;
        }
        else if(this.number == 3)
            return;

        for(int i = 0; i < this.number - 3; ++i){
            if(maxBackUp.length != this.bytes.length) {
                maxBackUp = new long[this.bytes.length];
                //System.out.println("allocated " + this.bytes.length + " bytes");
            }
            System.arraycopy(this.bytes, 0, maxBackUp, 0, this.bytes.length);
            //maxBackUp = this.getBytes();

            this.add(max);
            if(max.length != this.bytes.length) {
                max = new long[this.bytes.length];
                //System.out.println("allocated " + this.bytes.length + " bytes");
                overflown = true;
            }
            if(overflown) {
                System.arraycopy(maxBackUp, 0, max, maxBackUp.length, maxBackUp.length);
                overflown = false;
            }
            else
                System.arraycopy(maxBackUp, 0, max,0, maxBackUp.length);
        }

    }

    public String toString(){

        BigInteger res = new BigInteger(String.valueOf(0));
        BigInteger cur;
        for(int i = numLongs - 1; i >=0; --i){
            cur = BigInteger.valueOf(bytes[i]);
            for(int j = numLongs - i - 1; j > 0; --j){
                cur = cur.multiply(BigInteger.valueOf(Long.MIN_VALUE));
            }
            res = res.add(cur.abs());

        }
        return res.toString();
    }
}


public class Main {


    public static void main(String[] args) {

        BigNumber f1 = new BigNumber();
        System.out.println(f1.evaluate(300));
    }
}
