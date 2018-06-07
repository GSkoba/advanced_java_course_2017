
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Dictionary {

    private String words = "dog:cat:bear:octopus:red:green:blue:unicorn:doctor:gamer:youtuber:mouse:trackpad:computer";
    private String [] wordsAsArray = words.split(":");

    public String getRandomWord () {
        int randomNum = ThreadLocalRandom.current().nextInt(0, wordsAsArray.length);
        return wordsAsArray[randomNum];
    }
}
