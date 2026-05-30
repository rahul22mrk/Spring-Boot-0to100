package module1_homework;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.awt.event.FocusListener;

@Component("strawerryFrosting")
public class StrawerryFrosting implements Frosting {
    @Override
    public String getFrostingType() {
        return "Strawerry Frosting";
    }
}
