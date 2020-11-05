package ru.louamphibi.nextbasin.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ToString
public class EmployeeForm {

    @NotNull(message = "Field username is required")
    @Size(min=2, message = "(username) At least 2 characters")
    @Getter
    @Setter
    private String username;

    @NotNull(message = "Field first name is required")
    @Size(min=2, message = "(first name) At least 2 characters")
    @Getter
    @Setter
    private String firstName;

    @NotNull(message = "Field last name is required")
    @Size(min=2, message = "(last name) At least 2 characters")
    @Getter
    @Setter
    private String lastName;

    @NotNull(message = "Field password is required")
    @Size(min=5, message = "(password) At least 5 characters")
    @Getter
    @Setter
    private String password;

    @NotNull(message = "Field password confirm is required")
    @Size(min=5, message = "(password) At least 5 characters")
    @Getter
    @Setter
    private String passwordConfirm;

    @NotNull(message = "Field email is required")
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Invalid email format")
    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private Long managerId;

    public EmployeeForm() {}

    public EmployeeForm(@NotNull(message = "Field username is required") @Size(min = 2, message = "(username) At least 2 characters") String username, @NotNull(message = "Field first name is required") @Size(min = 2, message = "(first name) At least 2 characters") String firstName, @NotNull(message = "Field last name is required") @Size(min = 2, message = "(last name) At least 2 characters") String lastName, @NotNull(message = "Field password is required") @Size(min = 5, message = "(password) At least 5 characters") String password, @NotNull(message = "Field password confirm is required") @Size(min = 5, message = "(password) At least 5 characters") String passwordConfirm, @NotNull(message = "Field email is required") @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", message = "Invalid email format") String email, Long managerId) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.email = email;
        this.managerId = managerId;
    }
}
