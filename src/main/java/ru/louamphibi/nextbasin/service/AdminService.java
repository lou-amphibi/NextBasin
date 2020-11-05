package ru.louamphibi.nextbasin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.repositoty.UserRepo;
import java.util.*;

@Service
public class AdminService {

    @Autowired
    private UserRepo userRepo;

    public List<Employee> findAllEmployees() {
        return userRepo.findAllEmployees();
    }

    public Map<Long, Employee> findEmployeesManagers(List<Employee> employeeList) {
        Map<Long, Employee> managerMap = new HashMap<>();

        for(int i=0; i<employeeList.size(); i++) {
            Long managerId = employeeList.get(i).getManagerId();
            if (managerId != null) {
                Optional<Employee> result = userRepo.findById(managerId);
                managerMap.put(employeeList.get(i).getId(), result.get());
            } else {
                managerMap.put(employeeList.get(i).getId(), null);
            }
        }

        return managerMap;
    }

    public boolean updateEmployee(Employee employee, EmployeeForm employeeForm) {
        Employee employeeFromDb = userRepo.findByUsername(employeeForm.getUsername());

        Employee employeeEmailCheck = userRepo.findByEmail(employeeForm.getEmail());

        if (employeeFromDb != null && !employeeFromDb.getUsername().equals(employee.getUsername()))
            return false;

        if (employeeEmailCheck != null && !employeeEmailCheck.getEmail().equals(employee.getEmail()))
            return false;

        employee.setUsername(employeeForm.getUsername());
        employee.setFirstName(employeeForm.getFirstName());
        employee.setLastName(employeeForm.getLastName());
        employee.setEmail(employeeForm.getEmail());
        employee.setManagerId(employeeForm.getManagerId());

        userRepo.save(employee);

        return true;
    }

    public void deleteEmployee(Long id) {
        userRepo.deleteById(id);
    }

    public void deleteManager(Long id) {
        List<Employee> employeeList = userRepo.findByManagerIdOrderByLastNameAscFirstName(id);

        for (Employee e: employeeList)
            e.setManagerId(null);

        userRepo.deleteById(id);
    }

    public List<Employee> findAllAdmins(){
        return userRepo.findAllAdmins();
    }

    public Long countAllByManagerId(Long id) {return userRepo.countAllByManagerId(id);}
}
