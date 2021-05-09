package com.test.repository;

import com.test.model.Authority;
import com.test.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Override
    List<User> findAll();

    User getByEmail(String email);

    User getById(int id);

    User deleteById(int id);

    List<User> getByAuthorities(Authority authority);

    User getByVerificationCode(String code);

    User getByPasswordResetToken(String token);
}
