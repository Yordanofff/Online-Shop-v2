package com.project.Onlineshop.Service;

import com.project.Onlineshop.Dto.Request.EmployeeRequestDto;
import com.project.Onlineshop.Dto.Response.EmployeeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmployeeService {
    EmployeeResponseDto getEmployeeByID(Long id);
    List<EmployeeResponseDto> getAllEmployees();
    EmployeeResponseDto create(EmployeeRequestDto employeeRequestDtoRequest);
    EmployeeResponseDto editEmployeeByID(Long id, EmployeeRequestDto employeeRequestDto);
}
