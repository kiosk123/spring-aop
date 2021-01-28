package calculator;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


/**
 * 여러 애스펙트에서 공유하기 위한 포인트컷 정의 클래스
 */
@Aspect
public class CalculatorPointcuts {

    /**
     * 여러 애스펙트에서 공유를 위해 포인트 컷이 설정된 메서드는 반드시 public 키워드로 선언 
     */
    @Pointcut("execution(* *.*(..))")
    public void loggingOperation() {
    }

}
