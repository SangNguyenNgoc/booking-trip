package org.example.vehicle.utils.logging;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Log4j2
public class RequestLogging {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    @Async
    @Before("controllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Request: entering method: {} with arguments: {}",
                joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @Async
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Request: exiting method: {} with result: {}",
                joinPoint.getSignature().toShortString(), result);
    }

    @Async
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Request: method: {} threw exception: {}",
                joinPoint.getSignature().toShortString(), error.getMessage());
    }
}
