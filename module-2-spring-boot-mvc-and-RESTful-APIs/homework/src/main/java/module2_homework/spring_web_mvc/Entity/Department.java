package module2_homework.spring_web_mvc.Entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    private String id;
    private String title;
    private Boolean isActive;
    private Date createdAt;
}
