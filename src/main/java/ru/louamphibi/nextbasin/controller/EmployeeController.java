package ru.louamphibi.nextbasin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.entity.EmployeeTask;
import ru.louamphibi.nextbasin.service.EmployeeService;
import ru.louamphibi.nextbasin.service.ManagerService;
import ru.louamphibi.nextbasin.service.TaskService;
import java.util.List;

@Controller
@RequestMapping("/user-workspace")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private TaskService taskService;

    @GetMapping()
    public String getUserTasksPage(Model model, @AuthenticationPrincipal Employee currentEmployee) {

        List<EmployeeTask> employeeTaskList = managerService.findEmployeeTask(currentEmployee.getId());

        Employee manager;

        if (currentEmployee.getManagerId() == null) {
            manager = new Employee("None", "Nobody", "None@nobody.com");
        } else {
            manager = managerService.findById(currentEmployee.getManagerId());
        }

        model.addAttribute("manager", manager);

        model.addAttribute("taskList", employeeTaskList);

        return "employee/workspace";
    }

    @GetMapping("/updateTaskStatus")
    public String updateTaskStatus(@RequestParam("taskId") Long id) {
        EmployeeTask task = taskService.findById(id);

        taskService.changeTaskStatus(task);

        return "redirect:/user-workspace";
    }

    @GetMapping("/allManagers")
    public String allManagers(Model model) {
        model.addAttribute("managerList", employeeService.findAllManagers());

        return "service/managers";
    }
}
