package com.uf.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.uf.domain.PrimaryTransaction;

public interface PrimaryTransactionDao extends CrudRepository<PrimaryTransaction, Long> {

    List<PrimaryTransaction> findAll();
}
