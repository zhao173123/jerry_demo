package annotation;

public class TestMM {

	@Boy(name = "jerry")
	private String boy1;

	@Boy(name = "tom")
	private String boy2;

	@MM(name = "sugar", order = 1)
	private void moMM(String boyName, String mmName) {
		System.out.println(boyName + "," + mmName);
	}

	@MM(name = "cora", order = 2)
	private void mmSay(String boyName, String mmName) {
		if (boyName.equals("jerry")) {
			System.out.println(mmName + " say 'hello jerry!'");
		} else if (boyName.equals("tom")) {
			System.out.println(mmName + " say 'hello tom!'");
		}
	}

	public String getBoy1() {
		return this.boy1;
	}

	public String getBoy2() {
		return this.boy2;
	}
}
