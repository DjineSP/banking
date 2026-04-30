package com.api.banking.service;

import com.api.banking.dto.AccountResponse;
import com.api.banking.dto.CreateAccountRequest;
import com.api.banking.dto.TransactionRequest;
import com.api.banking.dto.TransactionResponse;
import com.api.banking.dto.TransferRequest;
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

    @Transactional
    public AccountResponse activateAccount(Long id) {
        Account account = findById(id);
        account.setActive(true);
        return AccountResponse.from(accountRepository.save(account));
    }

    public List<AccountResponse> listAccounts() {
        return accountRepository.findAll().stream().map(AccountResponse::from).toList();
    }

    public BigDecimal getBalance(Long id) {
        Account account = findById(id);
        if (!account.isActive()) throw new AccountInactiveException(id);
        return account.getBalance();
    }

    public List<TransactionResponse> getTransactions(Long id) {
        findById(id); // vérifie que le compte existe
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(id)
                .stream()
                .map(TransactionResponse::from)
                .toList();
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
            tx.setBalanceAfter(account.getBalance());
        } catch (Exception e) {
            tx.setStatus(TransactionStatus.FAILED);
        }

        transactionRepository.save(tx);
        return TransactionResponse.from(tx);
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
            tx.setBalanceAfter(account.getBalance());
            transactionRepository.save(tx);
            throw new InsufficientBalanceException();
        }

        try {
            account.setBalance(account.getBalance().subtract(request.amount()));
            accountRepository.save(account);
            tx.setStatus(TransactionStatus.SUCCESS);
            tx.setBalanceAfter(account.getBalance());
        } catch (Exception e) {
            tx.setStatus(TransactionStatus.FAILED);
        }

        transactionRepository.save(tx);
        return TransactionResponse.from(tx);
    }

    @Transactional
    public List<TransactionResponse> transfer(Long sourceId, TransferRequest request) {
        if (request.targetAccountId() == null)
            throw new IllegalArgumentException("Le compte destinataire est obligatoire");
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Le montant doit être positif");
        if (sourceId.equals(request.targetAccountId()))
            throw new IllegalArgumentException("Le compte source et destinataire doivent être différents");

        Account source = findById(sourceId);
        Account target = findById(request.targetAccountId());

        if (!source.isActive()) throw new AccountInactiveException(sourceId);
        if (!target.isActive()) throw new AccountInactiveException(request.targetAccountId());

        if (source.getBalance().compareTo(request.amount()) < 0)
            throw new InsufficientBalanceException();

        source.setBalance(source.getBalance().subtract(request.amount()));
        target.setBalance(target.getBalance().add(request.amount()));
        accountRepository.save(source);
        accountRepository.save(target);

        Transaction txDebit = new Transaction();
        txDebit.setAccountId(sourceId);
        txDebit.setType(TransactionType.DEBIT);
        txDebit.setAmount(request.amount());
        txDebit.setStatus(TransactionStatus.SUCCESS);
        txDebit.setBalanceAfter(source.getBalance());
        txDebit.setLinkedAccountId(request.targetAccountId());

        Transaction txCredit = new Transaction();
        txCredit.setAccountId(request.targetAccountId());
        txCredit.setType(TransactionType.CREDIT);
        txCredit.setAmount(request.amount());
        txCredit.setStatus(TransactionStatus.SUCCESS);
        txCredit.setBalanceAfter(target.getBalance());
        txCredit.setLinkedAccountId(sourceId);

        transactionRepository.save(txDebit);
        transactionRepository.save(txCredit);

        return List.of(TransactionResponse.from(txDebit), TransactionResponse.from(txCredit));
    }

    private Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }
}
