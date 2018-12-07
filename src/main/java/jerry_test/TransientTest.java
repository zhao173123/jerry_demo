package jerry_test;

import java.io.*;

/**
 * 1.变量被transient修饰，则变量不再是对象持久化的一部分，该变量内容在序列化后无法获得；
 * 2.transient只能修饰变量，不能修饰方法和类；本地变量也无法被transient修饰；
 *   变量如果是用户自定义类变量，则该类需要implements Serializable
 * 3.被transient关键字修饰的变量不能再被序列化，一个静态变量不管是否被transient修饰，均不能被序列化
 *
 * PS:
 * java中，对象的序列化可以通过两种接口来实现，如果实现的是Serializable接口，则所有的序列化将自动进行；
 * 如果实现的是Externalizable接口，则没有任何东西可以自动序列化，而必须在writeExternal方法中手动指定所要序列化的内容，
 * 此时与变量是否被transient修饰无关。
 *
 */
public class TransientTest {

    public static void main(String[] args) {
        User user = new User();
        user.setUserName("jerry");
        user.setPassword("123");

        System.out.println("read before Serializable: ");
        System.out.println("username: " + user.getUserName());
        System.out.println("password: " + user.getPassword());

        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("/Users/jerry/1.txt"));
            os.writeObject(user);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream("/Users/jerry/1.txt"));
            user = (User) is.readObject();
            is.close();

            System.out.println("read after Serializable: ");
            System.out.println("username: " + user.getUserName());
            System.out.println("password: " + user.getPassword());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}

class User implements Serializable {

    private static final long serialVersionUID = -5922951055002524837L;

    private String userName;
    private transient String password;

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {

        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }
}