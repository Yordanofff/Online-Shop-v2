package com.project.Onlineshop.Controller;

import com.project.Onlineshop.MyUserDetails;
import com.project.Onlineshop.Service.Implementation.PasswordServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/password")
@RequiredArgsConstructor

public class PasswordController {
    private final PasswordServiceImpl passwordService;

    @GetMapping("/change")
    public String changePasswordForm() {
        return "change_password";
    }

    @PostMapping("/save")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String repeatPassword,
                                 Model model, Authentication authentication) {
        MyUserDetails myUserDetails = (MyUserDetails) authentication.getPrincipal();
        boolean r1 = passwordService.isCurrentPasswordCorrect(myUserDetails, currentPassword, model);
        boolean r2 = passwordService.isNewPasswordMatching(newPassword, repeatPassword, model);
        if (r1 && r2) {
            passwordService.updatePassword(myUserDetails, newPassword);
            model.addAttribute("success", "Password was updated successfully!");
        }
        return "change_password";
    }
}
