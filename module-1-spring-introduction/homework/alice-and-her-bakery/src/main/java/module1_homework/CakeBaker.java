package module1_homework;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CakeBaker {
    private final Syrup syrup;
    private final Frosting frosting;

    public CakeBaker(@Qualifier("chocolateForsting") Frosting frosting, @Qualifier("chocolateSyrup")Syrup syrup){
        this.frosting = frosting;
        this.syrup = syrup;
    }
    public  void bakeCake(){
        System.out.println("Cake Baker....");
        System.out.println(syrup.getSyrupType());
        System.out.println(frosting.getFrostingType());

    }
}
