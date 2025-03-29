package andrehsvictor.memorix.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Slf4j
@Component
@Order(0)
public class LoggingAspect {

    @Pointcut("execution(* andrehsvictor.memorix..*Service.*(..))")
    private void serviceMethod() {
    }

    @Around("serviceMethod()")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodSignature = joinPoint.getSignature().toShortString();

        if (log.isDebugEnabled()) {
            Object[] args = joinPoint.getArgs();
            log.debug("Executing method: {} with args: {}", methodSignature, args);
        } else {
            log.info("Executing method: {}", methodSignature);
        }

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            log.info("Method {} completed successfully in {} ms", methodSignature, executionTime);

            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;

            log.error("Error executing method: {} after {} ms - Exception: {}",
                    methodSignature,
                    executionTime,
                    throwable.getMessage(),
                    throwable);

            throw throwable;
        }
    }

    @Pointcut("execution(* andrehsvictor.memorix..*(..)) && @annotation(andrehsvictor.memorix.annotation.LogExecutionDetail)")
    private void annotatedMethod() {
    }

    @Around("annotatedMethod()")
    public Object logDetailedExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("DETAILED LOGGING: Starting execution of {}", joinPoint.getSignature().toShortString());
        Object result = joinPoint.proceed();
        log.info("DETAILED LOGGING: Completed execution of {}", joinPoint.getSignature().toShortString());
        return result;
    }
}