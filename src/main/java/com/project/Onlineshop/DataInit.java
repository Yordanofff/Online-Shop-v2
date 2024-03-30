package com.project.Onlineshop;

import com.project.Onlineshop.Entity.Role;
import com.project.Onlineshop.Repository.RoleRepository;
import com.project.Onlineshop.Static.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInit implements ApplicationRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (roleRepository.count() == 0) {
            for (RoleType roleType : RoleType.values()) {
                Role role = new Role();
                role.setName(roleType.name());
                roleRepository.save(role);
            }
        }
    }
}
