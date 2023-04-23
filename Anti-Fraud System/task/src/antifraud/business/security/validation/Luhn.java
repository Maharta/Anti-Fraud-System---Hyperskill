package antifraud.business.security.validation;

public class Luhn {
    public static String generateValidChecksum(String numberSequence) {
        int sum = 0;
        for (int i = 0; i < numberSequence.length(); i++) {
            int num = numberSequence.charAt(i) - '0';
            if (i % 2 == 0) {
                num *= 2;
                if (num > 9) {
                    num -= 9;
                }
            }
            sum += num;
        }
        if (sum % 10 == 0) {
            return "0";
        }
        return String.valueOf(10 - (sum % 10));
    }

    public static boolean checkCardNumberValidity(String cardNumber) {
        String noChecksumCard = cardNumber.substring(0, cardNumber.length() - 1);
        char validChecksum = generateValidChecksum(noChecksumCard).charAt(0);
        char cardChecksum = cardNumber.charAt(cardNumber.length() - 1);

        return cardChecksum == validChecksum;
    }
}
