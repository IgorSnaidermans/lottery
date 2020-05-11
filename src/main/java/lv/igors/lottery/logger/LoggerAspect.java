package lv.igors.lottery.logger;

import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@AllArgsConstructor
@Component
@Aspect
public class LoggerAspect {
    LoggerContainer loggerContainer;

    @Pointcut("execution(* lv.igors.lottery.*.get*(..))")
    private void getters() {
    }

    @Pointcut("execution(* lv.igors.lottery.*.get*(..))")
    private void setters() {
    }

    @Pointcut("execution(* lv.igors.lottery..*..*(..))")
    private void allMethodPointcut() {

    }

    @Pointcut("execution(* lv.igors.lottery.logger.*.*(..))")
    private void loggerContainerPointcut() {

    }

    @Pointcut("execution(* lv.igors.lottery.*.*(..))")
    private void appInitialisationPointcut() {

    }

    @Pointcut("allMethodPointcut() && !(appInitialisationPointcut() || " +
            "getters() || setters() || loggerContainerPointcut())")
    private void appFlow() {
    }

    @AfterThrowing(value = "appFlow()", throwing = "exc")
    public void throwsExc(JoinPoint joinPoint, Throwable exc) {

    }

    @Before("appFlow()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        Object[] arguments = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        Logger LOGGER = loggerContainer.findLogger(joinPoint.getTarget().getClass());

        LOGGER.info("Exec: " + methodName + ". Args: " + Arrays.toString(arguments));
    }
}
