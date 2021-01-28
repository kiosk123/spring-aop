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
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(0) // 반환 값이 작을 수록 우선 순위 높음
public class CalculatorLoggingAspect {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    // 참조 대상이되는 포인트 컷 선언
    @Pointcut("execution(* *.*(..))")
    private void loggingOperation() {}

    // 포인트 컷 참조
    @Before("CalculatorLoggingAspect.loggingOperation()")
    public void logBefore(JoinPoint joinPoint) {
        log.info(
            "The method " + joinPoint.getSignature().getName() + "() begins with " +
            Arrays.toString(joinPoint.getArgs())
        );
    }

    @After("CalculatorLoggingAspect.loggingOperation()")
    public void logAfter(JoinPoint joinPoint) {
        log.info("The method " + joinPoint.getSignature().getName() + "() ends");
    }

    @AfterReturning(
        pointcut = "CalculatorLoggingAspect.loggingOperation()",
        returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info(
            "The method " + joinPoint.getSignature().getName() + "() ends with " + result
        );
    }

    @AfterThrowing(
        pointcut = "CalculatorLoggingAspect.loggingOperation()",
        throwing = "e"
    )
    public void logAfterThrowing(JoinPoint joinPoint, IllegalArgumentException e) {
        log.error(
            "Illegal argument " + Arrays.toString(joinPoint.getArgs()) + " in " +
            joinPoint.getSignature().getName() + "()"
        );
    }

    @Around("CalculatorLoggingAspect.loggingOperation()")
    public Object logAround(ProceedingJoinPoint joinPoint)throws Throwable {
        log.info(
            "The method " + joinPoint.getSignature().getName() + "() begins with " +
            Arrays.toString(joinPoint.getArgs())
        );
        try {
            Object result = joinPoint.proceed();
            log.info(
                "The method " + joinPoint.getSignature().getName() + "() ends with " + result
            );
            return result;
        } catch (IllegalArgumentException e) {
            log.error(
                "Illegal argument " + Arrays.toString(joinPoint.getArgs()) + " in " +
                joinPoint.getSignature().getName() + "()"
            );
            throw e;
        }
    }
}