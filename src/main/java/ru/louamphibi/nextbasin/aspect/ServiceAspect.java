package ru.louamphibi.nextbasin.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.logging.Logger;

@Aspect
@Component
public class ServiceAspect {
    private Logger logger = Logger.getLogger(getClass().getName());

    @Pointcut("execution(* ru.louamphibi.nextbasin.service.*.*(..))")
    private void forService() {}

    @Before("forService()")
    private void before(JoinPoint joinPoint) {
        String method = joinPoint.getSignature().toShortString();

        logger.info("---> in Service Aspect @Before: calling method: " + method);

        Object[] args = joinPoint.getArgs();

        for (Object o: args) {
            logger.info("--> argument: " + o);
        }
    }

    @AfterReturning(pointcut = "forService()", returning = "result")
    private void afterReturning(JoinPoint joinPoint, Object result) {
        String method = joinPoint.getSignature().toShortString();

        logger.info("---> in Service Aspect @AfterReturning: from method: " + method);

        logger.info("--> result: " + result);
    }

    @AfterThrowing(pointcut = "forService()", throwing = "ex")
    private void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();

        logger.info("---> in Service Aspect @AfterThrowing: from method: " + method);

        logger.info("--> exception: " + ex + " message " + ex.getMessage());
    }
}
