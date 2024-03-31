package com.project.Onlineshop.Service.Implementation;

import com.project.Onlineshop.Dto.Request.EmployeeRequestDto;
import com.project.Onlineshop.Dto.Request.UserRequestDto;
import com.project.Onlineshop.Dto.Response.UserResponseDto;
import com.project.Onlineshop.Entity.Role;
import com.project.Onlineshop.Entity.User;
import com.project.Onlineshop.Mapper.UserMapper;
import com.project.Onlineshop.Repository.RoleRepository;
import com.project.Onlineshop.Repository.UserRepository;
import com.project.Onlineshop.Service.UserService;
import com.project.Onlineshop.Static.RoleType;
//import com.project.Onlineshop.Utility.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;

    @Override
    public UserResponseDto addUser(UserRequestDto userRequestDto) {
        if (isEmailInDB(userRequestDto.getEmail())) {
            throw new RuntimeException("Email already in use. Please use a different email");
        }
        if (isUsernameInDB(userRequestDto.getUsername())) {
            throw new RuntimeException("Username already in use. Please use a different username");
        }
        validatePasswordsAreMatching(userRequestDto);

        Optional<Role> optionalRole = roleRepository.findByName(RoleType.ROLE_USER.name());
        if (optionalRole.isEmpty()) {
            throw new RuntimeException("User role not found in the DB");
        }

        try {
            User user = userMapper.toEntity(userRequestDto);
            user.setRole(optionalRole.get());
            user.setPassword(encoder.encode(userRequestDto.getPassword()));
            userRepository.save(user);
            return userMapper.toDto(user);
        } catch (Exception exception) {
            throw new RuntimeException("An internal error occurred. Please try again. " + exception.getMessage());
        }
    }

    private boolean isEmailInDB(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private boolean isUsernameInDB(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    private void validatePasswordsAreMatching(UserRequestDto userRequestDto) {
        if (!userRequestDto.getPassword().equals(userRequestDto.getRepeatedPassword())) {
            throw new RuntimeException("Passwords don't match");
        }
    }

    public String registerNewUser(UserRequestDto user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }
        if(user.getFirstName().length()<3 || user.getFirstName().length()>30){
            model.addAttribute("firstname_error","You must enter First name between 3 and 40");
        }
        addUser(user);
        return "redirect:/index";
    }

}
