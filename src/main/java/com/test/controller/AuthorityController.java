package com.test.controller;

import com.test.model.Authority;
import com.test.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/authority")
public class AuthorityController {
    @Autowired
    private AuthorityService authorityService;

    @PostMapping
    public void save(@Valid @RequestBody Authority authority) {
        authorityService.save(authority);
    }

    @GetMapping()
    public ResponseEntity<List<Authority>> getAll() {
        List<Authority> authorities = authorityService.getAll();
        return ResponseEntity.ok(authorities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Authority> getById(@PathVariable int id){
        Authority authority = authorityService.getById(id);
        return ResponseEntity.ok(authority);
    }
}
