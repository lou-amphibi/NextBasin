package ru.louamphibi.nextbasin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.louamphibi.nextbasin.controller.util.DaoToFormConverter;
import ru.louamphibi.nextbasin.entity.Employee;
import ru.louamphibi.nextbasin.entity.EmployeeTask;
import ru.louamphibi.nextbasin.form.EmployeeForm;
import ru.louamphibi.nextbasin.service.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RegistrationService registrationService;


    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, trimmerEditor);
    }

    @GetMapping()
    public String adminPage() {

        return "admin/admin";
    }

    @GetMapping("/employees")
    public String employeePage(Model model) {
        List<Employee> employeeList = adminService.findAllEmployees();

        Map<Long, Employee> managerMap = adminService.findEmployeesManagers(employeeList);

        model.addAttribute("employeeList", employeeList);

        model.addAttribute("managerMap", managerMap);

        return "admin/employees";
    }

    @GetMapping("/employees/updateEmployee")
    public String showEmployeeUpdatePage(@RequestParam("employeeId") Long id, Model model) {
        Employee currEmployee = managerService.findById(id);

        List<Employee> managersList = employeeService.findAllManagers();

        EmployeeForm employeeForm = DaoToFormConverter.userToForm(currEmployee);

        model.addAttribute("currEmployee", currEmployee);

        model.addAttribute("managersList", managersList);

        model.addAttribute("employeeForm", employeeForm);

        return "admin/update-employee-form";
    }

    @PostMapping("/employees/saveEmployee")
    public String saveUpdatedEmployee(@Valid @ModelAttribute("employeeForm") EmployeeForm employeeForm,
                                     BindingResult bindingResult, Model model,
                                     @ModelAttribute("employeeId") Long employeeId) {
        if(employeeForm.getManagerId() == -1)
            employeeForm.setManagerId(null);


        if (bindingResult.hasErrors()) {
            model.addAttribute("currEmployee", managerService.findById(employeeId));
            model.addAttribute("employeeForm", employeeForm);
            model.addAttribute("managersList", employeeService.findAllManagers());
            return "admin/update-employee-form";
        }

        if (!adminService.updateEmployee(managerService.findById(employeeId), employeeForm)) {
            model.addAttribute("currEmployee", managerService.findById(employeeId));
            model.addAttribute("employeeForm", employeeForm);
            model.addAttribute("managersList", employeeService.findAllManagers());
            model.addAttribute("usernameOrEmailError", "A user with the same name or email already exists");
            return "admin/update-employee-form";
        }

        return "redirect:/admin/employees";
    }

    @GetMapping("/employees/deleteEmployee")
    public String deleteEmployee(@RequestParam("employeeId") Long id) {
        adminService.deleteEmployee(id);

        return "redirect:/admin/employees";
    }

    @GetMapping("/employees/managerTasks")
    public String showManagerTasks(@RequestParam("managerId") Long id, Model model) {
        List<EmployeeTask> taskList = taskService.findAllTaskByManagerId(id);

        Employee manager = managerService.findById(id);

        model.addAttribute("taskList", taskList);

        model.addAttribute("currManager", manager);

        return "admin/manager-tasks";
    }

    @GetMapping("/employees/deleteTask")
    public String deleteTask(@RequestParam("taskId")Long taskId, @RequestParam("managerId") Long managerId,
                             RedirectAttributes redirectAttributes) {
        taskService.deleteTaskById(taskId);

        redirectAttributes.addAttribute("managerId", managerId);

        return "redirect:/admin/employees/managerTasks";
    }

    @GetMapping("/managers")
    public String managerPage(Model model) {
        model.addAttribute("managerList", employeeService.findAllManagers());

        model.addAttribute("taskService", taskService);

        model.addAttribute("adminService", adminService);

        return "admin/managers";
    }


    @GetMapping("/managers/updateManager")
    public String showManagerUpdatePage(@RequestParam("managerId") Long id, Model model) {
        Employee currManager = managerService.findById(id);

        EmployeeForm employeeForm = DaoToFormConverter.userToForm(currManager);

        model.addAttribute("currManager", currManager);

        model.addAttribute("employeeForm", employeeForm);

        return "admin/update-manager-form";
    }

    @PostMapping("/managers/saveManager")
    public String saveUpdatedManager(@Valid @ModelAttribute("employeeForm") EmployeeForm employeeForm,
                              BindingResult bindingResult, Model model,
                              @ModelAttribute("managerId") Long managerId){
        employeeForm.setManagerId(null);

        if (bindingResult.hasErrors()) {
            model.addAttribute("currManager", managerService.findById(managerId));
            model.addAttribute("employeeForm", employeeForm);
            return "admin/update-manager-form";
        }

        if (!adminService.updateEmployee(managerService.findById(managerId), employeeForm)) {
            model.addAttribute("currManager", managerService.findById(managerId));
            model.addAttribute("employeeForm", employeeForm);
            model.addAttribute("usernameOrEmailError", "A manager with the same name or email already exists");
            return "admin/update-manager-form";
        }

        return "redirect:/admin/managers";
    }

    @GetMapping("/managers/deleteManager")
    public String deleteManager(@RequestParam("managerId") Long id) {
        adminService.deleteManager(id);

        return "redirect:/admin/managers";
    }

    @GetMapping("addNewManager")
    public String showFormForAddNewManager(Model model) {
        model.addAttribute("employeeForm", new EmployeeForm());

        return "admin/new-manager-form";
    }

    @PostMapping("registrationManager")
    public String registrationManager(@Valid @ModelAttribute("employeeForm") EmployeeForm employeeForm,
                                      BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()) {
            return "admin/new-manager-form";
        }

        if(!employeeForm.getPassword().equals(employeeForm.getPasswordConfirm())) {
            model.addAttribute("passwordConfirmedError", "Passwords do not match");
            return "admin/new-manager-form";
        }

        if(!registrationService.saveUser(employeeForm, "ROLE_MANAGER")) {
            model.addAttribute("usernameOrEmailError", "A manager with the same name or email already exists");
            return "admin/new-manager-form";
        }

        model.addAttribute("SuccessfulRegistration", "New manager with username " + employeeForm.getUsername() + " has been successfully registered");
        model.addAttribute("managerList", employeeService.findAllManagers());
        model.addAttribute("taskService", taskService);
        model.addAttribute("adminService", adminService);

        return "admin/managers";
    }
}
