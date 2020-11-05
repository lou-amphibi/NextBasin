package ru.louamphibi.nextbasin.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import ru.louamphibi.nextbasin.aspect.error.BindingResultErrorsLogger;
import java.util.logging.Logger;

@Aspect
@Component
public class TaskAspect {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private BindingResultErrorsLogger bindingResultErrorsLogger;

    @Pointcut("execution(String ru.louamphibi.nextbasin.controller.ManagerController.saveTask(..))")
    private void forSaveTask() {}

    @Pointcut("execution(String ru.louamphibi.nextbasin.controller.ManagerController.updateTask(..))")
    private void forUpdateTask() {}

    @Pointcut("forSaveTask() || forUpdateTask()")
    private void forSaveAndUpdateTask() {}

    @Before("forSaveAndUpdateTask()")
    private void before(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();

        logger.info("+++> in Task Aspect @Before: calling method: " + method);

        Object[] args = joinPoint.getArgs();

        for (Object o: args) {
            if (o instanceof BindingResult) {
                BindingResult bs = (BindingResult) o;
                if (bs.hasErrors())
                    bindingResultErrorsLogger.logBindingResultErrors(bs);
            }
            logger.info("++> argument: " + o);
        }
    }

    @AfterReturning(pointcut = "forSaveAndUpdateTask()", returning = "result")
    private void afterReturning(JoinPoint joinPoint, Object result) {
        String method = joinPoint.getSignature().toShortString();

        logger.info("+++> in Task Aspect @AfterReturning: from method: " + method);

        logger.info("++> returning url - " + result);
    }
}
