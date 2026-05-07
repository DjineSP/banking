package com.api.banking.controller;

import com.api.banking.dto.ApiResponse;
import com.api.banking.dto.request.InterBankTransferRequest;
import com.api.banking.dto.request.TransactionRequest;
import com.api.banking.dto.request.TransferRequest;
import com.api.banking.dto.response.AccountResponse;
import com.api.banking.dto.response.TransactionResponse;
import com.api.banking.service.AccountService;
import com.api.banking.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client/accounts")
@RequiredArgsConstructor
@Tag(name = "Client", description = "Opérations bancaires client")
public class ClientController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Consulter son compte")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Détails du compte",
                accountService.getAccount(accountNumber)));
    }

    @GetMapping("/{accountNumber}/transactions")
    @Operation(summary = "Consulter l'historique des transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Historique des transactions",
                transactionService.getTransactions(accountNumber)));
    }

    @PostMapping("/{accountNumber}/deposit")
    @Operation(summary = "Effectuer un dépôt")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @PathVariable String accountNumber, @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Dépôt effectué avec succès",
                transactionService.deposit(accountNumber, request)));
    }

    @PostMapping("/{accountNumber}/withdrawal")
    @Operation(summary = "Effectuer un retrait")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdrawal(
            @PathVariable String accountNumber, @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Retrait effectué avec succès",
                transactionService.withdrawal(accountNumber, request)));
    }

    @PostMapping("/{accountNumber}/transfer")
    @Operation(summary = "Effectuer un virement intrabank")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> transferIntra(
            @PathVariable String accountNumber, @RequestBody TransferRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Virement intrabank effectué avec succès",
                transactionService.transferIntra(accountNumber, request)));
    }

    @PostMapping("/{accountNumber}/transfer/interbank")
    @Operation(summary = "Effectuer un virement interbank")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> transferInter(
            @PathVariable String accountNumber, @RequestBody InterBankTransferRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Virement interbank effectué avec succès",
                transactionService.transferInter(accountNumber, request)));
    }
}
