package com.api.banking.controller;

import com.api.banking.dto.ApiResponse;
import com.api.banking.dto.request.CreateAccountRequest;
import com.api.banking.dto.request.UpdateAccountRequest;
import com.api.banking.dto.response.AccountResponse;
import com.api.banking.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
@Tag(name = "Admin - Comptes", description = "Gestion des comptes bancaires")
public class AdminController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Créer un nouveau compte")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compte créé avec succès", accountService.createAccount(request)));
    }

    @GetMapping
    @Operation(summary = "Lister tous les comptes")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> listAccounts() {
        return ResponseEntity.ok(ApiResponse.ok("Liste des comptes", accountService.listAccounts()));
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Consulter un compte")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Détails du compte", accountService.getAccount(accountNumber)));
    }

    @PatchMapping("/{accountNumber}")
    @Operation(summary = "Modifier un compte")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(
            @PathVariable String accountNumber, @RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Compte mis à jour avec succès",
                accountService.updateAccount(accountNumber, request)));
    }

    @PutMapping("/{accountNumber}/suspend")
    @Operation(summary = "Suspendre un compte")
    public ResponseEntity<ApiResponse<AccountResponse>> suspendAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Compte suspendu avec succès",
                accountService.suspendAccount(accountNumber)));
    }

    @PutMapping("/{accountNumber}/close")
    @Operation(summary = "Clôturer un compte")
    public ResponseEntity<ApiResponse<AccountResponse>> closeAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Compte clôturé avec succès",
                accountService.closeAccount(accountNumber)));
    }

    @PutMapping("/{accountNumber}/activate")
    @Operation(summary = "Réactiver un compte")
    public ResponseEntity<ApiResponse<AccountResponse>> activateAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.ok("Compte réactivé avec succès",
                accountService.activateAccount(accountNumber)));
    }
}
