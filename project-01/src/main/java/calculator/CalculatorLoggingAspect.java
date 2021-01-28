package calculator;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CalculatorLoggingAspect {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* *.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info(
            "The method {}() begins with {} ",
            joinPoint.getSignature().getName(),
            Arrays.toString(joinPoint.getArgs())
        );
    }

    @After("execution(* *.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        log.info("The method {}() ends", joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* *.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info(
            "The method {}() ends with {}",
            joinPoint.getSignature().getName(),
            result
        );
    }

    @AfterThrowing(pointcut = "execution(* *.*(..))", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, IllegalArgumentException e) {
        log.error(
            "Illegal argument {} in {}()",
            Arrays.toString(joinPoint.getArgs()),
            joinPoint.getSignature().getName()
        );
    }

    @Around("execution(* *.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint)throws Throwable {

        log.info(
            "The method {}() begins with {}",
            joinPoint.getSignature().getName(),
            Arrays.toString(joinPoint.getArgs())
        );

        try {
            Object result = joinPoint.proceed();
            log.info(
                "The method {}() ends with ",
                joinPoint.getSignature().getName(),
                result
            );
            return result;
        } catch (IllegalArgumentException e) {
            log.error(
                "Illegal argument {} in {}()",
                Arrays.toString(joinPoint.getArgs()),
                joinPoint.getSignature().getName()
            );
            throw e;
        }
    }
}