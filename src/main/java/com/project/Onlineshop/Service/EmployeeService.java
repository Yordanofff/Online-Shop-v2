package com.project.Onlineshop.Service;

import com.project.Onlineshop.Dto.Request.EmployeeRequestDto;
import com.project.Onlineshop.Dto.Response.EmployeeResponseDto;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Service
public interface EmployeeService {
    EmployeeResponseDto getEmployeeByID(Long id);
    List<EmployeeResponseDto> getAllEmployees();
    EmployeeResponseDto create(EmployeeRequestDto employeeRequestDtoRequest);
    EmployeeResponseDto editEmployeeByID(Long id, EmployeeRequestDto employeeRequestDto);
    String showProfile(Model model, Authentication authentication);
    String registerNewEmployee(@ModelAttribute @Valid EmployeeRequestDto employeeRequestDto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes);
}
