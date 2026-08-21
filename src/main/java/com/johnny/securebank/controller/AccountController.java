package com.johnny.securebank.controller;

import com.johnny.securebank.dto.*;
import com.johnny.securebank.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Create a new account",
            description = "Creates a new account in the banking system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AccountResponseDTO.class)
            )),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "409", description = "Account number already exists" , content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            ))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponseDTO createAccount(@Valid @RequestBody CreateAccountRequestDTO request) {
        return accountService.createAccount(request);
    }

    @Operation(
            summary = "Get all accounts",
            description = "Retrieves all accounts from the banking system."
    )
    @ApiResponse(
            responseCode = "200", description = "Accounts retrieved successfully", content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = AccountResponseDTO.class))
    )
    )
    @GetMapping
    public List<AccountResponseDTO> getAccounts() {
        return  accountService.getAccounts();
    }

    @Operation(
            summary = "Get account by ID",
            description = "Retrieves an account by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AccountResponseDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            ))
    })
    @GetMapping("/{id}")
    public AccountResponseDTO getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @Operation(
            summary = "Update account status",
            description = "Updates the status of an existing account by its unique identifier."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200", description = "Account status updated successfully", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDTO.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponseDTO.class)
                    )),
                    @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    ))
            }
    )
    @PatchMapping("/{id}/status")
    public AccountResponseDTO updateAccountStatus(@PathVariable Long id, @Valid @RequestBody UpdateAccountStatusRequestDTO request) {
        return accountService.updateAccountStatus(id, request);
    }

    @Operation(
            summary = "Close account",
            description = "Closes an existing account by its unique identifier."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Account closed successfully", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDTO.class)
                    )
                    ),
                    @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    ))
            }
    )
    @DeleteMapping("/{id}")
    public AccountResponseDTO closeAccount(@PathVariable Long id) {
        return accountService.closeAccount(id);
    }
}