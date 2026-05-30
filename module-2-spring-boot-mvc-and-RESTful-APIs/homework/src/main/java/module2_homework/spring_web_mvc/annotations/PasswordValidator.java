package module2_homework.spring_web_mvc.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValidation,String> {
    private static final String PASSWORD_REGEX =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{10,}$";
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
//        a. contains one uppercase letter
//        b. contains one lowercase letter
//        c. contains one special character
//        d. minimum length is 10 characters

            if (password == null) {
                return false;
            }

            return password.matches(PASSWORD_REGEX);
    }
}
