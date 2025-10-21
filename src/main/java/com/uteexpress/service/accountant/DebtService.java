package com.uteexpress.service.accountant;

import com.uteexpress.dto.accountant.DebtDto;
import com.uteexpress.entity.Debt;
import com.uteexpress.entity.User;

import java.math.BigDecimal;
import java.util.List;

public interface DebtService {
    Debt createDebt(DebtDto debtDto);
    List<Debt> getAllDebts();
    List<Debt> getDebtsByDebtor(User debtor);
    List<Debt> getDebtsByCreditor(User creditor);
    List<Debt> getDebtsByTypeAndStatus(Debt.DebtType debtType, Debt.DebtStatus status);
    Debt updateDebtStatus(Long debtId, Debt.DebtStatus status);
    Debt makePayment(Long debtId, BigDecimal amount);
    BigDecimal getTotalDebtByDebtor(User debtor);
    BigDecimal getTotalReceivableByCreditor(User creditor);
}
