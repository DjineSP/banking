package com.api.banking.service;

import com.api.banking.dto.request.BankRequest;
import com.api.banking.dto.response.BankResponse;
import com.api.banking.entity.Bank;
import com.api.banking.exception.BankAlreadyExistsException;
import com.api.banking.exception.BankNotFoundException;
import com.api.banking.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;

    @Transactional
    public BankResponse registerBank(BankRequest request) {
        if (request.code() == null || request.code().isBlank())
            throw new IllegalArgumentException("Le code de la banque est obligatoire");
        if (request.name() == null || request.name().isBlank())
            throw new IllegalArgumentException("Le nom de la banque est obligatoire");
        if (request.code().length() > 20)
            throw new IllegalArgumentException("Le code ne doit pas dépasser 20 caractères");
        if (!request.code().matches("[A-Za-z0-9_-]+"))
            throw new IllegalArgumentException("Le code ne doit contenir que des lettres, chiffres, tirets ou underscores");

        validateFees(nvl(request.debitFeeRate()), nvl(request.debitFeeFlat()), "débit");
        validateFees(nvl(request.intraFeeRate()), nvl(request.intraFeeFlat()), "intrabank");
        validateFees(nvl(request.interFeeRate()), nvl(request.interFeeFlat()), "interbank");

        String code = request.code().toUpperCase();
        if (bankRepository.existsByCode(code))
            throw new BankAlreadyExistsException(code);

        Bank bank = new Bank();
        bank.setCode(code);
        bank.setName(request.name().trim());
        bank.setDebitFeeRate(nvl(request.debitFeeRate()));
        bank.setDebitFeeFlat(nvl(request.debitFeeFlat()));
        bank.setIntraFeeRate(nvl(request.intraFeeRate()));
        bank.setIntraFeeFlat(nvl(request.intraFeeFlat()));
        bank.setInterFeeRate(nvl(request.interFeeRate()));
        bank.setInterFeeFlat(nvl(request.interFeeFlat()));

        return BankResponse.from(bankRepository.save(bank));
    }

    @Transactional(readOnly = true)
    public List<BankResponse> listBanks() {
        return bankRepository.findAll().stream().map(BankResponse::from).toList();
    }

    @Transactional
    public BankResponse deactivateBank(Long id) {
        Bank bank = findById(id);
        if (!bank.isActive())
            throw new IllegalArgumentException("La banque est déjà désactivée");
        bank.setActive(false);
        return BankResponse.from(bankRepository.save(bank));
    }

    @Transactional(readOnly = true)
    public Bank findById(Long id) {
        return bankRepository.findById(id).orElseThrow(() -> new BankNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Bank findByCode(String code) {
        return bankRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BankNotFoundException(code));
    }

    private void validateFees(BigDecimal rate, BigDecimal flat, String label) {
        if (rate.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Le taux de frais " + label + " ne peut pas être négatif");
        if (rate.compareTo(new BigDecimal("1.00")) > 0)
            throw new IllegalArgumentException("Le taux de frais " + label + " ne peut pas dépasser 100%");
        if (flat.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Les frais fixes " + label + " ne peuvent pas être négatifs");
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
