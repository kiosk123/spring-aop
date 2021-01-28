# 스프링 AOP

## 프로젝트 구성

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

```
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