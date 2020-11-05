package ru.louamphibi.nextbasin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.louamphibi.nextbasin.entity.ConfirmationToken;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.repositoty.ConfirmationTokenRepo;
import ru.louamphibi.nextbasin.repositoty.UserRepo;

@Service
public class PasswordService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ConfirmationTokenRepo confirmationTokenRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Employee findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public ConfirmationToken saveConfirmationToken(Employee employee) {
        ConfirmationToken confirmationToken = new ConfirmationToken(employee);

        confirmationTokenRepo.save(confirmationToken);

        return confirmationToken;
    }

    public ConfirmationToken findByConfirmationToken(String confirmationToken) {
        return confirmationTokenRepo.findByConfirmationToken(confirmationToken);
    }

    public String changePassword(Employee employee, String password) {
        employee.setPassword(password);

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        userRepo.save(employee);

        return password;
    }

    public ConfirmationToken findByEmployeeId(Long id) {
        return confirmationTokenRepo.findByEmployeeId(id);
    }

    public Long deleteConfirmationToken(ConfirmationToken confirmationToken) {
        Long userId = confirmationToken.getEmployee().getId();

        confirmationTokenRepo.delete(confirmationToken);

        return userId;
    }
}
