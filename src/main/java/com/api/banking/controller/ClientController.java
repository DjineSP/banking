package com.api.banking.controller;

import com.api.banking.dto.ApiResponse;
import com.api.banking.dto.TransactionRequest;
import com.api.banking.dto.TransactionResponse;
import com.api.banking.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;


@RestController
@RequestMapping("/client/accounts")
@RequiredArgsConstructor
@Tag(name = "Client", description = "Opérations bancaires client")
public class ClientController {

    private final AccountService accountService;

    @GetMapping("/{id}/balance")
    @Operation(summary = "Voir le solde d'un compte")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBalance(@PathVariable Long id) {
        BigDecimal balance = accountService.getBalance(id);
        return ResponseEntity.ok(ApiResponse.ok("Solde récupéré avec succès",
                Map.of("account_id", id, "balance", balance)));
    }

    @PostMapping("/{id}/credit")
    @Operation(summary = "Créditer un compte")
    public ResponseEntity<ApiResponse<TransactionResponse>> credit(@PathVariable Long id,
                                                                    @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Crédit effectué avec succès",
                accountService.credit(id, request)));
    }

    @PostMapping("/{id}/debit")
    @Operation(summary = "Débiter un compte")
    public ResponseEntity<ApiResponse<TransactionResponse>> debit(@PathVariable Long id,
                                                                   @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Débit effectué avec succès",
                accountService.debit(id, request)));
    }
}
