import java.time.LocalDate;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner scanner = new Scanner(System.in);

        int year = scanner.nextInt();
        int month = scanner.nextInt();
        int dayFromLast = scanner.nextInt();
        LocalDate firstDateOfNextMonth = LocalDate.of(year, month, 1).plusMonths(1);
        System.out.println(firstDateOfNextMonth.minusDays(dayFromLast));
    }
}