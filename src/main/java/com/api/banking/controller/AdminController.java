package com.api.banking.controller;

import com.api.banking.dto.AccountResponse;
import com.api.banking.dto.ApiResponse;
import com.api.banking.dto.CreateAccountRequest;
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
@Tag(name = "Admin", description = "Gestion des comptes bancaires")
public class AdminController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Créer un nouveau compte")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compte créé avec succès", accountService.createAccount(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer (désactiver) un compte")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok(ApiResponse.ok("Compte désactivé avec succès", null));
    }

    @GetMapping
    @Operation(summary = "Lister tous les comptes")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> listAccounts() {
        return ResponseEntity.ok(ApiResponse.ok("Liste des comptes", accountService.listAccounts()));
    }
}
