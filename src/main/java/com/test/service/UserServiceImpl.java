package com.test.service;

import com.test.model.Authority;
import com.test.model.User;
import com.test.model.enums.Status;
import com.test.repository.AuthorityRepository;
import com.test.repository.UserRepository;
import com.test.util.exceptions.*;
import com.test.util.helpers.CustomMailSender;
import com.test.util.helpers.UserServiceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private UserServiceHelper userServiceHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomMailSender mailSender;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

    @Override
    public User getById(int id) {
        return userRepository.getById(id);
    }

    @Override
    public User getByCode(String code) {
        return userRepository.getByVerificationCode(code);
    }

    @Override
    public User deleteById(int id) {
        return userRepository.deleteById(id);
    }

    @Override
    public List<User> getByAuthorities(Authority authority) {
        return userRepository.getByAuthorities(authority);
    }

    @Transactional
    @Override
    public void save(User user) throws EmailAlreadyInUseException {
        User userByEmail = userRepository.getByEmail(user.getEmail());
        if (userByEmail == null) {
            Set<Authority> authorities = new HashSet<>();
            authorities.add(authorityRepository.findByRole("User"));
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setAuthorities(authorities);
            userServiceHelper.setRegDateAndStatus(user);
            String code = UserServiceHelper.generate();
            user.setVerificationCode(code);
            user.setCodeSentTime(LocalDateTime.now());
            userRepository.save(user);
            mailSender.send(user, "Welcome", "Account successfully created. Your verification code is: \n" +
                    code);
            userRepository.save(user);
        } else {
            throw new EmailAlreadyInUseException("Email already in use.");
        }
    }

    @Transactional
    @Override
    public void saveAdmin(User user) throws EmailAlreadyInUseException, AdminExistsException {
        Authority authorityAdmin = authorityRepository.findByRole("Admin");
        Authority authorityUser = authorityRepository.findByRole("User");
        List<User> admins = userRepository.getByAuthorities(authorityAdmin);
        if (admins.size() == 0) {
            User userByEmail = userRepository.getByEmail(user.getEmail());
            if (userByEmail == null) {
                Set<Authority> authorities = new HashSet<>();
                authorities.add(authorityAdmin);
                authorities.add(authorityUser);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setAuthorities(authorities);
                String code = UserServiceHelper.generate();
                user.setVerificationCode(code);
                user.setCodeSentTime(LocalDateTime.now());
                userServiceHelper.setRegDateAndStatus(user);
                userRepository.save(user);
                mailSender.send(user, "Welcome", "Account successfully created. Your verification code is: \n" +
                        code);
            } else {
                throw new EmailAlreadyInUseException("Email already in use.");
            }
        } else {
            throw new AdminExistsException("Cannot save new admin");
        }
    }

    @Override
    public void verify(Principal principal, String code) throws UserNotFoundException, CodeExpiredException, TokenException {
        User userByEmail = userRepository.getByEmail(principal.getName());
        User userByCode = getByCode(code);

        if (userByCode != null) {
            if (!userByEmail.equals(userByCode)) {
                throw new TokenException("Token misuse.");
            }
            Duration duration = Duration.between(userByCode.getCodeSentTime(), LocalDateTime.now());
            if (duration.toDays() <= 7) {
                userByCode.setStatus(Status.Verified);
                userByCode.setVerificationCode(null);
                userByCode.setVerificationDate(LocalDateTime.now());
                userRepository.save(userByCode);
            } else {
                mailSender.send(userByCode, "New verification code", "Your new verification code is:\n" + UserServiceHelper.generate());
                throw new CodeExpiredException("This verification code is expired. Please check your email for further details");
            }
        } else {
            throw new UserNotFoundException("Wrong verification code.");
        }
    }

    @Override
    public void delete(Principal principal, String password) throws UserNotFoundException, WrongPasswordException {
        User userToDelete = userRepository.getByEmail(principal.getName());
        if (userToDelete != null) {
            if (userServiceHelper.passwordsMatch(password, userToDelete.getPassword())) {
                mailSender.send(userToDelete, "Farewell!", "It has been nice having you.");
                userToDelete.setDeletionDate(LocalDateTime.now());
                userRepository.delete(userToDelete);
            } else {
                throw new WrongPasswordException("Passwords do not match.");
            }
        } else {
            throw new UserNotFoundException("User not found.");
        }
    }

    @Override
    public void getToken(String email, String password) throws UserNotFoundException, WrongPasswordException {
        User user = userRepository.getByEmail(email);
        if (user != null) {
            if (userServiceHelper.passwordsMatch(email, password)) {
                String resetToken = UserServiceHelper.generate();
                user.setPasswordResetToken(resetToken);
                user.setResetTokenSent(LocalDateTime.now());
                mailSender.send(user, "Reset token", "Here's the token to reset your password! Stay safe.\n" + resetToken +
                        "\nNote: this token expires in 2 hours.");
                userRepository.save(user);
            } else throw new WrongPasswordException("Password incorrect.");
        } else {
            throw new UserNotFoundException("User by email specified was not found");
        }
    }

    @Transactional
    @Override
    public void reset(String token, String password1, String password2) throws UserNotFoundException, WrongPasswordException, CodeExpiredException {
        User user = userRepository.getByPasswordResetToken(token);
        if (user != null) {
            Duration duration = Duration.between(user.getResetTokenSent(), LocalDateTime.now());
            if (duration.toHours() <= 2) {
                if (password1.equals(password2)) {
                    user.setPassword(passwordEncoder.encode(password1));
                    user.setResetTokenSent(null);
                    user.setPasswordResetToken(null);
                    userRepository.save(user);
                } else {
                    mailSender.send(user, "Reset token", "Your last token was expired! Here's a new one: \n" +
                            UserServiceHelper.generate());
                    throw new WrongPasswordException("Passwords do not match");
                }
            } else throw new CodeExpiredException("Reset token expired.");
        } else throw new UserNotFoundException("No user was found by token specified");
    }


}
