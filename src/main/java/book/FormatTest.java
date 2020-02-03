package book;

import java.util.Date;
import java.util.Formatter;

/**
 * ***********************************************
 * d：整数型转换（10进制）    
 * e：浮点数（科学计数）
 * c：Unicode字符			  
 * x：整数（16进制）
 * b：Boolean类型			 
 * h：散列码（16进制）
 * s：String				  
 * %：字符%
 * f：浮点数（10进制）
 * 
 * 日期：%t
 * c:包括全部日期和时间信息   星期六 十月 27 14:21:20 CST 2007
 * F:“年-月-日”格式  2007-10-27
 * D:“月/日/年”格式  10/27/07
 * r:“HH:MM:SS PM”格式（12时制） 02:25:51 下午
 * T:“HH:MM:SS”格式（24时制） 14:28:16
 * R:“HH:MM”格式（24时制） 14:28 
 * ***********************************************
 * 
 * @author jerry
 *
 */
public class FormatTest {

	public static void main(String[] args){
		int x = 5;
		double y = 3.1415926;
		//一般方式
		System.out.println("x = " + x + ",y = " + y);//x = 5,y = 3.1415926
		//printf()
		System.out.printf("x = %d, y = %f\n", x, y);//x = 5, y = 3.141593
		//format()等价于printf()
		System.out.format("x = %d, y = %f\n", x, y);//x = 5, y = 3.141593
		
		String name = "jerry";
		int age = 32;
		Formatter format = new Formatter(System.out);
		format.format("My name is %s,my age is %d", name, age);//My name is jerry,my age is 32
		
		System.out.println();
		// - 表示左对齐
		format.format("%-15s %-5s %-10s\n", "windows", "linux", "mac");//windows         linux mac
		format.format("%-15s %-5s %-10s\n", "windows", "windows+linux", "mac");//windows         windows+linux mac
		format.format("%-15s %5d %10.2f\n", "My name is jerry", 5, 4.2);//My name is jerry     5       4.20
		//为正数或者负数添加符号
		format.format("%+d", 15);//+15
		System.out.println();
		//左对齐
		format.format("%-5d", 15);
		System.out.println();
		//数字前面补0
		format.format("%04d", 99);//0099
		System.out.println();
		//以“,”对数字分组
		format.format("%,f", 99999.99);//99,999.990000
		System.out.println();
		//如果是浮点数则包含小数点，如果是16进制或8进制则添加0x或0
		format.format("%#x", 99);//0x63
		System.out.println();
		format.format("%#o", 99);//0143
		System.out.println();
		
		format.format("%tc", new Date());//星期二 七月 04 23:44:18 CST 2017
		
		format.close();
	}
}
