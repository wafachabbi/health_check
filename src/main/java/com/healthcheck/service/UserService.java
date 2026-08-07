package com.healthcheck.service;

import com.healthcheck.model.User;
import com.healthcheck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public List<User> getAll() { return userRepository.findAll(); }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    public User create(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent())
            throw new RuntimeException("Username already exists");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        auditService.log("system", "CREATE_USER", "users", "Created user: " + saved.getUsername() + " role: " + saved.getRole(), "system");
        return saved;
    }

    public User update(Long id, User user) {
        User existing = getById(id);
        existing.setEmail(user.getEmail());
        existing.setRole(user.getRole());
        if (user.getPassword() != null && !user.getPassword().isEmpty())
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(existing);
        auditService.log("system", "UPDATE_USER", "users", "Updated user: " + saved.getUsername(), "system");
        return saved;
    }

    public void delete(Long id) {
        User user = getById(id);
        userRepository.deleteById(id);
        auditService.log("system", "DELETE_USER", "users", "Deleted user: " + user.getUsername(), "system");
    }
}
