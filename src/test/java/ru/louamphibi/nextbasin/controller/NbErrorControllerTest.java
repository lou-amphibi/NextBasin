package ru.louamphibi.nextbasin.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.louamphibi.nextbasin.service.EmployeeService;
import javax.servlet.RequestDispatcher;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = NbErrorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class NbErrorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private AuthenticationSuccessHandler authenticationSuccessHandler;


    @Test
    public void testDefaultErrorPage() throws Exception {
        mvc.perform(get("/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/error"));
    }

    @Test
    public void testNotFoundErrorPage() throws Exception {

        mvc.perform(get("/error").with(request -> {
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, "404");
            return request;
        }))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/error-404"));
    }

    @Test
    public void testInternalServerErrorPage() throws Exception {
        mvc.perform(get("/error").with(request -> {
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, "500");
            return request;
        }))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/error-500"));
    }

    @Test
    public void testForbiddenErrorPage() throws Exception {
        mvc.perform(get("/error").with(request -> {
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, "403");
            return request;
        }))
                .andExpect(status().isOk())
                .andExpect(view().name("errors/error-403"));
    }

}
