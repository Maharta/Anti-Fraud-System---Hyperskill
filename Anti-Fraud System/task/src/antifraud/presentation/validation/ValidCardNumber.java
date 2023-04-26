package antifraud.presentation.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The annotated element must be a valid card number that has 16 digits
 * and validated by the Luhn Algorithm.
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CardNumberValidator.class)
public @interface ValidCardNumber {
    String message() default "Invalid Card Number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
