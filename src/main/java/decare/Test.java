package decare;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.Map.Entry;

public class Test {

	public static void main(String[] args) {

		Map<String, String[]> map = new HashMap<String, String[]>();
		map.put("箱体->尺寸", new String[] { "20寸", "21寸", "22寸", "23寸" });
//		map.put("箱体->材质", new String[] { "铝合金", "钢混", "双金" });
		map.put("箱体->颜色", new String[] { "红色", "灰色", "蓝色" });
		map.put("轮子->尺寸", new String[] { "30寸", "31寸" });

		String[] resultAry = sortGroupDecare(map);

		for (String s : resultAry) {
			System.out.println(s);
		}
	}

	// 笛卡尔组合
	public static String[] sortGroupDecare(Map<String, String[]> map) {
		List<String> returnList = new ArrayList<String>();

		Iterator<Entry<String, String[]>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String[]> entry = iterator.next();
			String[] array = entry.getValue();
			List<String> tmpList = new ArrayList<String>();

			if (returnList.size() > 0) {
				for (String returnStr : returnList) {
					for (String item : array) {
						tmpList.add(returnStr + "----" + item);
					}
				}

				returnList.clear();
				returnList.addAll(tmpList);
			} else {
				returnList.addAll(Arrays.asList(array));
			}
		}
		// 标题
		return new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add(StringUtils.join(map.keySet(), "----"));
				addAll(returnList);
			}
		}.toArray(new String[] {});
	}

	// 全量组合
	public static String[] sortGroup(Map<String, String[]> map) {
		List<String> returnList = new ArrayList<String>();

		Iterator<Entry<String, String[]>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String[]> entry = iterator.next();
			String[] array = entry.getValue();
			List<String> tmpList = new ArrayList<String>();

			if (returnList.size() > 0) {
				for (String returnStr : returnList) {
					for (String item : array) {
						tmpList.add(returnStr + "-" + item);
					}
				}
				returnList.addAll(tmpList);
			}
			returnList.addAll(Arrays.asList(array));
		}

		return returnList.toArray(new String[] {});
	}

}
