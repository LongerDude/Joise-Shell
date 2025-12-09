package service;

public class CharStream {
    private String input;
    private int position;

    public CharStream(String input) {
        this.input = input;
        this.position = -1;
    }
    public boolean hasNext(){
        return position < input.length() -1;
    }
    public char peek() {
        if (hasNext()) {
            return input.charAt(position + 1);
        }
        return '0';

    }
    public char next() {
        if (hasNext()) {
            position++;
            char nextChar = input.charAt(position);
            return nextChar;
        }
        return '0';
    }
    public void consumeNext() {
        position++;
    }
}
