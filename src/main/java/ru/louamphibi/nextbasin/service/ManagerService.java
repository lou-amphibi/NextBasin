package ru.louamphibi.nextbasin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.entity.EmployeeTask;
import ru.louamphibi.nextbasin.repositoty.TaskRepo;
import ru.louamphibi.nextbasin.repositoty.UserRepo;
import java.util.List;
import java.util.Optional;

@Service
public class ManagerService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TaskRepo taskRepo;

    public List<Employee> findAllEmployeesByManagerId(Long managerId) {
        return userRepo.findByManagerIdOrderByLastNameAscFirstName(managerId);
    }

    public Employee findById(Long id) {
        Optional<Employee> result = userRepo.findById(id);

        Employee employee;

        if(result.isPresent()) {
            employee = result.get();
        } else {
            throw new NullPointerException("id not found " + id);
        }

        return employee;
    }

    public List<Employee> findFreeEmployees() {
        return userRepo.findFreeEmployees();
    }

    public List<EmployeeTask> findEmployeeTask(Long id) {
        return taskRepo.findByEmployeeIdOrderByEndDateTime(id);
    }

    public void attachEmployee(Long employeeId, Long managerId) {
        Employee employee = findById(employeeId);

        employee.setManagerId(managerId);

        userRepo.save(employee);
    }

    public void unpinEmployee(Long id) {
        Employee employee = findById(id);

        employee.setManagerId(null);

        userRepo.save(employee);
    }

    public List<Employee> findOtherManagers(Long id) {
        return userRepo.findOtherManagers(id);
    }
}
