package module2_homework.spring_web_mvc.Mapper;


import module2_homework.spring_web_mvc.DTO.DepartmentDTO;
import module2_homework.spring_web_mvc.Entity.Department;
import org.mapstruct.Mapper;

@Mapper( componentModel ="spring")
public interface DepartmentMapper {
    DepartmentDTO toDTO(Department department);

    Department toEntity(DepartmentDTO departmentDTO);

}
