package antifraud.presentation.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * The annotated element must be a valid ipv4 address.
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IPConstraintValidator.class)
public @interface ValidIP {
    String message() default "Invalid IPV4 Address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
