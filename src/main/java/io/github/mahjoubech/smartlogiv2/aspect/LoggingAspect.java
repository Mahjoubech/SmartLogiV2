package io.github.mahjoubech.smartlogiv2.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    @Around("execution(* io.github.mahjoubech.smartlogiv2.service.impl.*ServiceImpl.*(..))")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();


        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;

        log.info("AOP Performance: {}.{}() took {} ms",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                duration
        );

        return result;
    }
}