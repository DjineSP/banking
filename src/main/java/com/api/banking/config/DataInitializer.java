package com.api.banking.config;

import com.api.banking.entity.Bank;
import com.api.banking.repository.BankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final BankRepository bankRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (bankRepository.count() > 0) return;

        bankRepository.saveAll(List.of(
                bank("BICEC",    "Banque Internationale du Cameroun pour l'Épargne et le Crédit",
                        "0.0050", "250.00", "0.0030", "150.00", "0.0150", "750.00"),

                bank("AFRILAND", "Afriland First Bank",
                        "0.0075", "300.00", "0.0050", "200.00", "0.0200", "1000.00"),

                bank("SCB",      "Société Commerciale de Banque Cameroun",
                        "0.0050", "200.00", "0.0030", "100.00", "0.0150", "800.00"),

                bank("UBA",      "United Bank for Africa Cameroun",
                        "0.0060", "250.00", "0.0040", "150.00", "0.0180", "900.00"),

                bank("ECOBANK",  "Ecobank Cameroun",
                        "0.0075", "350.00", "0.0050", "200.00", "0.0200", "1000.00")
        ));
    }

    private Bank bank(String code, String name,
                      String debitRate, String debitFlat,
                      String intraRate, String intraFlat,
                      String interRate, String interFlat) {
        Bank b = new Bank();
        b.setCode(code);
        b.setName(name);
        b.setDebitFeeRate(new BigDecimal(debitRate));
        b.setDebitFeeFlat(new BigDecimal(debitFlat));
        b.setIntraFeeRate(new BigDecimal(intraRate));
        b.setIntraFeeFlat(new BigDecimal(intraFlat));
        b.setInterFeeRate(new BigDecimal(interRate));
        b.setInterFeeFlat(new BigDecimal(interFlat));
        return b;
    }
}
