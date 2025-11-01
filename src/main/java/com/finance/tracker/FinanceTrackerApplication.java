
package com.finance.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinanceTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceTrackerApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("Personal Finance Tracker is running!");
        System.out.println("Open: http://localhost:8080");
        System.out.println("========================================\n");
    }
}