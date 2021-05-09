package com.test.service;

import com.test.model.Authority;
import com.test.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    AuthorityRepository authorityRepository;

    public List<Authority> getAll() {
        return authorityRepository.findAll();
    }

    public Authority getByRole(String role) {
        return authorityRepository.findByRole(role);
    }

    public void save(Authority authority) {
        authorityRepository.save(authority);
    }

    @Override
    public Authority getById(int id) {
        return authorityRepository.findById(id);
    }
}
