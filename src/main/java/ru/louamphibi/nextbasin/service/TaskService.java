package ru.louamphibi.nextbasin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.entity.EmployeeTask;
import ru.louamphibi.nextbasin.form.EmployeeTaskForm;
import ru.louamphibi.nextbasin.repositoty.TaskRepo;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;

    public boolean saveTask(EmployeeTaskForm employeeTaskForm, Employee employee) {
        if(employee==null) {
            return false;
        }

        EmployeeTask employeeTask = formToTask(employeeTaskForm, null);

        employeeTask.setEmployee(employee);

        taskRepo.save(employeeTask);

        return true;
    }

    public EmployeeTask findById(Long id) {
        Optional<EmployeeTask> result = taskRepo.findById(id);

        EmployeeTask task;

        if (result.isPresent()) {
            task = result.get();
        } else {
            throw new NullPointerException("Task not found " + id);
        }
        return task;
    }

    public void changeTaskStatus(EmployeeTask employeeTask) {
        if(employeeTask.isStatus())
            employeeTask.setStatus(false);
        else
            employeeTask.setStatus(true);

        taskRepo.save(employeeTask);
    }

    public void deleteTaskById(Long id) {
        taskRepo.deleteById(id);
    }

    public void updateTask(EmployeeTaskForm employeeTaskForm, EmployeeTask employeeTask) {

        EmployeeTask updatedTask = formToTask(employeeTaskForm, employeeTask);

        taskRepo.save(updatedTask);
    }

    public List<EmployeeTask> findAllTaskByManagerId(Long id) {
        return taskRepo.findAllTaskByManagerId(id);
    }

    public Long findCountOfManagerTask(Long id) { return taskRepo.findCountOfManagerTasks(id);}

    private EmployeeTask formToTask(EmployeeTaskForm employeeTaskForm, EmployeeTask employeeTask) {
        if (employeeTask == null)
            employeeTask = new EmployeeTask();

        employeeTask.setText(employeeTaskForm.getText());
        employeeTask.setStartDateTime(employeeTaskForm.getStartDateTime());
        employeeTask.setEndDateTime(employeeTaskForm.getEndDateTime());
        employeeTask.setStatus(true);

        return employeeTask;
    }
}
