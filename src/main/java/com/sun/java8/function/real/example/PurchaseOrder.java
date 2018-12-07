package com.sun.java8.function.real.example;

import java.util.Date;

public class PurchaseOrder {

	private final Customer customer;
	private final Date date;
	private final OrderStatus status;

	public PurchaseOrder(Customer customer, Date date, OrderStatus status) {
		this.customer = customer;
		this.date = date;
		this.status = status;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public Date getDate() {
		return this.date;
	}

	public OrderStatus getStatus() {
		return this.status;
	}
}
