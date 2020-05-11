package lv.igors.lottery.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* lv.igors.lottery.*.get*(..))")
    private void getters() {
    }

    @Pointcut("execution(* lv.igors.lottery.*.get*(..))")
    private void setters() {
    }

    @Pointcut("execution(* lv.igors.lottery.*.*(..))")
    private void beforeMethodAspect() {
    }

    @AfterThrowing(throwing = "exc", pointcut = "execution(* lv.igors.lottery.*.*(..))")
    public void throwsExc(JoinPoint joinPoint, Throwable exc) {

    }

    @Before("beforeMethodAspect() && !(getters() || setters())")
    public void aspect(JoinPoint joinPoint) {
        Object[] arguments = joinPoint.getArgs();


    }


}
