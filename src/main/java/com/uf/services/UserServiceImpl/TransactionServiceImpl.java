package com.uf.services.UserServiceImpl;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uf.dao.PrimaryAccountDao;
import com.uf.dao.PrimaryTransactionDao;
import com.uf.dao.RecipientDao;
import com.uf.dao.SavingsAccountDao;
import com.uf.dao.SavingsTransactionDao;
import com.uf.domain.PrimaryAccount;
import com.uf.domain.PrimaryTransaction;
import com.uf.domain.Recipient;
import com.uf.domain.SavingsAccount;
import com.uf.domain.SavingsTransaction;
import com.uf.domain.Users;
import com.uf.services.TransactionService;
import com.uf.services.UserService;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private UserService userService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private PrimaryTransactionDao primaryTransactionDao;

	@Autowired
	private SavingsTransactionDao savingsTransactionDao;

	@Autowired
	private PrimaryAccountDao primaryAccountDao;

	@Autowired
	private SavingsAccountDao savingsAccountDao;

	@Autowired
	private RecipientDao recipientDao;

	public List<PrimaryTransaction> findPrimaryTransactionList(String username) {
		Users user = userService.findByUsername(username);
		List<PrimaryTransaction> primaryTransactionList = user.getPrimaryAccount().getPrimaryTransactionList();

		return primaryTransactionList;
	}

	public List<SavingsTransaction> findSavingsTransactionList(String username) {
		Users user = userService.findByUsername(username);
		List<SavingsTransaction> savingsTransactionList = user.getSavingsAccount().getSavingsTransactionList();

		return savingsTransactionList;
	}

	public void savePrimaryDepositTransaction(PrimaryTransaction primaryTransaction) {
		primaryTransactionDao.save(primaryTransaction);
	}

	public void saveSavingsDepositTransaction(SavingsTransaction savingsTransaction) {
		savingsTransactionDao.save(savingsTransaction);
	}

	public void savePrimaryWithdrawTransaction(PrimaryTransaction primaryTransaction) {
		primaryTransactionDao.save(primaryTransaction);
	}

	public void saveSavingsWithdrawTransaction(SavingsTransaction savingsTransaction) {
		savingsTransactionDao.save(savingsTransaction);
	}

	public boolean betweenAccountsTransfer(String transferFrom, String transferTo, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount) throws Exception {
		boolean isDone = withdrawFromAccount(transferFrom, transferTo, amount, primaryAccount, savingsAccount, "Between account transfer from ", "Transfer");
		if (isDone) {
			depositToAccount(transferFrom, transferTo, amount, primaryAccount, savingsAccount, "Between account transfer from ", "Transfer");
		}

		return isDone;
	}

	/********************************
	 * function added by ISSAM
	 ******************************************/

	public void depositToAccount(String transferFrom, String transferTo, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount, String description, String accountType) {

		if (transferTo.equalsIgnoreCase("Primary")) {
			primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().add(new BigDecimal(amount)));
			primaryAccountDao.save(primaryAccount);

			Date date = new Date();
			PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, description + transferFrom + " to " + transferTo, accountType, "Finished", Double.parseDouble(amount), primaryAccount.getAccountBalance(), primaryAccount);
			transactionService.savePrimaryDepositTransaction(primaryTransaction);

		} else if (transferTo.equalsIgnoreCase("Savings")) {
			savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().add(new BigDecimal(amount)));
			savingsAccountDao.save(savingsAccount);

			Date date = new Date();
			SavingsTransaction savingsTransaction = new SavingsTransaction(date, description + transferFrom + " to " + transferTo, accountType, "Finished", Double.parseDouble(amount), savingsAccount.getAccountBalance(), savingsAccount);
			transactionService.saveSavingsDepositTransaction(savingsTransaction);
		}
	}

	public boolean withdrawFromAccount(String transferFrom, String transferTo, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount, String description, String accountType) {
		boolean isDone = false;
		if (transferFrom.equalsIgnoreCase("Primary")) {
			if (primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)).intValue() > 0) {
				isDone = true;
				primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
				primaryAccountDao.save(primaryAccount);

				Date date = new Date();

				PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, description + transferFrom + " to " + transferTo, accountType, "Finished", Double.parseDouble(amount), primaryAccount.getAccountBalance(), primaryAccount);
				transactionService.savePrimaryWithdrawTransaction(primaryTransaction);
			}

		} else if (transferFrom.equalsIgnoreCase("Savings")) {
			if (savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)).intValue() > 0) {
				isDone = true;
				savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
				savingsAccountDao.save(savingsAccount);

				Date date = new Date();
				SavingsTransaction savingsTransaction = new SavingsTransaction(date, description + transferFrom + " to " + transferTo, accountType, "Finished", Double.parseDouble(amount), savingsAccount.getAccountBalance(), savingsAccount);
				transactionService.saveSavingsWithdrawTransaction(savingsTransaction);
			}

		}
		return isDone;
	}

	/*******************************
	 * * function added by ISSAM
	 ******************************************/

	public List<Recipient> findRecipientList(Principal principal) {
		String username = principal.getName();
		List<Recipient> recipientList = recipientDao.findAll().stream() // convert list to stream
				.filter(recipient -> username.equals(recipient.getUser().getUsername())) // filters the line, equals to
																							// username
				.collect(Collectors.toList());

		return recipientList;
	}

	public Recipient saveRecipient(Recipient recipient) {
		return recipientDao.save(recipient);
	}

	public Recipient findRecipientByName(String recipientName) {
		return recipientDao.findByName(recipientName);
	}

	public void deleteRecipientByName(String recipientName) {
		recipientDao.deleteByName(recipientName);
	}

	public boolean toSomeoneElseTransfer(Recipient recipient, String accountType, String amount, PrimaryAccount primaryAccount, SavingsAccount savingsAccount) {
		return withdrawFromAccount(accountType, recipient.getName(), amount, primaryAccount, savingsAccount, "Transfer to recipient from ", "Transfer");
//		if (accountType.equalsIgnoreCase("Primary")) {
//			primaryAccount.setAccountBalance(primaryAccount.getAccountBalance().subtract(new BigDecimal(amount)));
//			primaryAccountDao.save(primaryAccount);
//
//			Date date = new Date();
//
//			PrimaryTransaction primaryTransaction = new PrimaryTransaction(date, "Transfer to recipient " + recipient.getName(), "Transfer", "Finished", Double.parseDouble(amount), primaryAccount.getAccountBalance(), primaryAccount);
//			primaryTransactionDao.save(primaryTransaction);
//		} else if (accountType.equalsIgnoreCase("Savings")) {
//			savingsAccount.setAccountBalance(savingsAccount.getAccountBalance().subtract(new BigDecimal(amount)));
//			savingsAccountDao.save(savingsAccount);
//
//			Date date = new Date();
//
//			SavingsTransaction savingsTransaction = new SavingsTransaction(date,
//					"Transfer to recipient " + recipient.getName(), "Transfer", "Finished", Double.parseDouble(amount),
//					savingsAccount.getAccountBalance(), savingsAccount);
//			savingsTransactionDao.save(savingsTransaction);
//		}

	}
}
