import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner scanner = new Scanner(System.in);
        System.out.println(isBalancedParentheses(scanner.nextLine()));

    }

    private static boolean isBalancedParentheses(String input) {
        Map<Character, Character> tagsMap = Map.of(
                '{', '}',
                '(', ')',
                '[', ']'
        );
        Deque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < input.length(); i++) {
            char currChar = input.charAt(i);
            if (tagsMap.containsKey(currChar)) {
                stack.push(currChar);
                continue;
            }

            if (stack.isEmpty() || currChar != tagsMap.get(stack.pop())) {
                return false;
            }
        }

        return stack.isEmpty();
    }
}