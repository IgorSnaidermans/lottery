package lv.igors.lottery.code;

public class CodeDoesntExistException extends Exception {
    public CodeDoesntExistException(String message) {
        super(message);
    }
}
