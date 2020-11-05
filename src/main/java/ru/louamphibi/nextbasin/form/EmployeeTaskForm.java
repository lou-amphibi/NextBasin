package ru.louamphibi.nextbasin.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ToString
public class EmployeeTaskForm {

    @NotNull(message = "Field text is required")
    @Getter
    @Setter
    private String text;

    @NotNull(message = "Field start time is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Getter
    @Setter
    private LocalDateTime startDateTime;

    @NotNull(message = "Field end time is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Getter
    @Setter
    private LocalDateTime endDateTime;

    public EmployeeTaskForm() {}

    public EmployeeTaskForm(@NotNull String text, @NotNull LocalDateTime startDateTime, @NotNull LocalDateTime endDateTime) {
        this.text = text;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }
}
