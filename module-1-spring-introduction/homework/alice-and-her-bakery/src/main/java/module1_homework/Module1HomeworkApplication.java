package module1_homework;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Module1HomeworkApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(Module1HomeworkApplication.class, args);
		CakeBaker cakeBaker = context.getBean(CakeBaker.class);
		cakeBaker.bakeCake();
	}

}
