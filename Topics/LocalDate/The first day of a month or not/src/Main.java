import java.time.LocalDate;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner scanner = new Scanner(System.in);

        try {
            int year = scanner.nextInt();
            int day = scanner.nextInt();
            LocalDate localDate = LocalDate.ofYearDay(year, day);

            if (localDate.getDayOfMonth() == 1) {
                System.out.println("true");
            } else {
                System.out.println("false");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}