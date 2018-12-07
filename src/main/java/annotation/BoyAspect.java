package annotation;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 解析注解
 * 1.通过@Aspect注解使该类成为切面类
 * 2.通过@Pointcut指定切入点，这里指定的切入点为Boy注解类型，
 * 	 也就是被@Boy修饰的方法，进入该切点
 * 3.
 * 	 @Before 前置通知：在某连接点之前执行的通知，但这个通知不能阻止连接点之前的执行流程（除非抛出一个异常）
 *   @Around 环绕通知：可以实现方法执行前后操作，需要在方法内部执行point.proceed()并返回结果
 *   @AfterReturning 后置通知：在某连接点正常完成后执行的通知，如：一个方法没有异常正常返回
 *   @AfterThrowing 异常通知：在方法抛出异常退出时执行的通知
 *   @After 后置通知：在某连接点正常完成后执行的通知，如：一个方法没有异常正常返回
 * 4.使用方式：在使用@Boy注解的地方会执行本切面类
 * 	 
 * 
 * @author jerry
 *
 */
@Aspect
@Component
public class BoyAspect {

	//切入点
	@Pointcut(value = "@annotation(annotation.Boy)")
	public void pointcut(){
		
	}

	/**
	 * 方法执行前后
	 * 
	 * @return
	 */
	@Around(value = "point() && @annotation(boy)")
	public Object around(ProceedingJoinPoint point, Boy boy){
		System.out.println("执行around...");
		String name = boy.name();
		Class<? extends Object> clz = point.getTarget().getClass();//拦截的类名
		Method method = ((MethodSignature)point.getSignature()).getMethod();//拦截的方法
		System.out.println("执行了 类 ： " + clz + " 方法：" + method + "名称：" + name);
		Boy b = method.getAnnotation(Boy.class);
		System.out.println(b.name());
		try{
			return point.proceed();//执行程序
		}catch(Throwable throwable){
			throwable.printStackTrace();
			return throwable.getMessage();
		}
	}
	
	/**
	 * 执行方法后
	 * @annotation(boy)->Boy boy
	 */
	@AfterReturning(value = "point() && @annotation(boy)", returning = "result")
	public Object afterReturning(JoinPoint joinPoint, Boy boy, Object result){
		System.out.println("执行afterReturning....");
		Object[] params = joinPoint.getArgs();// 获取目标方法体参数
		for(int i = 0; i < params.length; i++){
			System.out.println(params[i]);
		}
		String clzName = joinPoint.getTarget().getClass().getName();//获取目标类名
		String signature = joinPoint.getSignature().getName();//获取目标方法签名
		System.out.println("Target class : " + clzName + ",signature : " + signature);
		System.out.println("after result : " + result);
		return result;
	}
	
	/**
	 * 方法执行后抛出异常
	 */
	@AfterThrowing(value = "point() && @annotation(boy)", throwing = "ex")
	public void afterThrowing(JoinPoint joinPoint, Boy boy, Exception ex){
		System.out.println("执行afterThrowing...");
		System.out.println("请求" + boy.name() +"异常");
	}
}
