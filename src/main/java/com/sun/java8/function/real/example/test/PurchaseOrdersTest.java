package com.sun.java8.function.real.example.test;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.sun.java8.function.real.example.Customer;
import com.sun.java8.function.real.example.OrderStatus;
import com.sun.java8.function.real.example.PurchaseOrder;
import com.sun.java8.function.real.example.PurchaseOrders;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class PurchaseOrdersTest{

	private static final Customer customer1 = new Customer("Jerry");
	private static final PurchaseOrder order1 = 
			new PurchaseOrder(customer1, new GregorianCalendar(2018, 4, 12).getTime(), OrderStatus.PENDING);
	private static final Customer customer2 = new Customer("Tom");
	private static final PurchaseOrder order2 = 
			new PurchaseOrder(customer2, new GregorianCalendar(2018, 5, 12).getTime(), OrderStatus.PENDING);
	private PurchaseOrders orders;
	
	@Before
	public void setUp(){
		orders = new PurchaseOrders();
		orders.add(order1);
		orders.add(order2);
		Assert.assertEquals(2, orders.size());
	}
	
	@Test
	public void test_listOrderOlder(){
		Assert.assertEquals(1, orders.listOrdersByCustomer(customer1).size());
		Assert.assertEquals(1, orders.listRecentOrders(new GregorianCalendar(2018, 4, 18).getTime()).size());
		Assert.assertEquals(0, orders.listRecentOrders(new GregorianCalendar(2019, 4, 18).getTime()).size());
	}
	
	@Test
	public void test_listOrderPredicate(){
		/** 使用预先定义的CustomerPredicate **/
		List<PurchaseOrder> list = orders.listOrders(new CustomerPredicate(customer1));
		Assert.assertEquals(1, list.size());
		assertEquals(order1, list.get(0));
		/** 使用匿名Predicate类 **/
		final Predicate<PurchaseOrder> condition = order -> order.getCustomer().equals(customer1);
		list = orders.listOrders(condition);
		assertEquals(1, list.size());
		assertEquals(order1, list.get(0));
		
		/** 使用guava提供的工具类Collections2.filter实现 **/
		Collection<PurchaseOrder> listC = orders.filterOrders(new CustomerPredicate(customer1));
		Assert.assertEquals(1, listC.size());
		assertEquals(order1, list.get(0));

		listC = orders.filterOrders(order -> order.getCustomer().equals(customer1));
		Assert.assertEquals(1, listC.size());
		assertEquals(order1, list.get(0));

		/** 使用更通用的Iterable.filter实现 **/
		final Iterable<PurchaseOrder> it = orders.selectOrders(new DatePredicate(new GregorianCalendar(2018, 4, 18).getTime()));
		Assert.assertEquals(1, Iterables.size(it));
		Assert.assertEquals(order1, it.iterator().next());
	}
	
	//谓词测试
	@Test
	public void test_predicate(){
		final Predicate<Integer> isZero = Predicates.equalTo(0);
		Assert.assertTrue(isZero.apply(0));
		Assert.assertFalse(isZero.apply(1));
		
		final Predicate<String> isNotNull = Predicates.notNull();
		Assert.assertTrue(isNotNull.apply("jerry"));
		Assert.assertFalse(isNotNull.apply(null));
		
		final Predicate<Object> isString = Predicates.instanceOf(String.class);
		assertTrue(isString.apply("jerry"));
		assertFalse(isString.apply(Integer.MIN_VALUE));
		
		final Predicate<Integer> isNullOrZero = Predicates.or(isZero, Predicates.isNull());
		assertTrue(isNullOrZero.apply(null));
		assertTrue(isNullOrZero.apply(0));
		assertFalse(isNullOrZero.apply(1));
	}
	
	public static final class CustomerPredicate implements Predicate<PurchaseOrder>{

		private final Customer customer;
		
		public CustomerPredicate(Customer customer){
			this.customer = customer;
		}
		
		@Override
		public boolean apply(PurchaseOrder purchaseOrder) {
			return customer.equals(purchaseOrder.getCustomer());
		}
	}
	
	public static final class DatePredicate implements Predicate<PurchaseOrder>{

		private final Date date; 
		
		public DatePredicate(Date date){
			this.date = date;
		}
		
		@Override
		public boolean apply(PurchaseOrder order) {
			return date.after(order.getDate());
		}
		
	}
	
}


