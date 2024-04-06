package com.project.Onlineshop.Controller;

import com.project.Onlineshop.Entity.User;
import com.project.Onlineshop.MyUserDetails;
import com.project.Onlineshop.Service.Implementation.UserServiceImpl;
import com.project.Onlineshop.Service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordController {

    private final UserServiceImpl userService;

    public PasswordController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "change_password";
    }

    @PostMapping("/save-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String repeatPassword,
                                 Model model, Authentication authentication
    ) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        User user = myUserDetails.getUser();

        if (!newPassword.equals(repeatPassword)) {
            model.addAttribute("not_matching_passwords", "New password and confirm password do not match!");
            return "change_password";
        }

        if (!userService.checkPassword(user, currentPassword)) {
            model.addAttribute("wrong_password", "Incorrect current password!");
            return "change_password";
        }

        userService.updatePassword(user, newPassword);
        model.addAttribute("success", "Password was updated successfully!");
        return "change_password";
    }
}
