# 스프링 AOP

## 프로젝트 구성

스프링 5  
JAVA 11  
gradle 6.6

## 설명

AOP 프로그래밍을 위한 의존 라이브러리 설정은 다음과 같다.  

```
implementation "org.aspectj:aspectjrt:<version>"
implementation "org.aspectj:aspectjweaver:<version>"
```

## 목차

### 1. 애너테이션을 이용하여 AOP 처리 기본 - (calculator.CalculatorLoggingAspect)

애스펙트(Aspect)를 정의하려면 자바 클래스에 @Aspect를 붙인 후 메서드별로 필요한 애너테이션을 붙여 어드바이스를 만든다.  
어드바이스 애너테이션은 **@Before, @After, @AfterReturning, @AfterThrowing, @Around** 를 사용할 수 있다.  
  
애스펙트(Aspect)를 활성화하려면 @Configuration 클래스에 **@EnableAspectJAutoProxy**를 붙인다.  
기본적으로 스프링은 AOP를 적용시 다이나믹 프록시를 이용하지만 사용하지 않을 경우엔 CGLIB 프록시를 적용할 수 있다.  

**CGLIB 프록시를 적용하려면 @EnableAspectJAutoProxy(proxyTargetClass = true) 로 설정**한다.  
  
어드바이스에는 @어드바이스(포인트컷) 형태로 포인트컷을 적용할 수 있다.  
포인트컷은 어드바이스에 적용할 타입 및 객체를 찾는 표현식을 말한다.  
  
다음은 포인트 컷 예시다.

```java
/**
    ArithmeticCalculator 인터페이스의
    모든 modifier(public, private, protected)와 모든 반환형(return type)과 매치(*)하며,
    메서드의 파라미터의 갯수는 상관없이(..)
    일치하는 add 메소드를 실행하라.
 */ 
@Before("execution(* ArithmeticCalculator.add(..))")
public void logBefore() {
    //...
}
```

- **@Before** - 특정 프로그램 실행지점 이전의 공통 관심사를 처리한다.
- **@After** - 조인포인트가 끝나면 실행된다. 조인포인트가 조인포인트의 성공 여부와 상관없이 실행된다.
- **@AfterReturning** - 조인포인트의 성공 여부와 상관없이 작동하며, 조인포인트가 값을 반환할 경우에 로깅하고자 할때 사용한다.
- **@AfterThrowing** - 조인포인트 실행중 예외가 발생했을 경우 실행한다.
- **@Advice** - 조인포인트를 완전히 감쌀때 사용 (= @Before + @AfterReturning + @AfterThrowing) 조인포인트 인수형은 ProceedingJoinPoint를 사용하며 이걸 이용해서 타겟 메서드를 호출한다.

### 2. 조인 포인트 정보 가져오기 - (calculator.CalculatorLoggingAspect)

어드바이스에서 조인포인트에 엑세스 할때. 실행되는 타겟 메서드 정보(선언 타입, 인수값등..)을 가져올 수 있다.

### 3. 애스펙트 우선순위 설정

같은 조인포인트에 애스펙트(@Aspect)를 여러개 적용할 경우, 애스펙트 간 순위를 정해야한다.  
예를 들어 로깅과 검증 애스펙트(@Aspect)를 둘 다 사용할때 어느 쪽을 먼저 적용해야 할 지 알 수 없기 때문에  
우선 순위를 부여해서 실행 순서를 결정한다.

- Ordered 인터페이스 구현으로 우선 순위결정  - (calculator.CalculatorValidationAspect)
- @Order 으로 우선 순위 결정 - (calculator.CalculatorLoggingAspect)

### 4. 애스펙트 포인트컷 재사용하기

포인트 컷을 여러 번 되풀이해서 쓸 경우 포인터 컷 하나만 선언 후 선언한 포인트 컷을 참조해서 사용한다.

#### 4-1. 참조 대상이 되는 포인트 컷 선언

바디가 비어있는 메서드에 @PointCut을 붙여 참조대상이 되는 포인트컷을 선언하고 참조한다. - (calculator.CalculatorLoggingAspect)

#### 4-2. 여러 애스펙트에서 포인트 컷 공유

여러 애스펙트에서 포인트 컷을 공유하기 위해 별도의 포인트 컷만 정의하는 클래스를 생성 후 @Aspect를 붙이고 포인트 컷을 정의한다.  
포인트컷이 정의된 메서드는 반드시 public으로 선언한다. - (calculator.CalculatorPointcuts, calculator.CalculatorLoggingAspect)

#### 5. AspectJ 포인트컷 표현식 작성

상세 내용은 디렉터리 참고
