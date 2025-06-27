package andrehsvictor.memorix.common.aspect;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* andrehsvictor.memorix.*.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "SERVICE");
    }

    @Around("execution(* andrehsvictor.memorix.*.*Controller.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "CONTROLLER");
    }

    @Around("execution(* andrehsvictor.memorix.*.repository.*.*(..))")
    public Object logRepositoryMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return logMethodExecution(joinPoint, "REPOSITORY");
    }

    private Object logMethodExecution(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        String methodSignature = String.format("%s.%s", className, methodName);
        
        logMethodEntry(methodSignature, args, layer);
        
        long startTime = System.nanoTime();
        Object result = null;
        Throwable exception = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            exception = ex;
            throw ex;
        } finally {
            long endTime = System.nanoTime();
            long executionTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            
            if (exception != null) {
                logMethodException(methodSignature, exception, executionTime, layer);
            } else {
                logMethodExit(methodSignature, result, executionTime, layer);
            }
        }
    }

    private void logMethodEntry(String methodSignature, Object[] args, String layer) {
        if (log.isDebugEnabled()) {
            if (args != null && args.length > 0) {
                String sanitizedArgs = sanitizeArguments(args);
                log.debug("[{}] Entering method: {} with arguments: {}", layer, methodSignature, sanitizedArgs);
            } else {
                log.debug("[{}] Entering method: {} with no arguments", layer, methodSignature);
            }
        } else {
            log.info("[{}] Entering method: {}", layer, methodSignature);
        }
    }

    private void logMethodExit(String methodSignature, Object result, long executionTime, String layer) {
        if (log.isDebugEnabled()) {
            String sanitizedResult = sanitizeReturnValue(result);
            log.debug("[{}] Exiting method: {} with result: {} (execution time: {}ms)", 
                     layer, methodSignature, sanitizedResult, executionTime);
        } else {
            log.info("[{}] Exiting method: {} (execution time: {}ms)", layer, methodSignature, executionTime);
        }
    }

    private void logMethodException(String methodSignature, Throwable exception, long executionTime, String layer) {
        log.error("[{}] Exception in method: {} after {}ms - {}: {}", 
                 layer, methodSignature, executionTime, 
                 exception.getClass().getSimpleName(), exception.getMessage());
    }

    private String sanitizeArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        
        Object[] sanitized = Arrays.stream(args)
            .map(this::sanitizeValue)
            .toArray();
        
        return Arrays.toString(sanitized);
    }

    private String sanitizeReturnValue(Object result) {
        if (result == null) {
            return "null";
        }
        
        return sanitizeValue(result).toString();
    }

    private Object sanitizeValue(Object value) {
        if (value == null) {
            return null;
        }
        
        String className = value.getClass().getSimpleName();
        String stringValue = value.toString();
        
        if (containsSensitiveData(stringValue)) {
            return "[SENSITIVE_DATA_HIDDEN]";
        }
        
        if (stringValue.length() > 100) {
            return String.format("%s[%s...] (truncated, length: %d)", 
                               className, stringValue.substring(0, 100), stringValue.length());
        }
        
        return value;
    }

    private boolean containsSensitiveData(String value) {
        if (value == null) {
            return false;
        }
        
        String lowerValue = value.toLowerCase();
        return lowerValue.contains("password") || 
               lowerValue.contains("token") || 
               lowerValue.contains("secret") || 
               lowerValue.contains("key") ||
               lowerValue.contains("authorization") ||
               lowerValue.contains("bearer") ||
               lowerValue.matches(".*\\b\\d{4}[-\\s]?\\d{4}[-\\s]?\\d{4}[-\\s]?\\d{4}\\b.*") ||
               lowerValue.matches(".*\\b[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\\b.*");
    }
}
