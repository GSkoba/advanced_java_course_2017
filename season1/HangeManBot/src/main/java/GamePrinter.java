public class GamePrinter {

    public static String printInstructions() {

        return "Enter a letter.";

    }
    public static String printFinalResult(boolean result){
        if(result) {
            return "You've won!";
        }else{
            return "You've lost!";
        }
    }

    public static String printCurrentResult(String currentResult){
        return "The current word : " + currentResult;
    }
}
