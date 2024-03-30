package com.project.Onlineshop;

import com.project.Onlineshop.Entity.User;
import com.project.Onlineshop.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByEmailOrUsername(username);
    }


    private UserDetails loadUserByName(String username) throws UsernameNotFoundException{
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()){
            throw new UsernameNotFoundException("Could not find user");
        }

        return new MyUserDetails(optionalUser.get());
    }


    private UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            throw new UsernameNotFoundException("Could not find user");
        }

        return new MyUserDetails(optionalUser.get());
    }

    private UserDetails loadUserByEmailOrUsername(String emailOrUsername){
        Optional<User> optionalUser = userRepository.findByEmail(emailOrUsername);
        if (optionalUser.isPresent()){
            return new MyUserDetails(optionalUser.get());
        }
        optionalUser = userRepository.findByUsername(emailOrUsername);
        if (optionalUser.isPresent()){
            return new MyUserDetails(optionalUser.get());
        }
        throw new UsernameNotFoundException("Could not find user");
    }
}