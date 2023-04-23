package antifraud.presentation.validation;

import antifraud.business.security.validation.IPValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IPConstraintValidator implements ConstraintValidator<ValidIP, String> {

    @Override
    public void initialize(ValidIP constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return IPValidator.isValidIpv4(value);
    }
}
