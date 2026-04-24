package com.Esteban.cinema.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Esteban.cinema.DTO.Request.LoginRequest;
import com.Esteban.cinema.Model.Users;
import com.Esteban.cinema.Repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users register(Users request) {
        Optional<Users> usr = userRepository.findByEmail(request.getEmail());

        if(usr.isEmpty()){
            Users newUsers = new Users();
            newUsers.setFullName(request.getFullName());
            newUsers.setEmail(request.getEmail());
            newUsers.setRole("USER");
            newUsers.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(newUsers);

            return newUsers;
        }else{
            throw new RuntimeException("Users with email " + request.getEmail() + " already exists.");
        }
    }


    public Users login(LoginRequest request) {
        Optional<Users> usr = userRepository.findByEmail(request.getEmail());

        if(usr.isPresent()){
            Users users = usr.get();
            if(passwordEncoder.matches(request.getPassword(), users.getPassword())){
                return usr.get();
            }else{
                throw new RuntimeException("Invalid password.");
            }
        }else{
            throw new RuntimeException("Users with email " + request.getEmail() + " not found.");
        }
    }

    public void updateUser(Users request, Long idUser) {
        Optional<Users> usr = userRepository.findById(idUser);

        if(usr.isPresent()){
            Users users = usr.get();
            users.setFullName(request.getFullName());
            users.setEmail(request.getEmail());
            if(request.getPassword() != null && !request.getPassword().isEmpty()){
                users.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            userRepository.save(users);
        }else{
            throw new RuntimeException("Users with id " + idUser + " not found.");
        }
    }

    public Users getUserDetails(Long idUser) {
        Optional<Users> usr = userRepository.findById(idUser);

        if(usr.isPresent()){
            return usr.get();
        }else{
            throw new RuntimeException("Users with id " + idUser + " not found.");
        }
    }
}


