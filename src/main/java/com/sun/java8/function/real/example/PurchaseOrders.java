package com.sun.java8.function.real.example;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import org.apache.curator.shaded.com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class PurchaseOrders{

	private final List<PurchaseOrder> orders = Lists.newArrayList();
	
	//根据用户获取订单信息
	@Deprecated
	public List<PurchaseOrder> listOrdersByCustomer(Customer customer){
		final List<PurchaseOrder> selection = Lists.newArrayList();
		for(PurchaseOrder order : orders){
			if(order.getCustomer().equals(customer)){
				selection.add(order);
			}
		}
		return selection;
	}
	
	//根据日期过滤订单信息
	@Deprecated
	public List<PurchaseOrder> listRecentOrders(Date date){
		final List<PurchaseOrder> selection = Lists.newArrayList();
		for(PurchaseOrder order : orders){
			if(order.getDate().after(date)){
				selection.add(order);
			}
		}
		return selection;
	}
	
	/**
	 * 使用谓词过滤订单信息，上述的传统方式充斥了if的判断语句，而除了if判断外其他逻辑都相同，
	 * 无非就是根据if的判断来决定是否添加到返回结果中
	 * 这里就用到了谓词Predicate的思想，将这些if语句的逻辑抽离到谓词类中
	 **/
	public List<PurchaseOrder> listOrders(Predicate<PurchaseOrder> condition){
		final List<PurchaseOrder> selection = Lists.newArrayList();
		for(PurchaseOrder order : orders){
			if(condition.apply(order))
				selection.add(order);
		}
		return selection;
	}
	
	/** google | apache 提供了模版类
	 * 好坑，Predicate必须引用的是guava **/
	public Collection<PurchaseOrder> filterOrders(Predicate<PurchaseOrder> condition) {
		return Collections2.filter(orders, condition);
	}
	
	public Iterable<PurchaseOrder> selectOrders(Predicate<PurchaseOrder> condition){
		return Iterables.filter(orders, condition);
	}
	
	public boolean add(PurchaseOrder order){
		return orders.add(order);
	}
	
	public int size(){
		return orders.size();
	}
	
	@Override
	public String toString(){
		return "PurchaseOrders : " + orders.size() + " orders.";
	}
}

