package ru.job4j.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionHandlingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlingAspect.class);

    @AfterThrowing(
            pointcut = "execution(* ru.job4j.services.*.*(..))",
            throwing = "ex"
    )
    public void handleException(JoinPoint joinPoint, Exception ex) {
        LOGGER.error(
                "Exception in method {} with args {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs(),
                ex
        );
    }
}
