package antifraud.business.security.validation;

import java.util.Arrays;

public class IPValidator {

    public static boolean isValidIpv4(String ipv4) {
        String[] splitted = ipv4.split("\\.");

        if (splitted.length != 4) {
            return false;
        }

        try {
            return Arrays.stream(splitted).allMatch((ipString) -> {
                int currentIP = Integer.parseInt(ipString);
                return currentIP >= 0 && currentIP <= 255;
            });
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }


    }
}
