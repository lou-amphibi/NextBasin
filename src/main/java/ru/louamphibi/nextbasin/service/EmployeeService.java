package ru.louamphibi.nextbasin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.repositoty.UserRepo;
import java.util.List;

@Service
public class EmployeeService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = userRepo.findByUsername(username);

        if (employee == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        return employee;
    }

    public List<Employee> findAllManagers(){
        return userRepo.findAllManagers();
    }
}
