package ru.job4j.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* ru.job4j.services.*.*(..))")
    private void serviceLayer() {
    }

    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        System.out.println("Вызов метода: "
                + joinPoint.getSignature().getName()
                + ", args="
                + Arrays.toString(args));
    }
}
