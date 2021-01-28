# AspectJ 포인트 컷 표현식 작성

[AspectJ 공식 웹 사이트](www.eclipse.org/aspectj/)

## 메서드 시그니처 패턴

```java
// modifier와 반환형에 상관없고(*), 인자갯수에 상관없이(**) ArithmeticCalculator 인터페이스(클래스)에 선언한 메서드 전부를 매치
execution(* com.study.calculator.ArithmeticCalculator.*(..))

// 대상 클래스나 인터페이스가 애스펙트(@Aspect)와 같은 패키지에 있을 경우 패키지명 생략가능
execution(* ArithmeticCalculator.*(..))

// public modifier와 매칭되는 ArithmeticCalculator의 모든 메소드
execution(public * ArithmeticCalculator.*(..))

// 특정 타입을 반환하는 메서드와 매칭
execution(public double ArithmeticCalculator.*(..))

// 첫번째 인수는 무조건 double 이며, 두번째 인수부터 개수 제한 없음
execution(public double ArithmeticCalculator.*(double, ..))

// 인수형과 개수가 정확하게 매치
execution(public double ArithmeticCalculator.*(double, double))
```

매치하고 싶은 메서드 사이에 이렇다 할 공통 특성이 없는 경우 메서드/타입 레벨에 커스텀 애너테이션을 만든다.  
다음과 같이 선언할 경우 메서드 레벨과 클래스 레벨에 애너테이션을 적용할 수 있다.

```java
@Target({ElementType.METHOD, ElementTpe.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoggingRequired {}
```

위에서 선언한 애너테이션을 애스펙트 대상에 해당하는 메서드에 붙이거나,  
클래스 레벨에 붙이면 클래스의 모든 메서드들이 애스펙트 대상이 된다.

```java
@LoggingRequired
public class ArithmethicCalculatorImpl implements ArithmeticCalculator {
    
    public double add(double a, double b) {
        //...
    }

    public double sub(double a, double b) {

    }
}
```

어드바이스를 적용할 애스펙트에서 @annotation 키워드를 이용해서 포인트컷을 선언한다.

```java
@Aspect
@Component
public class CalculatorLoggingAspect {

    private LoggerFactory log = LoggerFactory.getLogger(this.getClass());

    // @LoggingRequired가 붙은 대상을 모두 애스펙트함
    @Before("@annotation(LoggingRequired)")
    public void logBefore(JoinPoint joinPoint) {
        log.info("The method " + joinPoint.getSignature().getName()
                + "() begins with " + Arrays.toString(joinPoint.getArgs()));
    }
}
```

## 타입 시그니처 패턴

특정한 타입 내부의 모든 조인포인트를 매치하는 포인트컷 표현식도 있다.  
타입으로 AOP 적용시 타입 안에 구현된 메서드를 실행할 때만 어드바이스가 적용되도록 포인트컷 적용 범위를 좁힐 수 있다.  
  
다음은 com.study.calculator 패키지의 전체 메서드 실행 조인포인트를 매치한다.  
```java
within(com.study.calculator.*)
```