package com.transactiq.repository;

import com.transactiq.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByDateDesc(String userId);

    List<Transaction> findByUserIdAndCategory(String userId, String category);

    List<Transaction> findByUserIdAndDateBetween(String userId, LocalDate start, LocalDate end);

    @Query("SELECT t.category, SUM(t.amount) FROM Transaction t WHERE t.userId = :userId GROUP BY t.category ORDER BY SUM(t.amount) DESC")
    List<Object[]> getCategorySpendSummary(@Param("userId") String userId);

    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND LOWER(t.merchant) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Transaction> searchByMerchant(@Param("userId") String userId, @Param("keyword") String keyword);
}
