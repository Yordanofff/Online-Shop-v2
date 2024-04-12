package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByPhoneNumber(String phoneNumber);
    Optional<Employee> findByUsername(String username);

    List<Employee> findByRole_IdNot(Long roleId);
}
