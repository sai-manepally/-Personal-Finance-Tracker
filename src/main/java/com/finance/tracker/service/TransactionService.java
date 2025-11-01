package com.finance.tracker.service;

import com.finance.tracker.model.Transaction;
import com.finance.tracker.model.TransactionType;
import com.finance.tracker.model.User;
import com.finance.tracker.repository.TransactionRepository;
import com.finance.tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Transaction addTransaction(Long userId, String description, double amount,
                                      TransactionType type, String category, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = new Transaction(description, amount, type, category, date, user);

        // Update user balance
        if (type == TransactionType.INCOME) {
            user.addToBalance(amount);
        } else {
            user.deductFromBalance(amount);
        }

        userRepository.save(user);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public List<Transaction> getTransactionsByType(Long userId, TransactionType type) {
        return transactionRepository.findByUserIdAndType(userId, type);
    }

    public List<Transaction> getTransactionsByCategory(Long userId, String category) {
        return transactionRepository.findByUserIdAndCategory(userId, category);
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public double getTotalIncome(Long userId) {
        Double total = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME);
        return total != null ? total : 0.0;
    }

    public double getTotalExpenses(Long userId) {
        Double total = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);
        return total != null ? total : 0.0;
    }

    public double getNetBalance(Long userId) {
        return getTotalIncome(userId) - getTotalExpenses(userId);
    }

    public List<String> getCategories(Long userId) {
        return transactionRepository.findDistinctCategoriesByUserId(userId);
    }

    @Transactional
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user balance
        if (transaction.getType() == TransactionType.INCOME) {
            user.deductFromBalance(transaction.getAmount());
        } else {
            user.addToBalance(transaction.getAmount());
        }

        userRepository.save(user);
        transactionRepository.deleteById(transactionId);
    }
}