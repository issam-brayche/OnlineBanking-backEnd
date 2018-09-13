package com.uf.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.uf.domain.Users;

public interface UserDao extends CrudRepository<Users, Long> {
	Users findByUsername(String username);
    Users findByEmail(String email);
    Users findByuserId(Long id);
    List<Users> findAll();
}
