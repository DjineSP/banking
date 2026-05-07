package com.api.banking.service;

import com.api.banking.dto.request.CreateAccountRequest;
import com.api.banking.dto.request.UpdateAccountRequest;
import com.api.banking.dto.response.AccountResponse;
import com.api.banking.entity.Account;
import com.api.banking.entity.Bank;
import com.api.banking.enums.AccountStatus;
import com.api.banking.exception.AccountNotFoundException;
import com.api.banking.exception.BankNotFoundException;
import com.api.banking.repository.AccountRepository;
import com.api.banking.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AccountService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"
    );

    // Accepte : 6XXXXXXXX, 2XXXXXXXX, +237XXXXXXXXX, 00237XXXXXXXXX (espaces ignorés)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+237|00237)?[62]\\d{8}$"
    );

    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        if (request.bankId() == null)
            throw new IllegalArgumentException("L'identifiant de la banque est obligatoire");
        if (request.fullname() == null || request.fullname().isBlank())
            throw new IllegalArgumentException("Le nom complet est obligatoire");
        if (request.email() == null || request.email().isBlank())
            throw new IllegalArgumentException("L'email est obligatoire");
        if (request.phone() == null || request.phone().isBlank())
            throw new IllegalArgumentException("Le numéro de téléphone est obligatoire");

        validateEmail(request.email());
        validatePhone(request.phone());

        Bank bank = bankRepository.findById(request.bankId())
                .orElseThrow(() -> new BankNotFoundException(request.bankId()));
        if (!bank.isActive())
            throw new IllegalArgumentException("Impossible de créer un compte dans une banque désactivée");

        Account account = new Account();
        account.setBank(bank);
        account.setAccountNumber(generateAccountNumber(bank.getCode()));
        account.setFullname(request.fullname().trim());
        account.setEmail(request.email().trim().toLowerCase());
        account.setPhone(normalizePhone(request.phone()));
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(String accountNumber) {
        return AccountResponse.from(findByAccountNumber(accountNumber));
    }

    @Transactional
    public AccountResponse updateAccount(String accountNumber, UpdateAccountRequest request) {
        Account account = findByAccountNumber(accountNumber);

        if (request.fullname() != null && !request.fullname().isBlank())
            account.setFullname(request.fullname().trim());

        if (request.phone() != null && !request.phone().isBlank()) {
            validatePhone(request.phone());
            account.setPhone(normalizePhone(request.phone()));
        }

        if (request.email() != null && !request.email().isBlank()) {
            validateEmail(request.email());
            account.setEmail(request.email().trim().toLowerCase());
        }

        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional
    public AccountResponse suspendAccount(String accountNumber) {
        Account account = findByAccountNumber(accountNumber);
        if (account.getStatus() == AccountStatus.CLOSED)
            throw new IllegalArgumentException("Un compte clôturé ne peut pas être suspendu");
        account.setStatus(AccountStatus.SUSPENDED);
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional
    public AccountResponse closeAccount(String accountNumber) {
        Account account = findByAccountNumber(accountNumber);
        account.setStatus(AccountStatus.CLOSED);
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional
    public AccountResponse activateAccount(String accountNumber) {
        Account account = findByAccountNumber(accountNumber);
        if (account.getStatus() == AccountStatus.CLOSED)
            throw new IllegalArgumentException("Un compte clôturé ne peut pas être réactivé");
        account.setStatus(AccountStatus.ACTIVE);
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> listAccounts() {
        return accountRepository.findAll().stream().map(AccountResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Account findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email.trim()).matches())
            throw new IllegalArgumentException("Format d'email invalide : " + email);
    }

    private void validatePhone(String phone) {
        String normalized = phone.replaceAll("\\s", "");
        if (!PHONE_PATTERN.matcher(normalized).matches())
            throw new IllegalArgumentException(
                    "Format de numéro invalide. Exemples acceptés : 677123456, +237 677123456");
    }

    private String normalizePhone(String phone) {
        return phone.replaceAll("\\s", "");
    }

    private String generateAccountNumber(String bankCode) {
        String accountNumber;
        do {
            accountNumber = bankCode + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
