package com.test.service;

import com.test.model.Authority;

import java.util.List;

public interface AuthorityService {
    List<Authority> getAll();

    Authority getByRole(String role);

    void save(Authority authority);

    Authority getById(int id);
}
