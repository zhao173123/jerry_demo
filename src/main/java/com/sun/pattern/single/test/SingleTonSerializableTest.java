package com.sun.pattern.single.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.sun.pattern.single.SingleTonSerializable;

/**
 * 单列模式静态类实现的序列化和反序列化测试
 * 当单列模式实现了readResolve接口的时候，多线程安全就得到了保证
 * 
 * @author jerry
 *
 */
public class SingleTonSerializableTest {

	public static void main(String[] args) {
		SingleTonSerializable singleTon = SingleTonSerializable.getInstance();
		File file = new File("/Users/jerry/serializable.txt");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(singleTon);
			fos.close();
			oos.close();
			System.out.println(singleTon.hashCode());//1028566121
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try{
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			SingleTonSerializable rSingleTon = (SingleTonSerializable) ois.readObject();
			fis.close();
			ois.close();
			System.out.println(rSingleTon.hashCode());//455659002
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}

}
