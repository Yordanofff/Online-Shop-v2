package com.project.Onlineshop.Service.Implementation;

import com.project.Onlineshop.Dto.Request.UserRequestDto;
import com.project.Onlineshop.Dto.Response.UserResponseDto;
import com.project.Onlineshop.Entity.Order;
import com.project.Onlineshop.Entity.Role;
import com.project.Onlineshop.Entity.User;
import com.project.Onlineshop.Exceptions.EmailInUseException;
import com.project.Onlineshop.Exceptions.PasswordsNotMatchingException;
import com.project.Onlineshop.Exceptions.ServerErrorException;
import com.project.Onlineshop.Exceptions.UsernameInUseException;
import com.project.Onlineshop.Mapper.UserMapper;
import com.project.Onlineshop.MyUserDetails;
import com.project.Onlineshop.Repository.*;
import com.project.Onlineshop.Service.UserService;
import com.project.Onlineshop.Static.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;
    private final OrderProductRepository orderProductRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public UserResponseDto addUser(UserRequestDto userRequestDto) {
        if (isEmailInDB(userRequestDto.getEmail())) {
            throw new EmailInUseException("Email already in use. Please use a different email");
        }
        if (isUsernameInDB(userRequestDto.getUsername())) {
            throw new UsernameInUseException("Username already in use. Please use a different username");
        }
        validatePasswordsAreMatching(userRequestDto);

        Role userRole = new Role(RoleType.ROLE_USER.getId(), RoleType.ROLE_USER.name());

        try {
            User user = userMapper.toEntity(userRequestDto);
            user.setRole(userRole);
            user.setPassword(encoder.encode(userRequestDto.getPassword()));
            userRepository.save(user);
            return userMapper.toDto(user);
        } catch (Exception exception) {
            throw new ServerErrorException("An internal error occurred. Please try again. " + exception.getMessage());
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
            throw new PasswordsNotMatchingException("Passwords don't match");
        }
    }

    public String registerNewUser(UserRequestDto userRequestDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userRequestDto", userRequestDto);
            return "register_user";
        }
        try {
            addUser(userRequestDto);
        } catch (EmailInUseException e) {
            model.addAttribute("userRequestDto", userRequestDto);
            model.addAttribute("email_error", e.getMessage());
            return "register_user";
        } catch (UsernameInUseException e) {
            model.addAttribute("userRequestDto", userRequestDto);
            model.addAttribute("user_error", e.getMessage());
            return "register_user";
        } catch (PasswordsNotMatchingException e) {
            model.addAttribute("userRequestDto", userRequestDto);
            model.addAttribute("password_error", e.getMessage());
            return "register_user";
        }
        return "redirect:/";
    }

    public String showProfile(Model model, Authentication authentication){
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        List<Order> orderList = orderRepository.findByUserIdAndStatusName(user.getId(), "DELIVERED");
        model.addAttribute("userDetails",userDetails);
        model.addAttribute("orderedProducts", orderProductRepository.findAll());
        model.addAttribute("orderStatuses", orderStatusRepository.findAll());
        model.addAttribute("orders",orderList);
        model.addAttribute("products", productRepository.findAll());
        return "profile";
    }

}
