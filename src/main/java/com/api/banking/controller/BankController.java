package com.api.banking.controller;

import com.api.banking.dto.ApiResponse;
import com.api.banking.dto.request.BankRequest;
import com.api.banking.dto.response.BankResponse;
import com.api.banking.service.BankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/banks")
@RequiredArgsConstructor
@Tag(name = "Admin - Banques", description = "Gestion de banques")
public class BankController {

    private final BankService bankService;

    @PostMapping
    @Operation(summary = "Enregistrer une nouvelle banque")
    public ResponseEntity<ApiResponse<BankResponse>> registerBank(@RequestBody BankRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Banque enregistrée avec succès", bankService.registerBank(request)));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les banques")
    public ResponseEntity<ApiResponse<List<BankResponse>>> listBanks() {
        return ResponseEntity.ok(ApiResponse.ok("Liste des banques", bankService.listBanks()));
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver une banque")
    public ResponseEntity<ApiResponse<BankResponse>> deactivateBank(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Banque désactivée avec succès", bankService.deactivateBank(id)));
    }
}
