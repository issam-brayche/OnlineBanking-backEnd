package com.uf.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.uf.domain.PrimaryAccount;
import com.uf.domain.PrimaryTransaction;
import com.uf.domain.SavingsAccount;
import com.uf.domain.SavingsTransaction;
import com.uf.domain.Users;
import com.uf.services.AccountService;
import com.uf.services.TransactionService;
import com.uf.services.UserService;

@Controller
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransactionService transactionsService;
	
	@RequestMapping("/primaryAccount")
	public String primaryAccount(Model model, Principal principal) {
		List<PrimaryTransaction> primaryTransactions = transactionsService.findPrimaryTransactionList(principal.getName());
		Users user = userService.findByUsername(principal.getName());
		PrimaryAccount pimaryAccount = user.getPrimaryAccount();
		model.addAttribute("primaryAccount", pimaryAccount);
		model.addAttribute("primaryTransactionList", primaryTransactions);
		return "primaryAccount";
	}

	@RequestMapping("/savingsAccount")
	public String savingAccount(Model model, Principal principal) {
		List<SavingsTransaction> savingsTransactions = transactionsService.findSavingsTransactionList(principal.getName());
		Users user = userService.findByUsername(principal.getName());
		SavingsAccount savingsAccount = user.getSavingsAccount();
		model.addAttribute("savingsAccount", savingsAccount);
		model.addAttribute("savingsTransactionList", savingsTransactions);
		return "savingsAccount";
	}
	
	@RequestMapping(value = "/deposit", method = RequestMethod.GET)
    public String deposit(Model model) {
        model.addAttribute("accountType", "");
        model.addAttribute("amount", "");

        return "deposit";
    }

    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
    public String depositPOST(@ModelAttribute("amount") String amount, @ModelAttribute("accountType") String accountType, Principal principal) {
        accountService.deposit(accountType, Double.parseDouble(amount), principal);

        return "redirect:/userFront";
    }
    
    @RequestMapping(value = "/withdraw", method = RequestMethod.GET)
    public String withdraw(Model model) {
        model.addAttribute("accountType", "");
        model.addAttribute("amount", "");

        return "withdraw";
    }

    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    public String withdrawPOST(@ModelAttribute("amount") String amount, @ModelAttribute("accountType") String accountType, Principal principal, Model model) {
    	if(accountType != null && (accountType.equalsIgnoreCase("Primary") || accountType.equalsIgnoreCase("Savings")) ) { 
    		boolean isDone = accountService.withdraw(accountType, Double.parseDouble(amount), principal);
            if(!isDone) {
            	model.addAttribute("balanceNS", true);
            	return "withdraw"; 
            }else {
            	return "redirect:/userFront";
            }
    	}else {
    		model.addAttribute("accountNotChoosen", true);
    		return "withdraw";
    	}
        
    }

}
