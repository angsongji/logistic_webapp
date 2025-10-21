package com.uteexpress.repository;

import com.uteexpress.entity.Debt;
import com.uteexpress.entity.DebtStatus;
import com.uteexpress.entity.DebtType;
import com.uteexpress.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByDebtorOrderByCreatedAtDesc(User debtor);
    List<Debt> findByCreditorOrderByCreatedAtDesc(User creditor);
    List<Debt> findByDebtTypeAndStatus(DebtType debtType, DebtStatus status);
    
    @Query("SELECT SUM(d.remainingAmount) FROM Debt d WHERE d.debtor = :debtor AND d.status != 'PAID'")
    Double getTotalDebtByDebtor(User debtor);
    
    @Query("SELECT SUM(d.remainingAmount) FROM Debt d WHERE d.creditor = :creditor AND d.status != 'PAID'")
    Double getTotalReceivableByCreditor(User creditor);
}
