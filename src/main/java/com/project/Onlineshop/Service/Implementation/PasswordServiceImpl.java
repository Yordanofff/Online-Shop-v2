package com.project.Onlineshop.Service.Implementation;


import com.project.Onlineshop.Entity.Employee;
import com.project.Onlineshop.Entity.User;
import com.project.Onlineshop.MyUserDetails;
import com.project.Onlineshop.Repository.EmployeeRepository;
import com.project.Onlineshop.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class PasswordServiceImpl {
    private final UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final EmployeeRepository employeeRepository;

    public PasswordServiceImpl(UserRepository userRepository,
                               EmployeeRepository employeeRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
    }

    public boolean isPasswordTheSame(User user, String password) {
        return bCryptPasswordEncoder.matches(password, user.getPassword());
    }
    public boolean isPasswordTheSame(Employee employee, String password) {
        return bCryptPasswordEncoder.matches(password, employee.getPassword());
    }

    public void updatePassword(MyUserDetails myUserDetails, String newPassword) {
        if(myUserDetails.getUser()!=null) {
            User user = myUserDetails.getUser();
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            Employee employee = myUserDetails.getEmployee();
            employee.setPassword(bCryptPasswordEncoder.encode(newPassword));
            employeeRepository.save(employee);
        }
    }

    public void validatePassword(String newPassword, String repeatPassword, Model model){
        if (!newPassword.equals(repeatPassword)) {
            model.addAttribute("not_matching_passwords", "New password and confirm password do not match!");
            return;
        }
        if (newPassword.isEmpty() || newPassword.isBlank()) {
            model.addAttribute("not_matching_passwords", "The new password cannot be empty!");
            return;
        }
        if (newPassword.length() < 3) {
            model.addAttribute("not_matching_passwords", "The new password cannot be less than 3 symbols long!");
        }
    }
    public void checkCurrentPassword(MyUserDetails myUserDetails, String currentPassword, Model model){
        if (myUserDetails.getUser() != null) {
            User user = myUserDetails.getUser();
            if (!isPasswordTheSame(user, currentPassword)) {
                model.addAttribute("wrong_password", "Incorrect current password!");
            }
        } else {
            Employee employee = myUserDetails.getEmployee();
            if (!isPasswordTheSame(employee, currentPassword)) {
                model.addAttribute("wrong_password", "Incorrect current password!");
            }
        }
    }
}
