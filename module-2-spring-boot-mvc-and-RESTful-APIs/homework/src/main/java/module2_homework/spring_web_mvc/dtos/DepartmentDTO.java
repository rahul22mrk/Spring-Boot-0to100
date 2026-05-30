package module2_homework.spring_web_mvc.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

    @NotBlank(message = "id must not be blank")
    private String id;
    @NotBlank(message = "title must not be blank")
    private String title;
    @NotNull(message = "isActive must not be null")
    private Boolean isActive;
}
