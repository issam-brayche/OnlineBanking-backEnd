package com.uf.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.uf.domain.SavingsTransaction;

public interface SavingsTransactionDao extends CrudRepository<SavingsTransaction, Long> {

    List<SavingsTransaction> findAll();
}

