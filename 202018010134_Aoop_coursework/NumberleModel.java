// NumberleModel.java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;

    private boolean TestMode;
    private boolean FixdMode;

    private boolean DisMode;
    private ArrayList<String> lines = new ArrayList<>();
    @Override
    public void initialize() {
        if (lines.isEmpty()){
            try (BufferedReader reader = new BufferedReader(new FileReader("equations.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                System.err.println("Unable to read file: " + e.getMessage());
                return;
            }
        }
        if (!lines.isEmpty()) {
            Random random = new Random();
            targetNumber = lines.get(random.nextInt(lines.size()));
        } else {
            System.out.println("The file is empty and rows cannot be selected.");
        }
        if (FixdMode){  //
            targetNumber = "4=9-3-2";
        }
        currentGuess = new StringBuilder(" ");
        remainingAttempts = MAX_ATTEMPTS;
        if (DisMode){  //
            System.out.println("Target Number:" + targetNumber);
        }
        gameWon = false;
        setChanged();
        notifyObservers();
    }

    @Override
    public boolean processInput(String input) {
        if (!input.contains("=") ){
            System.out.println("No equal '=' sign");
            if (!TestMode) remainingAttempts--;
            return false;
        }
        if (!input.contains("+") && !input.contains("-") &&!input.contains("*") &&!input.contains("/")){
            System.out.println("There must be at least one sign '+' '-' '*' '/' ");
            if (!TestMode) remainingAttempts--;
            return false;
        }
        currentGuess = new StringBuilder(input);
        if (isEquation()){
            remainingAttempts--;
            if (currentGuess.toString().equals(targetNumber)){ // Determine if you guessed correctly
                gameWon = true;
            }
            setChanged();
            notifyObservers();
            return true;
        }
        System.out.println("The left side is not equal to the right");
        return false;
    }

    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    @Override
    public String getTargetNumber() {
        return targetNumber;
    }

    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }

    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    @Override
    public void startNewGame() {
        initialize();
    }
    private boolean hasPrecedence(char op1, char op2) {
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false;
        }
        return true;
    }

    private void processOperation(Stack<Integer> operandStack, Stack<Character> operatorStack) {
        char operator = operatorStack.pop();
        int operand2 = operandStack.pop();
        int operand1 = operandStack.pop();

        int result = 0;
        switch (operator) {
            case '+':
                result = operand1 + operand2;
                break;
            case '-':
                result = operand1 - operand2;
                break;
            case '*':
                result = operand1 * operand2;
                break;
            case '/':
                result = operand1 / operand2;
                break;
        }

        operandStack.push(result);
    }

    private int evaluateExpression(String expression) {
        Stack<Integer> operandStack = new Stack<>();
        Stack<Character> operatorStack = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == ' ') {
                continue;
            }

            if (ch >= '0' && ch <= '9') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (expression.charAt(i) >= '0' && expression.charAt(i) <= '9')) {
                    sb.append(expression.charAt(i));
                    i++;
                }
                i--;

                operandStack.push(Integer.parseInt(sb.toString()));

            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                while (!operatorStack.isEmpty() && hasPrecedence(ch, operatorStack.peek())) {
                    processOperation(operandStack, operatorStack);
                }
                operatorStack.push(ch);
            }
        }

        while (!operatorStack.isEmpty()) {
            processOperation(operandStack, operatorStack);
        }
        return operandStack.pop();
    }
    private boolean isEquation() {
        return
                evaluateExpression(currentGuess.toString().split("=")[0])
                ==
                evaluateExpression(currentGuess.toString().split("=")[1]);
    }

    // MODEL SETTING
    private void setTestMode(boolean testMode) {
        TestMode = testMode;
    }

    private void setFixdMode(boolean fixdMode) {
        FixdMode = fixdMode;
    }

    private void setDisMode(boolean disMode) {
        DisMode = disMode;
    }

    public void write(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Whether to enable test mode?");
        if ("yes".equals(scanner.next())){
           setTestMode(true);
        }
        System.out.print("Whether to enable the fixed mode?");
        if ("yes".equals(scanner.next())){
            setFixdMode(true);
        }
        System.out.print("Whether to enable the display mode?");
        if ("yes".equals(scanner.next())){
            setDisMode(true);
        }
    }
    public int[] verifiedNum() {
        int[] result = new int[currentGuess.length()];
        for (int i = 0; i < currentGuess.length(); i++) {
            if (this.targetNumber.charAt(i) == (currentGuess.charAt(i))) {
                result[i] = 1; // Indicates the correct position and character
                continue;
            }
            if (this.targetNumber.contains(String.valueOf(currentGuess.charAt(i)))) {
                result[i] = 2;
            }
        }
        return result;
    }
}
