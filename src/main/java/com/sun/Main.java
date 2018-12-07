package com.sun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.sun.json.vo.Book;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	
	private static byte[] block = new byte[0];

	public static void main(String[] args) throws InterruptedException {
//		String result1 = StringUtils.join(new String[] { "a", "c", "d", "e" }, "|");
//		System.out.println("result1:" + result1);// result1:a|c|d|e
//		String result2 = StringUtils.join(Arrays.asList("a", "b", "1"), '|');
//		System.out.println("result2:" + result2);// result2:a|b|1
//
//		// isEmpty
//		System.out.println(StringUtils.isEmpty(null));// true
//		System.out.println(StringUtils.isEmpty(""));// true
//		System.out.println(StringUtils.isEmpty(" "));// false
//
//		System.out.println(StringUtils.isNotEmpty(null));// false
//		// trim:去掉字符串两端的空格
//		System.out.println(StringUtils.trim(null));// null
//		System.out.println(StringUtils.trimToEmpty(null));
//		System.out.println(StringUtils.trimToNull(null));// null
//
//		System.out.println(StringUtils.equals("ac", "ac"));// true
//		System.out.println(StringUtils.equals(null, null));// true
//
//		Long aa = 19l;
//		System.out.println(aa.toString());
//
//		BigDecimal b = new BigDecimal(0);
//		System.out.println(b.doubleValue());
//
//		System.out.println("result : " + Math.min(1, 2));// 1
//		System.out.println("result : " + Math.max(1, 2));// 2
//
//		System.out.println(5 - 2 <= 1);// false
//
//		System.out.println("...common months name...");
//		String[] months = new SimpleDateFormat().getDateFormatSymbols().getMonths();
//		// January February March April May June July August September October
//		// November December
//		for (int i = 0; i < months.length; i++) {
//			System.out.print(months[i] + " ");
//		}
//		System.out.println("");
//		System.out.println("...short months name...");
//		months = new SimpleDateFormat().getDateFormatSymbols().getShortMonths();
//		// Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec
//		for (int i = 0; i < months.length; i++) {
//			System.out.print(months[i] + " ");
//		}
//		System.out.println("");
//		// Sunday Monday Tuesday Wednesday Thursday Friday Saturday
//		String[] weeks = new SimpleDateFormat().getDateFormatSymbols().getWeekdays();
//		for (int i = 0; i < weeks.length; i++) {
//			System.out.print(weeks[i] + " ");
//		}
//		System.out.println();
//		// Sun Mon Tue Wed Thu Fri Sat
//		weeks = new SimpleDateFormat().getDateFormatSymbols().getShortWeekdays();
//		for (int i = 0; i < weeks.length; i++) {
//			System.out.print(weeks[i] + " ");
//		}
//
//		System.out.println("--------------------------");
//		System.out.println(isNumber("11231319.22"));// true
//		System.out.println(isNumber("19.22"));// true
//		System.out.println(isNumber("19.2"));// true
//		System.out.println(isNumber("9.2"));// true
//		System.out.println(isNumber("9.22"));// true
//		System.out.println(isNumber("0.91"));// true
//		System.out.println(isNumber("00.91"));// false
//		System.out.println(isNumber("19.2223231"));// false
//		System.out.println(isNumber("啊"));// false
//		System.out.println(isNumber("。"));// false
//
//		int sourceInt = 8;
//		byte[] result = intToBytes(sourceInt);
//		for(byte i : result){
//			System.out.print(i + "");
//		}
//		System.out.println("");
//		
//		String a = Integer.toBinaryString(sourceInt);
//		byte[] a1 = a.getBytes();
//		for(int i = 0; i < a1.length; i++){
//			System.out.print(a1[i] + "");
//		}
//		
//		System.out.println("exception before...");
////		if(1 == 1)
////			throw new RuntimeException();
////		System.out.println("exception after : " + 1);
//		synchronized(block){
//			Thread.sleep(2000);
//		}
//		//会等待2s才执行下面的操作
//		System.out.println("synchronized after....");
//		
//		AtomicInteger atomicI = new AtomicInteger(0);
//		System.out.println(atomicI.compareAndSet(atomicI.get(), 1));//true
//		
//		BigDecimal rent = new BigDecimal(100.23);
//		String rentStr = rent.toString();
//		System.err.println("rentStr : " + rentStr);
////		rentStr = rentStr.substring(0, rentStr.indexOf(".") - 1);
////		System.err.println("rentStr : " + rentStr);
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
//		Date publishTime;
//		try {
//			publishTime = df.parse("2018-06-21 10:00:00");
//			Long publishTimeUnix = publishTime.getTime();
//			Long currentTimeUnix = System.currentTimeMillis();
//			Long diff = currentTimeUnix - publishTimeUnix;
//			long days = diff / (1000 * 60 * 60 * 24);  
//		    long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);  
//		    long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);  
//		    System.out.println(""+days+"天"+hours+"小时"+minutes+"分");  
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		String accessToken = 
//				"11_nrUuuloL3mFYsa93r0k_d3B7Fd6PyBhBG1TrNrw1ID0isDQv8Hph3fBTvptN9WSrAjCQVrBu-4xVpH7BL4-8BIUEe-TVx2eiT12nyaldL3IosJuthaAbVeLicOBpdKszSQ_CsUMcXY2eIyr9UXJiAAAFIU";
//		getminiqrQr("111", accessToken);
//		wxpic("111", accessToken);
// 		List<String> list = Lists.newArrayList("1");
// 		String removeStr = list.get(0);
// 		Iterator<String> iter = list.iterator();
// 		while(iter.hasNext()){
// 			if(iter.next().equals(removeStr)){
// 				iter.remove();
// 			}
// 		}
// 		System.out.println(list);
// 		list.forEach(item->{
// 			System.out.print(item);
// 		});

//        BigDecimal bd = new BigDecimal(10.00);
//        System.out.println(bd.toString());

//        String zifu001 = "Nesta";
//        System.out.println(zifu001.length());//5
//        System.out.println(zifu001.getBytes().length);//5
//        zifu001 = "我就喜欢我啊";
//        System.out.println(zifu001.length());//4
//        System.out.println(zifu001.getBytes().length);//12
//        System.out.println(zifu001.substring(0,4));
//        zifu001 = "我就喜欢01";
//        System.out.println(zifu001.length());//6
//        System.out.println(zifu001.getBytes().length);//14
//        zifu001 = "我就喜欢，01";
//        System.out.println(zifu001.length());//7
//        System.out.println(zifu001.getBytes().length);//17
//        String result = zifu001.substring(0, 5);
//        System.out.println(result);

//        List<String> roommateTagQueryDTOS = Lists.newArrayList();
//        roommateTagQueryDTOS.add("不养宠物");
//        roommateTagQueryDTOS.add("爱做饭");
//        roommateTagQueryDTOS.add("是个学霸是个");
//        StringBuilder sb = new StringBuilder();
//        sb.append("室友标准：");
//        for(int i = 0; i < roommateTagQueryDTOS.size(); i++){
//            sb.append(roommateTagQueryDTOS.get(i)).append("、");
//        }
//        String roommateDesc = sb.substring(0, sb.lastIndexOf("、"));
//        System.out.println(roommateDesc.length());
//        if(sb.length() > 20){
//            roommateDesc = sb.substring(0, 20) + "...";
//        }
//        System.out.println(roommateDesc);

        // int tail = 1;
        // int b = 0;
        // boolean b1 = b != (b = tail);
        // System.out.println(b1);//true
        // int c = (b != (b = tail)) ? 1 : 2;
        // System.out.println("b = " + b + ",c = " + c);//b = 1,c = 1
        // b = tail;
        // b1 = b != tail;
        // System.out.println(b1);//false

        int owner = 1;
        int renter =  1;
        short x = 0;

        System.out.println(1 << 1); //2
        System.out.println(2 << 1); //4

        System.out.println(2<<4);//32
        System.out.println((2<<4) | 1);//33
        System.out.println((2<<4) | 4);//36

        int ceil0 = 4;
        int ceil1 = 20;
        // BigDecimal ceil2 = new BigDecimal((float)ceil0/ceil1);
        // System.out.println("" + ceil2 + "," + ceil2.compareTo(new BigDecimal(0.2)));
        float ceil2 = (float)ceil0/ceil1;
        if(ceil2 >= 0.6)
            System.out.println("haha" + ceil2); //0.2
        else
            System.out.println("hehe : " + ceil2);
	}

	private static Map getminiqrQr(String sceneStr, String accessToken) {
        RestTemplate rest = new RestTemplate();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
        	String url = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="+accessToken;
            Map<String,Object> param = new HashMap<>();
            param.put("scene", sceneStr);
//            param.put("page", "pages/index/index");
            param.put("width", 430);
            param.put("auto_color", false);
            Map<String,Object> line_color = new HashMap<>();
            line_color.put("r", 0);
            line_color.put("g", 0);
            line_color.put("b", 0);
            param.put("line_color", line_color);
            String jsonString = JSON.toJSONString(param);
            //okhttp
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json;charset-utf-8");
            RequestBody body = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder()
    				.url(url)
    				.post(body)
    				.build();
    		Response response = client.newCall(request).execute();

    		byte[] result = response.body().bytes();
    		String message = response.message();
    		System.out.println("message : " + message);
//            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//            HttpEntity requestEntity = new HttpEntity(param, headers);
//            ResponseEntity<byte[]> entity = rest.exchange(url, HttpMethod.POST, requestEntity, byte[].class, new Object[0]);
////            LOG.info("调用小程序生成微信永久小程序码URL接口返回结果:" + entity.getBody());
//            byte[] result = entity.getBody();
//            LOG.info(org.apache.commons.codec.binary.Base64.encodeBase64String(result));
            inputStream = new ByteArrayInputStream(result);
            Thread.sleep(2000);
            File file = new File("/Users/jerry/myself/1.png");
            if (!file.exists()){
                file.createNewFile();
            }
            System.out.println(file.getPath());
            outputStream = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = inputStream.read(buf, 0, 1024)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.flush();
            
        } catch (Exception e) {
//            LOG.error("调用小程序生成微信永久小程序码URL接口异常",e);
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
	
	public static void wxpic(String scene, String accessToken){
		try
        {
            URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            JSONObject paramJson = new JSONObject();
            paramJson.put("scene", scene);
//            paramJson.put("page", "pages/index/index");
            paramJson.put("width", 430);
            paramJson.put("auto_color", true);

            printWriter.write(paramJson.toString());
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            try(OutputStream os = new FileOutputStream(new File("/Users/jerry/myself/abc.png"))){
                int len;
                byte[] arr = new byte[1024];
                while((len = bis.read(arr)) != -1){
                    os.write(arr, 0, len);
                    os.flush();
                }
                os.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
	}
	
	public static byte[] intToBytes(int n) {
		byte[] b = new byte[32];
		int index = 0;
		for (int i = 31; i >= 0; i--){
			b[index++] = (byte) (n >>> i & 1);
		}
		return b;
	}

	public static boolean isNumber(String source) {
		Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*||[0]{1}))(\\.(\\d){0,2})?$");
		Matcher matcher = pattern.matcher(source);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	@Test
	public void testMethodInvoke() throws IntrospectionException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Book book = new Book();
		PropertyDescriptor pd = new PropertyDescriptor("isbn10", Book.class);
		Method methodSetter = pd.getWriteMethod();
		methodSetter.invoke(book, "123");
//		pd = new PropertyDescriptor("isbn10", Book.class);
		Method methodGetter = pd.getReadMethod();
		String isbn10 = (String) methodGetter.invoke(book);
		System.out.println(isbn10);
		
		try {
			Field field = Book.class.getDeclaredField("isbn10");
			pd = new PropertyDescriptor(field.getName(), Book.class);
			System.out.println(pd);
			if(pd != null){
				methodSetter = pd.getWriteMethod();
				methodSetter.invoke(book, "345");
			}	
			System.out.println(pd.getReadMethod().invoke(book));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String start = sdf.format(calendar.getTime());
        System.out.println(start);
	}
	
	@Test
	public void testListAdd(){
		List<String> list = Lists.newArrayList("1", "2", "3");
		list.add("4");
		System.out.println(list.toString());
	}


	@Test
    public void testSortList(){
	    List<Person> list = Lists.newLinkedList();
	    Person p1 = new Person("p1", 1, "2018-08-02 00:00:00");
        Person p2 = new Person("p2", 0, "2018-08-02 00:00:00");
        Person p3 = new Person("p3", 1, "2018-08-01 00:00:00");
        Person p4 = new Person("p4", 0, "2018-08-12 00:00:00");
        Person p5 = new Person("p5", 1, "2018-08-10 00:00:00");
        Person p6 = new Person("p6", 1, "2018-08-30 00:00:00");
        Person p7 = new Person("p7", 0, "2018-08-11 00:00:00");
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        list.add(p6);
        list.add(p7);
        //理论结果应该是[p6 p5 p1 p3 p4 p7 p2]
        Collections.sort(list, new Comparator<Person>(){
            @Override
            public int compare(Person o1, Person o2){
                int cr = 0;
                int age1 = o1.getAge();
                int age2 = o2.getAge();
                int a = age2 - age1;
                if(a != 0){
                    cr = (a > 0) ? 3 : -1;
                }else{
                    a = o2.getTime().compareTo(o1.getTime());
                    if(a != 0){
                        cr = (a > 0) ? 2 : -2;
                    }
                }
                return cr;
            }
        });
        list.forEach(item -> System.out.print(item.getName() + " "));
    }

    static class Person{

	    private String name;
	    private Integer age;
	    private String time;

	    public Person(){

        }

        public Person(String name, Integer age, String time){
	        this.name = name;
	        this.age = age;
	        this.time = time;
        }

        public Integer getAge(){
            return age;
        }

        public void setAge(Integer age){
            this.age = age;
        }

        public String getTime(){
            return time;
        }

        public void setTime(String time){
            this.time = time;
        }

        public String getName(){
            return name;
        }

        public void setName(String name){
            this.name = name;
        }
    }
}
