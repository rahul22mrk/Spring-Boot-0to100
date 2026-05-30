package module1_homework;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("strawerrySyrup")
public class StrawerrySyrup implements Syrup{
    @Override
    public String getSyrupType() {
        return "StrawberrySyrup";
    }
}
