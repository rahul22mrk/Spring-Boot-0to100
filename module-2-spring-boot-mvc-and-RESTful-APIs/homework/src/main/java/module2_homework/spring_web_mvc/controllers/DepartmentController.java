package module2_homework.spring_web_mvc.controllers;


import jakarta.validation.Valid;
import module2_homework.spring_web_mvc.advices.ApiResponse;
import module2_homework.spring_web_mvc.dtos.DepartmentDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @GetMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> departments(){
        DepartmentDTO dto = new DepartmentDTO();
        return new ResponseEntity<>(new ApiResponse<>(dto), HttpStatus.FOUND);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentDTO>> department(@Valid @RequestBody DepartmentDTO dto){
        return new ResponseEntity<>(new ApiResponse<>(dto), HttpStatus.CREATED);
    }

    @PutMapping
    public  ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(@Valid @RequestBody DepartmentDTO dto){
        return new ResponseEntity<>(new ApiResponse<>(dto), HttpStatus.OK);
    }

    @DeleteMapping(path="/{deptId}")
    public ResponseEntity<ApiResponse<String>> deleteDepartment(@PathVariable("deptId") String id){
        return new ResponseEntity<>(new ApiResponse<>("Deleted : "+id), HttpStatus.OK);
    }

    @GetMapping("/{deptId}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> department(@PathVariable("deptId") String id){
        return new ResponseEntity<>(new ApiResponse<>(new DepartmentDTO()), HttpStatus.FOUND);
    }

}
