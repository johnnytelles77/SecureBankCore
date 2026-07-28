package com.johnny.securebank.service;

import com.johnny.securebank.dto.UserResponseDTO;
import com.johnny.securebank.exception.DuplicateEmailException;
import com.johnny.securebank.exception.UserNotFoundException;
import com.johnny.securebank.model.User;
import com.johnny.securebank.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public UserResponseDTO registerUser(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new DuplicateEmailException("Email already exists!");
        }
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

    public UserResponseDTO updateUser(Long id, User updatedUser){
        User existingUser = findUserById(id);

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());

        User savedUser = userRepository.save(existingUser);
        return convertToResponseDTO(savedUser);
    }

    public void deleteUser(Long id){
        User existingUser = findUserById(id);
        userRepository.delete(existingUser);
    }
}