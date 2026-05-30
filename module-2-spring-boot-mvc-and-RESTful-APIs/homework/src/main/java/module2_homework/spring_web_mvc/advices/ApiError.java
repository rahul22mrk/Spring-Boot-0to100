package module2_homework.spring_web_mvc.advices;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ApiError {
    private HttpStatus status;
    private String message;
    private Map<String, List<String>> subErrors;
    private Integer errorCount;
}
