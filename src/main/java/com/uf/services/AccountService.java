package com.uf.services;

import java.security.Principal;

import com.uf.domain.PrimaryAccount;
import com.uf.domain.SavingsAccount;

public interface AccountService {
	PrimaryAccount createPrimaryAccount();
    SavingsAccount createSavingsAccount();
    void deposit(String accountType, double amount, Principal principal);
    boolean withdraw(String accountType, double amount, Principal principal);
    
    
}
