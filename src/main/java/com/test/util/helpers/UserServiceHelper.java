package com.test.util.helpers;

import com.test.model.User;
import com.test.model.enums.Status;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserServiceHelper {

    @Autowired
    PasswordEncoder passwordEncoder;

    public static String generate() {
        RandomString generator = new RandomString(8);
        return generator.nextString();
    }

    public void setRegDateAndStatus(User user) {
        user.setRegDate(LocalDateTime.now());
        user.setStatus(Status.Unverified);
    }

    public boolean passwordsMatch(String password, String password2) {
        if (passwordEncoder.matches(password, password2)) {
            return true;
        }
        return false;
    }
}
