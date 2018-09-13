package com.uf.controller;

import java.security.Principal;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.uf.domain.PrimaryAccount;
import com.uf.domain.Recipient;
import com.uf.domain.SavingsAccount;
import com.uf.domain.Users;
import com.uf.services.TransactionService;
import com.uf.services.UserService;

@Controller
@RequestMapping("/transfer")
public class TransfertController {

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/betweenAccounts", method = RequestMethod.GET)
	public String betweenAccounts(Model model) {
		model.addAttribute("transferFrom", "");
		model.addAttribute("transferTo", "");
		model.addAttribute("amount", "");

		return "betweenAccounts";
	}

	@RequestMapping(value = "/betweenAccounts", method = RequestMethod.POST)
	public String betweenAccountsPost(@ModelAttribute("transferFrom") String transferFrom, @ModelAttribute("transferTo") String transferTo, @ModelAttribute("amount") String amount, Principal principal, Model model) throws Exception {
		Users user = userService.findByUsername(principal.getName());
		PrimaryAccount primaryAccount = user.getPrimaryAccount();
		SavingsAccount savingsAccount = user.getSavingsAccount();
		boolean isDone = transactionService.betweenAccountsTransfer(transferFrom, transferTo, amount, primaryAccount, savingsAccount);
		if (!isDone) {
			model.addAttribute("balanceNS", true);
			return "betweenAccounts";
		} else {
			return "redirect:/userFront";
		}
	}

	@RequestMapping(value = "/recipient", method = RequestMethod.GET)
	public String recipient(Model model, Principal principal) {
		Recipient recipient = new Recipient();
		findRecipientAndFillModel(model, principal, recipient);

		return "recipient";
	}

	@RequestMapping(value = "/recipient/save", method = RequestMethod.POST)
	public String recipientPost(@ModelAttribute("recipient") Recipient recipient, Principal principal) {

		Users user = userService.findByUsername(principal.getName());
		recipient.setUser(user);
		transactionService.saveRecipient(recipient);

		return "redirect:/transfer/recipient";
	}

	@RequestMapping(value = "/recipient/edit", method = RequestMethod.GET)
	public String recipientEdit(@RequestParam(value = "recipientName") String recipientName, Model model, Principal principal) {
		Recipient recipient = transactionService.findRecipientByName(recipientName);
		findRecipientAndFillModel(model, principal, recipient);
		return "recipient";
	}

	@RequestMapping(value = "/recipient/delete", method = RequestMethod.GET)
	@Transactional
	public String recipientDelete(@RequestParam(value = "recipientName") String recipientName, Model model, Principal principal) {
		transactionService.deleteRecipientByName(recipientName);
		Recipient recipient = new Recipient();
		findRecipientAndFillModel(model, principal, recipient);
		return "recipient";
	}

	private void findRecipientAndFillModel(Model model, Principal principal, Recipient recipient) {
		List<Recipient> recipientList = transactionService.findRecipientList(principal);
		model.addAttribute("recipient", recipient);
		model.addAttribute("recipientList", recipientList);
	}

	@RequestMapping(value = "/toSomeoneElse", method = RequestMethod.GET)
	public String toSomeoneElse(Model model, Principal principal) {
		List<Recipient> recipientList = transactionService.findRecipientList(principal);

		model.addAttribute("recipientList", recipientList);
		model.addAttribute("accountType", "");

		return "toSomeoneElse";
	}

	@RequestMapping(value = "/toSomeoneElse", method = RequestMethod.POST)
	public String toSomeoneElsePost(@ModelAttribute("recipientName") String recipientName, @ModelAttribute("accountType") String accountType, @ModelAttribute("amount") String amount, Principal principal, Model model) {
		if (recipientName == null || recipientName.equalsIgnoreCase("-- select the recipient --")) {
			model.addAttribute("recipientNotChoosen", true);
			return toSomeoneElse(model, principal);
		} else if (accountType != null && (accountType.equalsIgnoreCase("Primary") || accountType.equalsIgnoreCase("Savings"))) {
			Users user = userService.findByUsername(principal.getName());
			Recipient recipient = transactionService.findRecipientByName(recipientName);
			boolean isDone = transactionService.toSomeoneElseTransfer(recipient, accountType, amount, user.getPrimaryAccount(), user.getSavingsAccount());
			if (!isDone) {
				model.addAttribute("balanceNS", true);
				return toSomeoneElse(model, principal);
			} else {
				return "redirect:/userFront";
			}
		} else {
			model.addAttribute("accountNotChoosen", true);
			return toSomeoneElse(model, principal);
		}

	}

}
