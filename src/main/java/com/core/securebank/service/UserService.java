package com.core.securebank.service;

import com.core.securebank.dto.CreateUserRequestDTO;
import com.core.securebank.dto.UpdateUserRequestDTO;
import com.core.securebank.dto.UserResponseDTO;
import com.core.securebank.exception.DuplicateEmailException;
import com.core.securebank.exception.UserNotFoundException;
import com.core.securebank.model.User;
import com.core.securebank.model.enums.Role;
import com.core.securebank.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole()
        );
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found!"));
    }

    public UserResponseDTO registerUser(CreateUserRequestDTO request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException("Email already exists!");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                encodedPassword,
                Role.CUSTOMER
        );

        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        return convertToResponseDTO(savedUser);
    }

    public List<UserResponseDTO> getUsers(){
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public UserResponseDTO getUserById(Long id){
        User user = findUserById(id);

        return convertToResponseDTO(user);
    }

    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request){
        User existingUser = findUserById(id);

        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setEmail(request.getEmail());

        User savedUser = userRepository.save(existingUser);
        return convertToResponseDTO(savedUser);
    }

    public void deleteUser(Long id){
        User existingUser = findUserById(id);
        userRepository.delete(existingUser);
    }
}