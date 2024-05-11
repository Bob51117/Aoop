import java.util.Scanner;

/**
 * @author
 * @create
 */
public class CLIApp {
    private static INumberleModel model = new NumberleModel();
    private static final Scanner scanner = new Scanner(System.in);

    public static void InitGame() {
        System.out.println("Initialize the game...");
        model.write();
        System.out.println("Successful initialization!");
        System.out.println("Starting the game....");
        model.startNewGame();
        System.out.println("Game launch success");
    }

    public static void main(String[] args) {
        while (true) {
            InitGame();
            while (model.getRemainingAttempts() > 0) {
                System.out.print("Please enter your expression:");
                model.processInput(scanner.next());
                if (model.isGameWon()) {
                    System.out.println("You Win");
                    break;
                }
                System.out.println("You remainingAttempts times: " + model.getRemainingAttempts());
            }
            if (!model.isGameWon()) {
                System.out.println("You Lose");
            }
            System.out.println("Whether to continue the game (enter 0 to continue)");
            if (scanner.nextInt() != 0) {
                return;
            }
        }
    }
}
