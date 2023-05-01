import java.time.LocalDate;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        // put your code here
        Scanner scanner = new Scanner(System.in);

        try {
            int year = scanner.nextInt();
            int firstDateDay = scanner.nextInt();
            int secondDateDay = scanner.nextInt();
            int thirdDateDay = scanner.nextInt();


            LocalDate firstDate = LocalDate.ofYearDay(year, firstDateDay);
            LocalDate secondDate = LocalDate.ofYearDay(year, secondDateDay);
            LocalDate thirdDate = LocalDate.ofYearDay(year, thirdDateDay);

            System.out.println(firstDate);
            System.out.println(secondDate);
            System.out.println(thirdDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}