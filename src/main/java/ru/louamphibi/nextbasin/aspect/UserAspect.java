package ru.louamphibi.nextbasin.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import ru.louamphibi.nextbasin.aspect.error.BindingResultErrorsLogger;
import java.util.logging.Logger;

@Aspect
@Component
public class UserAspect {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private BindingResultErrorsLogger bindingResultErrorsLogger;

    @Pointcut("execution(String ru.louamphibi.nextbasin.controller.RegistrationController.addEmployee(..))")
    private void forRegistrationEmployee() {}

    @Pointcut("execution(String ru.louamphibi.nextbasin.controller.AdminController.registrationManager(..))")
    private void forRegistrationManager() {}

    @Pointcut("forRegistrationEmployee() || forRegistrationManager()")
    private void forRegistrationNewUser() {}


    @Pointcut("execution(String ru.louamphibi.nextbasin.controller.AdminController.saveUpdatedEmployee(..))")
    private void forUpdateEmployee() {}

    @Pointcut("execution(String ru.louamphibi.nextbasin.controller.AdminController.saveUpdatedManager(..))")
    private void forUpdateManager() {}

    @Pointcut("execution(String ru.louamphibi.nextbasin.controller.MainController.updateProfile(..))")
    private void forUpdateProfile() {}

    @Pointcut("forUpdateEmployee() || forUpdateManager() || forUpdateProfile()")
    private void forUpdateUser() {}

    @Pointcut("execution(String ru.louamphibi.nextbasin.controller.ForgottenPasswordController.*(..))")
    private void forResetPassword() {}

    @Pointcut("forRegistrationNewUser() || forUpdateUser() || forResetPassword()")
    private void forUser() {}


    @Before("forUser()")
    private void before(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();

        logger.info("===> in User Aspect @Before: calling method: " + method);

        Object[] args = joinPoint.getArgs();

        for (Object o: args) {
            if (o instanceof BindingResult) {
                BindingResult bs = (BindingResult) o;
                if (bs.hasErrors())
                    bindingResultErrorsLogger.logBindingResultErrors(bs);
                else
                    logger.info("==> argument: " + o);
            } else {
                logger.info("==> argument: " + o);
            }
        }
    }

    @AfterReturning(pointcut = "forUser()", returning = "result")
    private void afterReturning(JoinPoint joinPoint, Object result) {
        String method = joinPoint.getSignature().toShortString();

        logger.info("===> in User Aspect @AfterReturning: from method: " + method);

        logger.info("==> returning url - " + result);
    }

    @AfterThrowing(pointcut = "forUser()", throwing = "ex")
    private void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();

        logger.info("===> in User Aspect @AfterThrowing: from method: " + method);

        logger.info("==> exception: " + ex + " message " + ex.getMessage());
    }
}
