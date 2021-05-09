package com.test.repository;

import com.test.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    @Override
    List<Authority> findAll();

    Authority findByRole(String role);

    Authority findById(int id);

}
