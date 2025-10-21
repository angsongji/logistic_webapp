package com.uteexpress.service.impl;

import com.uteexpress.dto.accountant.DebtDto;
import com.uteexpress.entity.Debt;
import com.uteexpress.entity.DebtStatus;
import com.uteexpress.entity.DebtType;
import com.uteexpress.entity.User;
import com.uteexpress.repository.DebtRepository;
import com.uteexpress.service.accountant.DebtService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;

    public DebtServiceImpl(DebtRepository debtRepository) {
        this.debtRepository = debtRepository;
    }

    @Override
    public Debt createDebt(DebtDto debtDto) {
        // Implementation will be added later
        return null;
    }

    @Override
    public List<Debt> getAllDebts() {
        return debtRepository.findAll();
    }

    @Override
    public List<Debt> getDebtsByDebtor(User debtor) {
        return debtRepository.findByDebtorOrderByCreatedAtDesc(debtor);
    }

    @Override
    public List<Debt> getDebtsByCreditor(User creditor) {
        return debtRepository.findByCreditorOrderByCreatedAtDesc(creditor);
    }

    @Override
    public List<Debt> getDebtsByTypeAndStatus(DebtType debtType, DebtStatus status) {
        return debtRepository.findByDebtTypeAndStatus(debtType, status);
    }

    @Override
    public Debt updateDebtStatus(Long debtId, DebtStatus status) {
        Debt debt = debtRepository.findById(debtId).orElse(null);
        if (debt != null) {
            debt.setStatus(status);
            return debtRepository.save(debt);
        }
        return null;
    }

    @Override
    public Debt makePayment(Long debtId, BigDecimal amount) {
        Debt debt = debtRepository.findById(debtId).orElse(null);
        if (debt != null) {
            BigDecimal currentPaid = debt.getPaidAmount() != null ? debt.getPaidAmount() : BigDecimal.ZERO;
            debt.setPaidAmount(currentPaid.add(amount));
            if (debt.getPaidAmount().compareTo(debt.getAmount()) >= 0) {
                debt.setStatus(DebtStatus.PAID);
            } else {
                debt.setStatus(DebtStatus.PARTIAL);
            }
            return debtRepository.save(debt);
        }
        return null;
    }

    @Override
    public BigDecimal getTotalDebtByDebtor(User debtor) {
        Double total = debtRepository.getTotalDebtByDebtor(debtor);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalReceivableByCreditor(User creditor) {
        Double total = debtRepository.getTotalReceivableByCreditor(creditor);
        return total != null ? BigDecimal.valueOf(total) : BigDecimal.ZERO;
    }
}
