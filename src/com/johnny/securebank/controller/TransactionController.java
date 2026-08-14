package com.johnny.securebank.controller;

import com.johnny.securebank.dto.*;
import com.johnny.securebank.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(
            summary = "Deposit funds",
            description = "Deposits funds into an existing bank account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deposit successful",  content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
    })
    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDTO deposit(@Valid @RequestBody AccountAmountRequestDTO request) {
        return transactionService.deposit(
                request.getAccountId(),
                request.getAmount());
    }

    @Operation(
            summary = "Withdraw funds",
            description = "Withdraws funds from an existing bank account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Withdraw successful",  content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
    })
    @PostMapping("/withdraw")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDTO withdraw(@Valid @RequestBody AccountAmountRequestDTO request) {
        return transactionService.withdraw(
                request.getAccountId(),
                request.getAmount()
        );
    }

    @Operation(
            summary = "Transfer funds",
            description = "Transfers funds from one bank account to another."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transfer successful",  content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponseDTO.class)
            )),
            @ApiResponse(responseCode = "404", description = "Source or destination account not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDTO.class)
            )),
    })
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDTO transfer(@Valid @RequestBody TransferRequestDTO request) {
        return transactionService.transfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount()
        );
    }

    @Operation(
            summary = "Get transactions by account ID",
            description = "Retrieves all transactions associated with a bank account."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Transactions retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = TransactionResponseDTO.class)
                            ))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    ))
    })
    @GetMapping("/account/{id}")
    public List<TransactionResponseDTO> getTransactionByAccountId(@PathVariable Long id) {
        return transactionService.getTransactionsByAccountId(id);
    }
}