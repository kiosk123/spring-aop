# AspectJ 포인트 컷 표현식 작성

[AspectJ 공식 웹 사이트](www.eclipse.org/aspectj/)

## 메서드 시그니처 패턴

```java
// modifier와 반환형에 상관없고(*), 인자갯수에 상관없이(**) ArithmeticCalculator 인터페이스(클래스)에 선언한 메서드 전부를 매치
execution(* hmeticCalculator.*(..))

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

## 타입 시그니처 패턴 - [참고](https://www.baeldung.utorial)

특정한 타입 내부의 모든 조인포인트를 매치하는 포인트컷 표현식도 있다.  
타입으로 AOP 적용시 타입 안에 구현된 메서드를 실행할 때만 어드바이스가 적용되도록 포인트컷 적용 범위를 좁힐 수 있다.  
  
```java
//  전체 메서드 실행 조인포인트를 매치한다.  
within(// 하위 패키지도 함께 매치하려면 와일드카드 앞에 점(.)하나를 더 붙인다.
within(
// 어느 한 클래스 내부에 구현된 메서드 실행 조인 포인트를 매치한다.
within(hmeticCalculatorImpl)

// 해당 클래스의 패키지가 애스펙트와 같으면 패키지명은 안 써도 된다.
within(ArithmeticCalculatorImpl)

// ArithmeticCalculator 인터페이스를 구현한 모든 클래스 실행 조인포인트를 매치
within(ArithmeticCalculator+)
```

## 포인트컷 표현식 조합

포인트컷 표현식은 &&(and), ||(or), !(not)등의 연산자로 조합가능
```java

within(ArithmeticCalculator+) || within(UnitCalculator+)
```

포인트컷 표현식이나 다른 포인트컷을 가리키는 레퍼런스 모두 이런 연산자로 묶을 수 있음
```java
@Aspect
public class CalculatorPointcuts {

    @Pointcut("within(ArithmeticCalculator+)")
    public void arithmeticOperation() {
    }

    @Pointcut("within(UnitCalculator+)")
    public void unitOperation() {
    }

    @Pointcut("arithmeticOperation() || unitOperation()")
    public void loggingOperation() {
    }
}
```

## 포인트컷 매개변수 선언

표현식 target()과 args()로 각각 현재 조인포인트의 대상 객체 및 인수값을 포착하면 포인트컷 매개변수로 빼낼 수 있다.  
이렇게 도출한 매개변수는 자신과 이름이 똑같은 어드바이스 메서드의 인수로 전달한다.
```java
@Aspect
@Component
public class CalculatorLoggingAspect {
    // 
    @Before("execution(* *.*(..)) && target(target) && args(a,b)")
    public void logParameter(Object target, double a, double b) {
        log.info("Target class : " + target.getClass().getName());
        log.info("Arguments : " + a + ", " + b);
    }
}
```

포인트컷을 독립적으로 선언해 사용할 경우 포인트컷 메서드의 인수 목록에도 함께 넣는다.
```java
@Aspect
public class CalculatorPointcuts {
    //...
    @Pointcut("execution(* *.*(..)) && target(target) && args(a,b)")
    public void parameterPointcut(Object target, double a, double b) {
    }
}
```

독립적으로 선언된 포인트 컷은 다음과 같이 참조한다.
```java
@Aspect
@Component
public class CalculatorLoggingAspect {
    //...
    @Before("CalculatorPointcuts.parameterPointcut(target,a,b)")
    public void logParameter(Object target, double a, double b) {
        log.info("Target class : " + target.getClass().getName());
        log.info("Arguments : " + a + ", " + b);
    }
}
```

## 인트로덕션을 이용해 POJO가 여러클래스에서 상속받은 것과 같은 효과를 내도록 구현하기

자바는 언어구조상 하나의 클래스에서만 상속받을 수 있기 때문에 동시에 여러 구현 클래스로부터 기능을 물려받아 쓰는 것은 불가능하다.  
객체가 어떤 인터페이스의 구현 클래스를 공급받아 동적으로 인터페이스를 구현하는 인트로덕션을 사용하면 런타임에 객체가 구현 클래스를,  
상속하는 것 처럼 보이게 하는 것이 가능  

### 구현순서  

다음과 같이 두 인터페이스를 정의한다.
```java
public interface MinCalculator {

    double min(double a, double b);
}

public interface MaxCalculator {

    double max(double a, double b);
}
```

두 인터페이스를 구현한 클래스를 작성한다.
```java
public class MaxCalculatorImpl implements MaxCalculator {

    @Override
    public double max(double a, double b) {
        double result = (a >= b) ? a : b;
        System.out.println("max(" + a + ", " + b + ") = " + result);
        return result;
    }
}


public class MinCalculatorImpl implements MinCalculator {

    @Override
    public double min(double a, double b) {
        double result = (a <= b) ? a : b;
        System.out.println("min(" + a + ", " + b + ") = " + result);
        return result;
    }
}
```

다음은 인트로덕션 대상이 되는 ArithmeticCalculatorImpl 클래스이다.
```java
@Component("arithmeticCalculator")
@LoggingRequired
public class ArithmeticCalculatorImpl implements ArithmeticCalculator {

    @Override
    public double add(double a, double b) {
        double result = a + b;
        System.out.println(a + " + " + b + " = " + result);
        return result;
    }

    @Override
    public double sub(double a, double b) {
        double result = a - b;
        System.out.println(a + " - " + b + " = " + result);
        return result;
    }

    @Override
    public double mul(double a, double b) {
        double result = a * b;
        System.out.println(a + " * " + b + " = " + result);
        return result;
    }

    @Override
    public double div(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Division by zero");
        }
        double result = a / b;
        System.out.println(a + " / " + b + " = " + result);
        return result;
    }
}
```

ArithmeticCalculatorImpl 클래스에 MaxCalculatorImpl와 MinCalculatorImpl을 인트로덕션한다.  
@DeclareParents의 value 속성에 인트로덕션 대상 클래스를 설정하고, defaultImpl에 인트로덕션 클래스를 설정한다.
```java
@Aspect
@Component
public class CalculatorIntroduction {

    @DeclareParents(
            value = "calculator.ArithmeticCalculatorImpl",
            defaultImpl = MaxCalculatorImpl.class)
    public MaxCalculator maxCalculator;

    @DeclareParents(
            value = "calculator.ArithmeticCalculatorImpl",
            defaultImpl = MinCalculatorImpl.class)
    public MinCalculator minCalculator;
}
```

인트로덕션된 클래스를 다음과 같이 호출하여 사용한다.
```java
public class App {

    public static void main(String[] args) {

        ApplicationContext context =
                new GenericXmlApplicationContext("appContext.xml");

        ArithmeticCalculator arithmeticCalculator =
                (ArithmeticCalculator) context.getBean("arithmeticCalculator");

        MaxCalculator maxCalculator = (MaxCalculator) arithmeticCalculator;
        maxCalculator.max(1, 2);

        MinCalculator minCalculator = (MinCalculator) arithmeticCalculator;
        minCalculator.min(1, 2);
    }
}
```

#### 인트로덕션을 이용한 POJO에 상태추가

기존 객체에 새로운 상태를 추가해서 호출 횟수, 최종 수정 일자등 사용 내역을 파악하고 싶은 경우가 있을 때  
레이어 구조가 다른 클래스 상태를 추가할 수 있다.  
  
다음은 Calculator 객체의 호출 횟수를 기록하는 인트로덕션을 구현한다.  
원본 클래스에는 호출 횟수를 담을 카운터 필드가 없기 때문에 Counter 인터페이스를 이용해서 구현한다.

```java
public interface Counter {
    void increase();
    int getCount();
}

public class CounterImpl implements Counter {

    private int count;

    public void increase() {
        count++;
    }

    public int getCount() {
        return count;
    }
}


@Aspect
@Component
public class CalculatorIntroduction {

    // Counter 인터페이스를 모든 Calulator 객체로 인트로덕션
    @DeclareParents(
            value = "com.study.calculator.*CalculatorImpl",
            defaultImpl = CounterImpl.class)
    public Counter counter;

    // 계산기 메서드를 호출할 때마다 counter값 하나씩 증가
    // target(counter)가 this(counter)를 쓴 이유는 Counter 인터페이스를 구현한 객체는 프록시가 유일하기 때문
    @After("execution(* com.study.calculator.*Calculator.*(..)) && this(counter)")
    public void increaseCount(Counter counter) {
        counter.increase();
    }

}

public class App {

    public static void main(String[] args) {

        ApplicationContext context =
                new GenericXmlApplicationContext("appContext.xml");

        ArithmeticCalculator arithmeticCalculator =
                (ArithmeticCalculator) context.getBean("arithmeticCalculator");
        arithmeticCalculator.add(1, 2);
        arithmeticCalculator.sub(4, 3);
        arithmeticCalculator.mul(2, 3);
        arithmeticCalculator.div(4, 2);

        UnitCalculator unitCalculator =
                (UnitCalculator) context.getBean("unitCalculator");
        unitCalculator.kilogramToPound(10);
        unitCalculator.kilometerToMile(5);

        // 인트로덕션된 Calulator은 호출된 횟수를 카운트하는 대상이 아님
        MaxCalculator maxCalculator = (MaxCalculator) arithmeticCalculator;
        maxCalculator.max(1, 2);

        MinCalculator minCalculator = (MinCalculator) arithmeticCalculator;
        minCalculator.min(1, 2);

        // Caculator의 메서드가 호출된 횟수를 출력 - 4
        Counter arithmeticCounter = (Counter) arithmeticCalculator;
        System.out.println(arithmeticCounter.getCount());

        // Caculator의 메서드가 호출된 횟수를 출력 - 2
        Counter unitCounter = (Counter) unitCalculator;
        System.out.println(unitCounter.getCount());
    }
}
```