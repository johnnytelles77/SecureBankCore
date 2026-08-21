package com.johnny.securebank.controller;

import com.johnny.securebank.dto.*;
import com.johnny.securebank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user in the banking system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data" , content = @Content(
                          mediaType = "application/json",
                          schema = @Schema(implementation = ValidationErrorResponseDTO.class)
                  )
            ),
            @ApiResponse(responseCode = "409", description = "Email already exists" , content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@Valid @RequestBody CreateUserRequestDTO request) {
        return userService.registerUser(request);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDTO.class)
            )
            ),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )
            )
    })
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves all users from the banking system."
    )
    @ApiResponse(
            responseCode = "200", description = "Users retrieved successfully", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))
         )
    )
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getUsers();
    }

    @Operation(
            summary = "Update user",
            description = "Updates an existing user's information by its unique identifier."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "User updated successfully", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)
                    )
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
                    )
            }
    )
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@Valid @RequestBody UpdateUserRequestDTO request, @PathVariable Long id) {
        return userService.updateUser(id, request);
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes an existing user by its unique identifier."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    ))
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}