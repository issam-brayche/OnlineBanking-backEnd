package com.uf.services.UserServiceImpl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uf.dao.PrimaryAccountDao;
import com.uf.dao.SavingsAccountDao;
import com.uf.domain.PrimaryAccount;
import com.uf.domain.PrimaryTransaction;
import com.uf.domain.SavingsAccount;
import com.uf.domain.SavingsTransaction;
import com.uf.domain.Users;
import com.uf.services.AccountService;
import com.uf.services.TransactionService;
import com.uf.services.UserService;

@Service
public class AccountServiceImpl implements AccountService {

	private static int nextAccountNumber = 11223146;

	@Autowired
	private PrimaryAccountDao primaryAccountDao;

	@Autowired
	private SavingsAccountDao savingsAccountDao;

	@Autowired
	private UserService userService;

	@Autowired
	private TransactionService transactionService;

	public PrimaryAccount createPrimaryAccount() {
		PrimaryAccount primaryAccount = new PrimaryAccount();
		primaryAccount.setAccountBalance(new BigDecimal(0.0));
		primaryAccount.setAccountNumber(accountGen());
		System.out.println(primaryAccount.getAccountNumber());

		primaryAccountDao.save(primaryAccount);

		return primaryAccountDao.findByAccountNumber(primaryAccount.getAccountNumber());
	}

	public SavingsAccount createSavingsAccount() {
		SavingsAccount savingsAccount = new SavingsAccount();
		savingsAccount.setAccountBalance(new BigDecimal(0.0));
		savingsAccount.setAccountNumber(accountGen());

		savingsAccountDao.save(savingsAccount);

		return savingsAccountDao.findByAccountNumber(savingsAccount.getAccountNumber());
	}

	public void deposit(String accountType, double amount, Principal principal) {
		Users user = userService.findByUsername(principal.getName());

		if (accountType.equalsIgnoreCase("Primary")) {
			PrimaryAccount primaryAccount = user.getPrimaryAccount();
			primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
			primaryAccountDao.save(primaryAccount);

			Date date = new Date();
			PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Deposit to Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
			transactionService.savePrimaryDepositTransaction(primaryTransaction);

		} else if (accountType.equalsIgnoreCase("Savings")) {
			SavingsAccount savingsAccount = user.getSavingsAccount();
			savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
			savingsAccountDao.save(savingsAccount);

			Date date = new Date();
			SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Deposit to savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
			transactionService.saveSavingsDepositTransaction(savingsTransaction);
		}
	}

	public boolean withdraw(String accountType, double amount, Principal principal) {
		Users user = userService.findByUsername(principal.getName());
		boolean isDone = false;
		if (accountType.equalsIgnoreCase("Primary")) {
			PrimaryAccount primaryAccount = user.getPrimaryAccount();
			if (primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)).intValue() > 0) {
				isDone = true;
				primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
				primaryAccountDao.save(primaryAccount);

				Date date = new Date();

				PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Withdraw from Primary Account", "Account", "Finished", amount, primaryAccount.getAccountBalance(), primaryAccount);
				transactionService.savePrimaryWithdrawTransaction(primaryTransaction);
			}

		} else if (accountType.equalsIgnoreCase("Savings")) {
			SavingsAccount savingsAccount = user.getSavingsAccount();
			if (savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)).intValue() > 0) {
				isDone = true;
				savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
				savingsAccountDao.save(savingsAccount);

				Date date = new Date();
				SavingsTransaction savingsTransaction = new SavingsTransaction(date, "Withdraw from savings Account", "Account", "Finished", amount, savingsAccount.getAccountBalance(), savingsAccount);
				transactionService.saveSavingsWithdrawTransaction(savingsTransaction);
			}

		}
		return isDone;
	}

	private int accountGen() {
		return ++nextAccountNumber;
	}

}
