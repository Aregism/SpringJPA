package com.test.service;

import com.test.model.Authority;
import com.test.model.User;
import com.test.util.exceptions.*;

import java.security.Principal;
import java.util.List;

public interface UserService {
    List<User> getAll();

    User getByEmail(String email);

    User getById(int id);

    User getByCode(String code);

    User deleteById(int id);

    List<User> getByAuthorities(Authority authority);

    void save(User user) throws EmailAlreadyInUseException;

    void saveAdmin(User user) throws EmailAlreadyInUseException, AdminExistsException;

    void verify(Principal principal, String code) throws UserNotFoundException, CodeExpiredException, TokenException;

    void delete(Principal principal, String password) throws UserNotFoundException, WrongPasswordException;

    void reset(String token, String newPassword1, String newPassword2) throws UserNotFoundException, WrongPasswordException, CodeExpiredException;

    void getToken(String email, String password) throws UserNotFoundException, WrongPasswordException;
}
