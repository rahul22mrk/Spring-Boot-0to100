package module2_homework.spring_web_mvc.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PasswordValidator.class})
public @interface PasswordValidation {
    String message() default "Password must contain at least one uppercase letter, one lowercase letter, one special character and be at least 10 characters long";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
