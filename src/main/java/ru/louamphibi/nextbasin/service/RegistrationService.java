package ru.louamphibi.nextbasin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.entity.Role;
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.repositoty.UserRepo;
import java.util.Collections;

@Service
public class RegistrationService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean saveUser(EmployeeForm employeeForm, String role) {

        Employee employee = formToEmployee(employeeForm);

        Employee employeeFromDb = userRepo.findByUsername(employee.getUsername());

        Employee employeeWithSameEmail = userRepo.findByEmail(employee.getEmail());

        if (employeeFromDb != null || employeeWithSameEmail != null) {
            return false;
        }

        saveEmployeeRole(employee, role);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        userRepo.save(employee);

        return true;
    }

    private Employee formToEmployee(EmployeeForm employeeForm) {
        Employee employee = new Employee();

        employee.setUsername(employeeForm.getUsername());
        employee.setFirstName(employeeForm.getFirstName());
        employee.setLastName(employeeForm.getLastName());
        employee.setPassword(employeeForm.getPassword());
        employee.setEmail(employeeForm.getEmail());
        employee.setManagerId(employeeForm.getManagerId());

        return employee;
    }

    private void saveEmployeeRole(Employee employee, String role) {

        if (role.equals("ROLE_MANAGER"))
            employee.setRoles(Collections.singleton(new Role(2L, "ROLE_MANAGER")));
        else if (role.equals("ROLE_ADMIN"))
            employee.setRoles(Collections.singleton(new Role(3L, "ROLE_ADMIN")));
        else
            employee.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));

    }
}
