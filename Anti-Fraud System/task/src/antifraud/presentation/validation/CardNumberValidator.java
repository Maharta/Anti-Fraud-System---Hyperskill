package antifraud.presentation.validation;

import antifraud.business.security.validation.Luhn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CardNumberValidator implements ConstraintValidator<ValidCardNumber, String> {
    @Override
    public void initialize(ValidCardNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return Luhn.checkCardNumberValidity(value);
    }
}
