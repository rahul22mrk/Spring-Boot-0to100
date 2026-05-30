package module2_homework.spring_web_mvc.mappers;


import module2_homework.spring_web_mvc.dtos.DepartmentDTO;
import module2_homework.spring_web_mvc.entities.Department;
import org.mapstruct.Mapper;

@Mapper( componentModel ="spring")
public interface DepartmentMapper {
    DepartmentDTO toDTO(Department department);

    Department toEntity(DepartmentDTO departmentDTO);

}
