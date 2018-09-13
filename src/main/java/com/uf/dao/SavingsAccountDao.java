package com.uf.dao;

import org.springframework.data.repository.CrudRepository;

import com.uf.domain.SavingsAccount;

/**
 * Created by z00382545 on 10/21/16.
 */
public interface SavingsAccountDao extends CrudRepository<SavingsAccount, Long> {

    SavingsAccount findByAccountNumber (int accountNumber);
}
