import java.util.Scanner;

public class HumanPlayer {

    Scanner scanner = new Scanner(System.in);

    public char chooseWord(){

        String letter = "";
        while (true){
            GamePrinter.printInstructions();
            letter = scanner.next();

            if (letter.length() == 1 && Character.isLetter(letter.charAt(0))){
                return Character.toLowerCase(letter.charAt(0));
            }
            else {
                System.out.println("The character you introduce has to be a letter. Thanks!");
            }
        }
    }
}
