package module2_homework.spring_web_mvc.Controller;


import module2_homework.spring_web_mvc.DTO.DepartmentDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @GetMapping
    public DepartmentDTO departments(){
        DepartmentDTO dto = new DepartmentDTO();
        return dto;
    }

    @PostMapping
    public DepartmentDTO department(@RequestBody DepartmentDTO dto){
        return dto;
    }

    @PutMapping
    public  DepartmentDTO updateDepartment(@RequestBody DepartmentDTO dto){
        return dto;
    }

    @DeleteMapping(path="/{deptId}")
    public String deleteDepartment(@PathVariable("deptId") String id){
        return "Deleted : "+id;
    }

    @GetMapping("/{deptId}")
    public DepartmentDTO department(@PathVariable("deptId") String id){
        return new DepartmentDTO();
    }

}
