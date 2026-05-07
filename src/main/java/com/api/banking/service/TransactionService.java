package com.api.banking.service;

import com.api.banking.dto.request.InterBankTransferRequest;
import com.api.banking.dto.request.TransactionRequest;
import com.api.banking.dto.request.TransferRequest;
import com.api.banking.dto.response.TransactionResponse;
import com.api.banking.entity.Account;
import com.api.banking.entity.Bank;
import com.api.banking.entity.Transaction;
import com.api.banking.enums.AccountStatus;
import com.api.banking.enums.FeeType;
import com.api.banking.enums.TransactionStatus;
import com.api.banking.enums.TransactionType;
import com.api.banking.exception.AccountInactiveException;
import com.api.banking.exception.AccountNotFoundException;
import com.api.banking.exception.BankInactiveException;
import com.api.banking.exception.InsufficientBalanceException;
import com.api.banking.repository.AccountRepository;
import com.api.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(account.getId()).stream()
                .sorted(Comparator.comparing(Transaction::getType)
                        .thenComparing(Transaction::getCreatedAt, Comparator.reverseOrder()))
                .map(TransactionResponse::from)
                .toList();
    }

    @Transactional
    public TransactionResponse deposit(String accountNumber, TransactionRequest request) {
        validateAmount(request.amount());
        Account account = requireActive(accountNumber);

        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);

        Transaction tx = buildTx(account, TransactionType.CREDIT, request.amount(),
                BigDecimal.ZERO, FeeType.NONE, account.getBalance());
        tx.setStatus(TransactionStatus.SUCCESS);
        tx.setSourceBankId(account.getBank().getId());
        transactionRepository.save(tx);
        return TransactionResponse.from(tx);
    }

    @Transactional
    public TransactionResponse withdrawal(String accountNumber, TransactionRequest request) {
        validateAmount(request.amount());
        Account account = requireActive(accountNumber);
        Bank bank = account.getBank();

        BigDecimal fee = computeFee(request.amount(), bank.getDebitFeeRate(), bank.getDebitFeeFlat());
        BigDecimal total = request.amount().add(fee);

        if (account.getBalance().compareTo(total) < 0)
            throw new InsufficientBalanceException();

        account.setBalance(account.getBalance().subtract(total));
        accountRepository.save(account);

        Transaction tx = buildTx(account, TransactionType.DEBIT, request.amount(),
                fee, FeeType.DEBIT_FEE, account.getBalance());
        tx.setStatus(TransactionStatus.SUCCESS);
        tx.setSourceBankId(bank.getId());
        transactionRepository.save(tx);
        return TransactionResponse.from(tx);
    }

    @Transactional
    public List<TransactionResponse> transferIntra(String sourceAccountNumber, TransferRequest request) {
        if (request.targetAccountNumber() == null || request.targetAccountNumber().isBlank())
            throw new IllegalArgumentException("Le numéro de compte destinataire est obligatoire");
        validateAmount(request.amount());

        Account source = requireActive(sourceAccountNumber);
        Account target = accountRepository.findByAccountNumber(request.targetAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(request.targetAccountNumber()));

        if (source.getAccountNumber().equals(target.getAccountNumber()))
            throw new IllegalArgumentException("Le compte source et destinataire doivent être différents");
        if (target.getStatus() != AccountStatus.ACTIVE)
            throw new AccountInactiveException(target.getId(), target.getStatus());
        if (!source.getBank().getId().equals(target.getBank().getId()))
            throw new IllegalArgumentException(
                    "Les deux comptes doivent appartenir à la même banque pour un virement intrabank");

        Bank bank = source.getBank();
        BigDecimal fee = computeFee(request.amount(), bank.getIntraFeeRate(), bank.getIntraFeeFlat());
        BigDecimal total = request.amount().add(fee);

        if (source.getBalance().compareTo(total) < 0)
            throw new InsufficientBalanceException();

        source.setBalance(source.getBalance().subtract(total));
        target.setBalance(target.getBalance().add(request.amount()));
        accountRepository.save(source);
        accountRepository.save(target);

        Transaction txDebit = buildTx(source, TransactionType.DEBIT, request.amount(),
                fee, FeeType.INTRA_FEE, source.getBalance());
        txDebit.setStatus(TransactionStatus.SUCCESS);
        txDebit.setLinkedAccountNumber(target.getAccountNumber());
        txDebit.setSourceBankId(bank.getId());
        txDebit.setTargetBankId(bank.getId());

        Transaction txCredit = buildTx(target, TransactionType.CREDIT, request.amount(),
                BigDecimal.ZERO, FeeType.NONE, target.getBalance());
        txCredit.setStatus(TransactionStatus.SUCCESS);
        txCredit.setLinkedAccountNumber(source.getAccountNumber());
        txCredit.setSourceBankId(bank.getId());
        txCredit.setTargetBankId(bank.getId());

        transactionRepository.save(txDebit);
        transactionRepository.save(txCredit);
        return List.of(TransactionResponse.from(txDebit), TransactionResponse.from(txCredit));
    }

    @Transactional
    public List<TransactionResponse> transferInter(String sourceAccountNumber, InterBankTransferRequest request) {
        if (request.targetBankCode() == null || request.targetBankCode().isBlank())
            throw new IllegalArgumentException("Le code de la banque cible est obligatoire");
        if (request.targetAccountNumber() == null || request.targetAccountNumber().isBlank())
            throw new IllegalArgumentException("Le numéro de compte destinataire est obligatoire");
        validateAmount(request.amount());

        Account source = requireActive(sourceAccountNumber);
        Bank sourceBank = source.getBank();

        Account target = accountRepository.findByAccountNumber(request.targetAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(request.targetAccountNumber()));

        if (!target.getBank().getCode().equalsIgnoreCase(request.targetBankCode()))
            throw new IllegalArgumentException(
                    "Le compte " + request.targetAccountNumber() + " n'appartient pas à la banque " + request.targetBankCode());
        if (sourceBank.getId().equals(target.getBank().getId()))
            throw new IllegalArgumentException(
                    "Les deux comptes appartiennent à la même banque. Utilisez le virement intrabank.");
        if (!target.getBank().isActive())
            throw new BankInactiveException(target.getBank().getCode());
        if (target.getStatus() != AccountStatus.ACTIVE)
            throw new AccountInactiveException(target.getId(), target.getStatus());

        BigDecimal fee = computeFee(request.amount(), sourceBank.getInterFeeRate(), sourceBank.getInterFeeFlat());
        BigDecimal total = request.amount().add(fee);

        if (source.getBalance().compareTo(total) < 0)
            throw new InsufficientBalanceException();

        source.setBalance(source.getBalance().subtract(total));
        target.setBalance(target.getBalance().add(request.amount()));
        accountRepository.save(source);
        accountRepository.save(target);

        Transaction txDebit = buildTx(source, TransactionType.DEBIT, request.amount(),
                fee, FeeType.INTER_FEE, source.getBalance());
        txDebit.setStatus(TransactionStatus.SUCCESS);
        txDebit.setLinkedAccountNumber(target.getAccountNumber());
        txDebit.setSourceBankId(sourceBank.getId());
        txDebit.setTargetBankId(target.getBank().getId());

        Transaction txCredit = buildTx(target, TransactionType.CREDIT, request.amount(),
                BigDecimal.ZERO, FeeType.NONE, target.getBalance());
        txCredit.setStatus(TransactionStatus.SUCCESS);
        txCredit.setLinkedAccountNumber(source.getAccountNumber());
        txCredit.setSourceBankId(sourceBank.getId());
        txCredit.setTargetBankId(target.getBank().getId());

        transactionRepository.save(txDebit);
        transactionRepository.save(txCredit);
        return List.of(TransactionResponse.from(txDebit), TransactionResponse.from(txCredit));
    }

    private Account requireActive(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        if (account.getStatus() != AccountStatus.ACTIVE)
            throw new AccountInactiveException(account.getId(), account.getStatus());
        if (!account.getBank().isActive())
            throw new BankInactiveException(account.getBank().getCode());
        return account;
    }

    private BigDecimal computeFee(BigDecimal amount, BigDecimal rate, BigDecimal flat) {
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP).add(flat);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Le montant doit être positif");
    }

    private Transaction buildTx(Account account, TransactionType type, BigDecimal amount,
                                 BigDecimal fee, FeeType feeType, BigDecimal balanceAfter) {
        Transaction tx = new Transaction();
        tx.setAccountId(account.getId());
        tx.setType(type);
        tx.setAmount(amount);
        tx.setFee(fee);
        tx.setFeeType(feeType);
        tx.setBalanceAfter(balanceAfter);
        return tx;
    }
}
