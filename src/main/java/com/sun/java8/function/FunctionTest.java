package com.sun.java8.function;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.*;

import static org.junit.Assert.assertEquals;

/**
 * 函数式接口 java8包含了很多内建的函数式接口
 * -----------------------------------------------------
 * |name 		| type 			|description 			|
 * -----------------------------------------------------
 * |Consumer  	|Consumer<T> 	|接受T对象,不返回值 		|
 * -----------------------------------------------------
 * |Predicate 	|Predicate<T> 	|接受T对象并返回boolean值 |
 * -----------------------------------------------------
 * |Function  	|Function<T,R>	|接受T对象返回R对象 		|
 * -----------------------------------------------------
 * |Supplier 	| Supplier<T>	|提供T对象不接受输入参数 	|
 * -----------------------------------------------------
 * |UnaryOperator|UnaryOperator |接受T对象返回T对象 		|
 * -----------------------------------------------------
 * |BinaryOperator|BinaryOperator|接受两个T对象返回T对象 	|
 * -----------------------------------------------------
 * 说明：
 * Function： T -> R 输入参数为T，输出参数为R
 * Consumer： T -> void 输入参数T，无输出
 * Supplier： void -> T 无输入参数，输出T
 * Predicate：T -> boolean 输入参数T，输出布尔值
 * 如果输入参数是2个，可以使用：
 * BiFunction<T,U,R> 记作<T,U>-> R
 * BiConsumer<T,U> 记作<T,U> -> void
 * BiPredicate<T,U> 记作<T,U> -> boolean
 * 如果Function的两个输入参数类型相同，可以使用UnaryOperator<T>
 * 如果使用BiFunction时，2个输入参数和输出参数均是同一类型，即BiFunction<T,T>，可以使用BinaryOperator<T>
 * 如果输入参数是基本类型，为来避免自动拆装箱，
 * 可以使用DoubleConsumer|DoubleFunction|DoublePredicate|DoubleSupplier
 * IntConsumer|IntFunction|IntPredicate|IntSupplier
 * LongConsumer|LongFunction|LongPredicate|LongSupplier
 * LongToDoubleFunction|LongToIntFunction|
 * DoubleToLongFunction|DoubleToIntFunction|
 * IntToLongFunction|IntToDoubleFunction| ToIntFunction等
 * <p>
 * 标注为@FunctionalInterface的接口被称为函数式接口 函数式接口（Function interface）定义：
 * 有且仅有一个抽象方法，但是可以有多个非抽象方法的接口。
 * 函数式接口可以被隐式转换为Lamdba表达式。
 * 1.8之前有Runnable、Callable、Comparator、FileFilter等,1.8新增Funcation系列
 * <p>
 * 其他谓词的使用可以参照api 谓词的作用：参考PurchaseOrdersTest.java
 *
 * @author jerry
 */
public class FunctionTest {

    /**
     * Function：接受一定量的参数，同时提供一个返回结果.
     * andThen:先执行apply，再执行after
     * compose:先执行before，在执行apply
     * identity:返回Function f(x) = x
     * <p>
     * 扩展：
     * BiFunction<T,U,R>:接受T&U，返回R类型 R apply(T t, U u) default <V>
     * BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after)
     */
    @Test
    public void testFunction() {
        Function <Integer, Integer> name = e -> e * 2;
        Function <Integer, Integer> square = e -> e + 3;
        int value = name.andThen(square).apply(3);
        System.out.println("Function andThen value = " + value);//9 : 3*2+3
        value = name.compose(square).apply(3);
        System.out.println("Function compose value2 = " + value);//12 : (3+3)*2

        /** UnaryOperator:Function的2个输入参数类型相同 **/
        UnaryOperator <Integer> nameUnaryOperator = e -> e * 2;
        UnaryOperator <Integer> squareUnaryOperator = e -> e + 3;
        value = nameUnaryOperator.andThen(squareUnaryOperator).apply(3);
        System.out.println("UnaryOperator andThen value = " + value);//9 : (3*2)+3
        value = nameUnaryOperator.compose(squareUnaryOperator).apply(3);
        System.out.println("UnaryOperator compose value = " + value);//12 : (3+3)*2

        /** BinaryOperator:Function的2个输入参数和输出参数类型都相同 **/
        BinaryOperator <Integer> nameBinaryOperator = (t, u) -> t + u;
        value = nameBinaryOperator.apply(3, 2);
        System.out.println("BinaryOperator value = " + value);// 5 : 3+2

        Object identity = Function.identity().apply("jerry");// 就像f(x) = x
        System.out.println(identity);// jerry
        /** BiFunction **/
        BiFunction <T, U, R> biFunction = (T t, U u) -> {
            System.out.println(String.format("BiConsumer receive -> %s + %s", t, u));
            return new R();
        };
        biFunction.apply(new T(), new U());// BiConsumer receive -> T + U

        BiFunction <Integer, Integer, String> bf = (t, v) -> String.valueOf(t + v);
        String r = bf.apply(1, 2);
        System.out.println(r);// 3
    }

    /**
     * Predicate<T>：接受T，返回布尔值 该接口用于测试true或false
     */
    @Test
    public void testPredicate() {
        List <Integer> source = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        System.out.println("print all numbers：");
        eval(source, n -> true);
        System.out.println();
        System.out.println("print no numbers:");
        eval(source, n -> false);
        System.out.println();
        System.out.print("所有偶数：");
        eval(source, n -> n % 2 == 0);
        System.out.println();
        System.out.println("输出大于3的数字：");
        eval(source, n -> n > 3);
        System.out.println();
    }

    /*
     * Consumer输入T，无输出
     * 可以理解为赋值操作
     */
    @Test
    public void testConsumer() {
        User user = new User("jerry");
		Consumer<User> consumer = con -> con.setName("sugar");
//         Consumer <User> consumer = new Consumer <User>() {
//             private String name = "sugar";
//
//             @Override
//             public void accept(User t) {
//                 t.setName(name);
//             }
//         };
        consumer.accept(user);
        assertEquals("sugar", user.getName());

        //输入一个参数T
        IntConsumer ic = i -> System.out.println(i);
        ic.accept(10);//10
        ic.accept(8);//8
    }

    /**
     * 一元运算UnaryOperator
     * 接受一个T参数，输出一个T参数值
     */
    @Test
    public void testUnaryOperator() {
        assertEquals(1, UnaryOperator.identity().apply(1));
        assertEquals(true, UnaryOperator.identity().apply(true));
        assertEquals("str", UnaryOperator.identity().apply("str"));
    }

    @Test
    public void testLongBinaryOperator() {
        LongBinaryOperator lbo = (p, v) -> p + v;
        assertEquals(2, lbo.applyAsLong(1L, 1L));
        assertEquals(10, lbo.applyAsLong(5L, 5L));
    }


    private void eval(List <Integer> source, Predicate <Integer> predicate) {
        for(Integer i : source){
            if(predicate.test(i)){
                System.out.print(i + " ");
            }
        }
    }

    static class T {
        @Override
        public String toString() {
            return "T";
        }
    }

    static class U {
        @Override
        public String toString() {
            return "U";
        }
    }

    static class R {
        @Override
        public String toString() {
            return "R";
        }
    }

    static class User {
        private String name;

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
