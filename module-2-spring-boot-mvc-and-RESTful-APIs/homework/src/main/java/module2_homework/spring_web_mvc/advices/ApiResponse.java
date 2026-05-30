package module2_homework.spring_web_mvc.advices;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ApiResponse<T> {

    private T data;
    private ApiError error;
    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy")
    private LocalDateTime timeStamp = LocalDateTime.now();


    public ApiResponse(T data){
        this();
        this.data = data;
    }

    public ApiResponse(ApiError error){
        this();
        this.error = error;
    }
}
