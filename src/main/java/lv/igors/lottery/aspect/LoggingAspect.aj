package lv.igors.lottery.aspect;

import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@AllArgsConstructor
@Component
public aspect LoggingAspect {
    LoggerContainer loggerContainer;

    @Pointcut("execution(* lv.igors.lottery.*.get*(..))")
    private void getters() {
    }

    @Pointcut("execution(* lv.igors.lottery.*.get*(..))")
    private void setters() {
    }

    @Pointcut("execution(* lv.igors.lottery.*.*(..))")
    private void beforeMethodAspect() {

    }

    @AfterThrowing(value = "execution(* lv.igors.lottery.*.*(..))", throwing = "exc")
    public void throwsExc(JoinPoint joinPoint, Throwable exc) {

    }

    @Before("beforeMethodAspect() && !(getters() || setters())")
    public void logBeforeMethod(JoinPoint joinPoint) {
        Object[] arguments = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().toString();
        Logger LOGGER = loggerContainer.findLogger(joinPoint.getClass());

        LOGGER.info("Exec= " + methodName + ". Args= " + Arrays.toString(arguments));
    }
}
