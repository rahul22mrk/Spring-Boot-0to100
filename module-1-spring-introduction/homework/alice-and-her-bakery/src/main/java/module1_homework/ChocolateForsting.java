package module1_homework;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("chocolateForsting")
public class ChocolateForsting implements Frosting{
    @Override
    public String getFrostingType() {
        return "Chocolate Forsting";
    }
}
