package com.api.banking.service;

import com.api.banking.dto.AccountResponse;
import com.api.banking.dto.CreateAccountRequest;
import com.api.banking.dto.TransactionRequest;
import com.api.banking.dto.TransactionResponse;
import com.api.banking.entity.Account;
import com.api.banking.entity.Transaction;
import com.api.banking.entity.Transaction.TransactionStatus;
import com.api.banking.entity.Transaction.TransactionType;
import com.api.banking.exception.AccountAlreadyExistsException;
import com.api.banking.exception.AccountInactiveException;
import com.api.banking.exception.AccountNotFoundException;
import com.api.banking.exception.InsufficientBalanceException;
import com.api.banking.repository.AccountRepository;
import com.api.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        if (request.fullname() == null || request.fullname().isBlank())
            throw new IllegalArgumentException("Le nom complet est obligatoire");
        if (request.email() == null || request.email().isBlank())
            throw new IllegalArgumentException("L'email est obligatoire");
        if (request.phone() == null || request.phone().isBlank())
            throw new IllegalArgumentException("Le téléphone est obligatoire");

        if (accountRepository.existsByEmail(request.email()))
            throw new AccountAlreadyExistsException("email", request.email());
        if (accountRepository.existsByPhone(request.phone()))
            throw new AccountAlreadyExistsException("téléphone", request.phone());

        Account account = new Account();
        account.setFullname(request.fullname());
        account.setEmail(request.email());
        account.setPhone(request.phone());
        return AccountResponse.from(accountRepository.save(account));
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account account = findById(id);
        account.setActive(false);
        accountRepository.save(account);
    }

    public List<AccountResponse> listAccounts() {
        return accountRepository.findAll().stream().map(AccountResponse::from).toList();
    }

    public BigDecimal getBalance(Long id) {
        Account account = findById(id);
        if (!account.isActive()) throw new AccountInactiveException(id);
        return account.getBalance();
    }

    @Transactional
    public TransactionResponse credit(Long id, TransactionRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Le montant doit être positif");

        Account account = findById(id);
        if (!account.isActive()) throw new AccountInactiveException(id);

        Transaction tx = new Transaction();
        tx.setAccountId(id);
        tx.setType(TransactionType.CREDIT);
        tx.setAmount(request.amount());

        try {
            account.setBalance(account.getBalance().add(request.amount()));
            accountRepository.save(account);
            tx.setStatus(TransactionStatus.SUCCESS);
        } catch (Exception e) {
            tx.setStatus(TransactionStatus.FAILED);
        }

        transactionRepository.save(tx);
        return TransactionResponse.from(tx, account.getBalance());
    }

    @Transactional
    public TransactionResponse debit(Long id, TransactionRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Le montant doit être positif");

        Account account = findById(id);
        if (!account.isActive()) throw new AccountInactiveException(id);

        Transaction tx = new Transaction();
        tx.setAccountId(id);
        tx.setType(TransactionType.DEBIT);
        tx.setAmount(request.amount());

        if (account.getBalance().compareTo(request.amount()) < 0) {
            tx.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(tx);
            throw new InsufficientBalanceException();
        }

        try {
            account.setBalance(account.getBalance().subtract(request.amount()));
            accountRepository.save(account);
            tx.setStatus(TransactionStatus.SUCCESS);
        } catch (Exception e) {
            tx.setStatus(TransactionStatus.FAILED);
        }

        transactionRepository.save(tx);
        return TransactionResponse.from(tx, account.getBalance());
    }

    private Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }
}
