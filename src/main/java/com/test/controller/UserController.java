package com.test.controller;

import com.test.model.User;
import com.test.service.UserService;
import com.test.util.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public void save(@Valid @RequestBody User user) throws EmailAlreadyInUseException {
        userService.save(user);
    }

    @PostMapping("/admin")
    public void saveAdmin(@Valid @RequestBody User user) throws EmailAlreadyInUseException, AdminExistsException {
        userService.saveAdmin(user);
    }

    @PostMapping("/verify")
    public void verify(Principal principal, @RequestParam String code) throws UserNotFoundException, CodeExpiredException, TokenException {
        userService.verify(principal, code);
    }

    @DeleteMapping
    public void delete(Principal principal, @RequestParam String password) throws UserNotFoundException, WrongPasswordException {
        userService.delete(principal, password);
    }

    @PostMapping("/getToken")
    public void getToken(@RequestParam String email, @RequestParam String password) throws UserNotFoundException, WrongPasswordException {
        userService.getToken(email, password);
    }

    @PostMapping("/reset")
    public void reset(@RequestParam String resetToken, @RequestParam String password1, @RequestParam String password2) throws UserNotFoundException, CodeExpiredException, WrongPasswordException {
        userService.reset(resetToken, password1, password2);
    }

}
