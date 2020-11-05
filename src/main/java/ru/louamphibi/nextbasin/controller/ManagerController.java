package ru.louamphibi.nextbasin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.entity.EmployeeTask;
import ru.louamphibi.nextbasin.form.EmployeeTaskForm;
import ru.louamphibi.nextbasin.service.ManagerService;
import ru.louamphibi.nextbasin.service.TaskService;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private TaskService taskService;

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, trimmerEditor);
    }

    @GetMapping()
    public String employeeList(Model model, @AuthenticationPrincipal Employee currentEmployee) {

        model.addAttribute("employeeList", managerService.findAllEmployeesByManagerId(currentEmployee.getId()));

        model.addAttribute("freeEmployeeList", managerService.findFreeEmployees());

        return "manager/management";
    }

    @GetMapping("/showEmployeeTasks")
    public String showEmployeeTasks(@RequestParam("employeeId") Long id, Model model) {
        Employee employee = managerService.findById(id);

        List<EmployeeTask> employeeTaskList = managerService.findEmployeeTask(id);

        model.addAttribute("currEmployee", employee);

        model.addAttribute("taskList", employeeTaskList);

        return "manager/current-employee";
    }

    @GetMapping("showEmployeeTasks/formForAddTasks")
    public String addTasks(@RequestParam("employeeId") Long id, Model model) {
        model.addAttribute("taskForm", new EmployeeTaskForm());

        Employee employee = managerService.findById(id);

        model.addAttribute("currEmployee", employee);

        return "manager/new-task-form";
    }

    @PostMapping("showEmployeeTasks/saveTask")
    public String saveTask(@Valid @ModelAttribute("taskForm") EmployeeTaskForm taskForm,
                           BindingResult bindingResult, Model model,
                           @ModelAttribute("employeeId") Long id,
                           RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors()) {
            model.addAttribute("currEmployee", managerService.findById(id));
            return "manager/new-task-form";
        }

        if (taskForm.getStartDateTime().isBefore(LocalDateTime.now())
            || taskForm.getEndDateTime().isBefore(LocalDateTime.now())
            || taskForm.getStartDateTime().isAfter(taskForm.getEndDateTime())) {

            model.addAttribute("currEmployee", managerService.findById(id));
            model.addAttribute("timeError", "Wrong timing");
            return "manager/new-task-form";
        }

        Employee employee = managerService.findById(id);

        if (!taskService.saveTask(taskForm, employee)) {
            model.addAttribute("taskSaveError", "Failed to save the task");
            model.addAttribute("currEmployee", managerService.findById(id));
            return "manager/new-task-form";
        }

        redirectAttributes.addAttribute("employeeId", id);

        return "redirect:/manager/showEmployeeTasks";
    }

    @GetMapping("updateTaskStatus")
    public String updateTaskStatus(@RequestParam("taskId") Long taskId,
                                   @RequestParam("employeeId") Long employeeId,
                                   RedirectAttributes redirectAttributes) {
        EmployeeTask task = taskService.findById(taskId);

        taskService.changeTaskStatus(task);

        redirectAttributes.addAttribute("employeeId", employeeId);

        return "redirect:/manager/showEmployeeTasks";
    }

    @GetMapping("confirmTask")
    public String confirmTask(@RequestParam("taskId") Long taskId,
                              @RequestParam("employeeId") Long employeeId,
                              RedirectAttributes redirectAttributes) {
        taskService.deleteTaskById(taskId);

        redirectAttributes.addAttribute("employeeId", employeeId);

        return "redirect:/manager/showEmployeeTasks";
    }

    @GetMapping("attachEmployee")
    public String attachEmployee(@RequestParam("employeeId") Long id,
                                 @AuthenticationPrincipal Employee manager) {
        managerService.attachEmployee(id, manager.getId());

        return "redirect:/manager";
    }

    @GetMapping("unpinEmployee")
    public String unpinEmployee(@RequestParam("employeeId") Long id) {
        managerService.unpinEmployee(id);

        return "redirect:/manager";
    }

    @GetMapping("showEmployeeTasks/formForUpdateTask")
    public String showFormForUpdateTask(@RequestParam("taskId") Long taskId,
                             @RequestParam("employeeId") Long employeeId, Model model) {
        Employee employee = managerService.findById(employeeId);

        EmployeeTask employeeTask = taskService.findById(taskId);

        EmployeeTaskForm employeeTaskForm = new EmployeeTaskForm(employeeTask.getText(),
                employeeTask.getStartDateTime(), employeeTask.getEndDateTime());

        model.addAttribute("currEmployee", employee);

        model.addAttribute("taskForm", employeeTaskForm);

        model.addAttribute("currTask", employeeTask);

        return "manager/update-task-form";
    }

    @PostMapping("showEmployeeTasks/updateTask")
    public String updateTask(@Valid @ModelAttribute("taskForm") EmployeeTaskForm taskForm,
                             BindingResult bindingResult, Model model,
                             @ModelAttribute("employeeId") Long employeeId,
                             @ModelAttribute("taskId") Long taskId,
                             RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()) {
            model.addAttribute("currEmployee", managerService.findById(employeeId));
            model.addAttribute("taskForm", taskForm);
            model.addAttribute("currTask", taskService.findById(taskId));
            return "manager/update-task-form";
        }

        if (taskForm.getStartDateTime().isBefore(LocalDateTime.now())
                || taskForm.getEndDateTime().isBefore(LocalDateTime.now())
                || taskForm.getStartDateTime().isAfter(taskForm.getEndDateTime())) {
            model.addAttribute("currEmployee", managerService.findById(employeeId));
            model.addAttribute("taskForm", taskForm);
            model.addAttribute("currTask", taskService.findById(taskId));
            model.addAttribute("timeError", "Wrong timing");
            return "manager/update-task-form";
        }

        taskService.updateTask(taskForm, taskService.findById(taskId));

        redirectAttributes.addAttribute("employeeId", employeeId);

        return "redirect:/manager/showEmployeeTasks";
    }

    @GetMapping("/otherManagers")
    public String otherManagers(Model model, @AuthenticationPrincipal Employee currManager){
        List<Employee> managerList = managerService.findOtherManagers(currManager.getId());

        model.addAttribute("managerList", managerList);

        return "service/managers";
    }
}
