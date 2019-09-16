package annotation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 
 * jerry
 * tom
 * jerry,sugar
 * tom,sugar
 * cora say 'hello jerry!'
 * cora say 'hello tom!'
 * 
 * @author jerry
 *
 */
public class Main {

	public static void main(String[] args) {
		// String response = "{read=true,appId=10011,pageSize=10,receiveBy=33129248,pageNum=1,bizTypes=[20]}";
		String response = "{read:true,appId:10011,pageSize:10,receiveBy:33129248,pageNum:1,bizTypes:[20]}";
		if (StringUtils.isNotEmpty(response)) {
			JSONObject retMap =  (JSONObject)JSON.parse(response);
			if ("10000".equalsIgnoreCase((String) retMap.get("code"))) {
				JSONObject data = (JSONObject) retMap.get("content");
				System.out.println(null == data ? "" : data.toJSONString());
			}
		}
	}

	// public static void main(String[] args){
	// 	Class<?> clz = TestMM.class;
	// 	try{
	// 		TestMM testMM = (TestMM) clz.newInstance();
	// 		Field[] fields = clz.getDeclaredFields();
	// 		//根据属性反射
	// 		for(Field field : fields){
	// 			if(field.isAnnotationPresent(Boy.class)){
	// 				Boy boy = field.getAnnotation(Boy.class);
	// 				field.setAccessible(true);
	// 				field.set(testMM, boy.name());
	// 			}
	// 		}
	//
	// 		System.out.println(testMM.getBoy1());//jerry
	// 		System.out.println(testMM.getBoy2());//tom
	//
	// 		Method[] methods = clz.getDeclaredMethods();
	// 		//根据方法反射
	// 		for(Method method : methods){
	// 			if(method.isAnnotationPresent(MM.class)){//判断方法是否带有MM注解
	// 				MM mm = method.getAnnotation(MM.class);//获取MM注解
	// 				method.setAccessible(true);
	// 				method.invoke(testMM, testMM.getBoy1(), mm.name());
	// 				method.invoke(testMM, testMM.getBoy2(), mm.name());
	// 				System.out.println(mm.name());//cora
	// 			}
	// 		}
	// 	}catch(Exception e){
	//
	// 	}
	// }
}
