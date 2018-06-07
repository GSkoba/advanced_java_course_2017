import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class MyAmazingBot extends TelegramLongPollingBot {
    private GameState game;
    private HumanPlayer humanPlayer;
     {
        this.game = new GameState();
        this.humanPlayer = new HumanPlayer();
    }
    @Override
    public void onUpdateReceived(Update update){
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            if(!game.isOver()) {
                //GamePrinter.printCurrentResult(game.getCurrentResult());
                String message_text = update.getMessage().getText();
                long chat_id = update.getMessage().getChatId();

                SendMessage messageForCurrentResult = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText(GamePrinter.printCurrentResult(game.getCurrentResult()));

                try{
                    execute(messageForCurrentResult);
                }catch (TelegramApiException e){
                    e.printStackTrace();
                }


                String letter = update.getMessage().getText().toLowerCase();

                SendMessage messageForCheckLetter = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText(game.checkLetter(letter));

                try {

                    execute(messageForCheckLetter);// Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                if(game.isOver()){
                    SendMessage messageForHasWon = new SendMessage() // Create a message object object
                            .setChatId(chat_id)
                            .setText(GamePrinter.printFinalResult(game.hasWon()));

                    try{
                        execute(messageForHasWon);
                    }catch (TelegramApiException e){
                        e.printStackTrace();
                    }
                    SendMessage messageForHasLose = new SendMessage() // Create a message object object
                            .setChatId(chat_id)
                            .setText(GamePrinter.printCurrentResult(game.getCurrentResult()));
                    try{
                        execute(messageForHasLose);
                    }catch (TelegramApiException e){
                        e.printStackTrace();
                    }
                    game = new GameState();
                    humanPlayer = new HumanPlayer();
                }
            }

        }
    }

    @Override
    public String getBotUsername() {
        // TODO
        return "gskoba_bot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "386813799:AAHRI5pLvzDyY67_xrfW9AqD38_rN5HtAU4";
    }
}
