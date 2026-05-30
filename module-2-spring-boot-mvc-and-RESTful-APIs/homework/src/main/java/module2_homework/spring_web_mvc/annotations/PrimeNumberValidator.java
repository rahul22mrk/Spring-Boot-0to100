package module2_homework.spring_web_mvc.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PrimeNumberValidator implements ConstraintValidator<PrimeNumberValidation,Integer> {

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return isPrimeNumber(integer);
    }

    private boolean isPrimeNumber(Integer number){
        if(number<2) return false;

        if(number == 2 || number == 3) return true;

        if(number %2==0) return false;

        for(int i=5;i *i<=number; i+=6){
            if(number%i==0 || number%(i+2)==0){
                return false;
            }

        }

        return true;
    }
}
