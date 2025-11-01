package com.finance.tracker.controller;

import com.finance.tracker.model.Transaction;
import com.finance.tracker.model.TransactionType;
import com.finance.tracker.model.User;
import com.finance.tracker.service.TransactionService;
import com.finance.tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public String dashboard(@RequestParam Long userId, Model model) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions = transactionService.getAllTransactions(userId);
        double totalIncome = transactionService.getTotalIncome(userId);
        double totalExpenses = transactionService.getTotalExpenses(userId);
        double netBalance = transactionService.getNetBalance(userId);

        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpenses", totalExpenses);
        model.addAttribute("netBalance", netBalance);

        return "dashboard";
    }

    @GetMapping("/add-transaction")
    public String addTransactionPage(@RequestParam Long userId, Model model) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "add-transaction";
    }

    @PostMapping("/add-transaction")
    public String addTransaction(@RequestParam Long userId,
                                 @RequestParam String description,
                                 @RequestParam double amount,
                                 @RequestParam TransactionType type,
                                 @RequestParam String category,
                                 @RequestParam String date) {
        LocalDate transactionDate = LocalDate.parse(date);
        transactionService.addTransaction(userId, description, amount, type, category, transactionDate);
        return "redirect:/dashboard?userId=" + userId;
    }

    @PostMapping("/delete-transaction")
    public String deleteTransaction(@RequestParam Long transactionId,
                                    @RequestParam Long userId) {
        transactionService.deleteTransaction(transactionId, userId);
        return "redirect:/dashboard?userId=" + userId;
    }
}