package com.sun.spring.mongo;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Lists;
import com.sun.spring.mongo.vo.User;

public class QueryApp {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
		MongoOperations mongoOperations = (MongoOperations) ctx.getBean("mongoTemplate");
		testUse(mongoOperations);
	}

	private static void testUse(MongoOperations mongoOperations) {
		List<User> users = Lists.newArrayList();
		User user1 = new User("1001", "ant", 10);
		User user2 = new User("1002", "bird", 20);
		User user3 = new User("1003", "cat", 30);
		User user4 = new User("1004", "dog", 40);
		User user5 = new User("1005", "elephant", 50);
		User user6 = new User("1006", "frog", 60);
		users.add(user1);
		users.add(user2);
		users.add(user3);
		users.add(user4);
		users.add(user5);
		users.add(user6);
		mongoOperations.insert(users, User.class);

		System.out.println("Case 1 - find with BasicQuery example");

		BasicQuery query1 = new BasicQuery("{age:{$lt:40},name:'cat'}");
		User userTest1 = mongoOperations.findOne(query1, User.class);
		System.out.println("query1 - " + query1.toString());
		System.out.println("userTest1-" + userTest1);
		Query query2 = new Query();
		query2.addCriteria(Criteria.where("name").is("dog").and("age").is(40));
		User userTest2 = mongoOperations.findOne(query2, User.class);
		System.out.println("query2 - " + query2.toString());
		System.out.println("userTest2 - " + userTest2);

		List<Integer> listOfAge = Lists.newArrayList();
		listOfAge.add(10);
		listOfAge.add(40);
		Query query3 = new Query();
		query3.addCriteria(Criteria.where("age").in(listOfAge));
		List<User> userTest3 = mongoOperations.find(query3, User.class);
		System.out.println("query3 - " + query3.toString());
		for (User user : userTest3) {
			System.out.println("userTest3 - " + user);
		}
		System.out.println("\nCase 4 find list $and $lt,$gt example");
		Query query4 = new Query();
		query4.addCriteria(Criteria.where("age").exists(true).andOperator(Criteria.where("age").gt(10),
				Criteria.where("age").lt(40)));
		List<User> userTest4 = mongoOperations.find(query4, User.class);
		for(User user : userTest4){
			System.out.println("userTest4: " + user);
		}
		
		System.out.println("\nCase 5 - find list and sorting example");
		Query query5 = new Query();
		query5.addCriteria(Criteria.where("age").gte(30));
		query5.with(new Sort(Sort.Direction.DESC, "age"));
		List<User> userTest5 = mongoOperations.find(query5, User.class);
		for(User user : userTest5){
			System.out.println("userTest5: " + user);
		}
		
		System.out.println("\nCase 6 -find list and sorting example");
		Query query6 = new Query();
		query6.addCriteria(Criteria.where("name").regex("D.*G", "i"));
		List<User> userTest6 = mongoOperations.find(query6, User.class);
		System.out.println("query6 - " + query6.toString());
		for(User user : userTest6){
			System.out.println("userTest6 : " + user);
		}
		
//		mongoOperations.dropCollection(User.class);
	}
	
	public static final String menuJson = 
	"{'moduleCode':'management001','moduleName':'管理','level':'1','url':'','menuList':["+
	"{'resourceCode':'structure001','resourceName':'组织架构','level':'2','url':'','menuList':["+
	"{'resourceCode':'organization001','resourceName':'组织','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'position001','resourceName':'职位','url':'','level':'3','menuList':[]}]},"+
	"{'resourceCode':'employeeManagement001','resourceName':'员工管理','url':'','level':'2','menuList':[]},"+
	"{'resourceCode':'authManagement001','resourceName':'权限管理','url':'','level':'2','menuList':[]},"+
	"{'resourceCode':'systemSettings001','resourceName':'系统设置','url':'','level':'2','menuList':["+
	"{'resourceCode':'addressManagement001','resourceName':'地址管理','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'baseData001','resourceName':'基础数据','url':'','level':'3','menuList':[]}]}]},"+
	"{'moduleCode':'approval001','moduleName':'审批','url':'','level':'1','menuList':["+
	"{'resourceCode':'approvalFiling','resourceName':'审批归档','url':'','level':'2','menuList':[]},"+
	"{'resourceCode':'approvalManagement001','resourceName':'审批表管理','url':'','level':'2','menuList':[]},"+
	"{'resourceCode':'recycleBin','resourceName':'回收站','url':'','level':'2','menuList':[]}]},"+
	"{'moduleCode':'checkingIn001','moduleName':'考勤','url':'','level':'1','menuList':["+
	"{'resourceCode':'checkingInd','resourceName':'考勤明细','url':'','level':'2','menuList':[]},"+
	"{'resourceCode':'checkingInReport001','resourceName':'考勤报表','url':'','level':'2','menuList':[]},"+
	"{'resourceCode':'vacationManagement001','resourceName':'假期管理','url':'','level':'2','menuList':[]},"+
	"{'resourceCode':'checkingInSettings001','resourceName':'考勤设置','url':'','level':'2','menuList':[]}]},"+
	"{'moduleCode':'ec001','moduleName':'电商','url':'','level':'1','menuList':["+
	"{'resourceCode':'homePage001','resourceName':'中心首页','url':'','level':'2','menuList':[]},"+
	"{'resourceCode':'shopManagement001','resourceName':'商品管理','url':'','level':'2','menuList':["+
	"{'resourceCode':'shopMaterial001','resourceName':'商品物料库','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'shopList001','resourceName':'商品列表','url':'','level':'3','menuList':[]}]},"+
	"{'resourceCode':'purchaseManagement001','resourceName':'销售管理','url':'','level':'2','menuList':["+
	"{'resourceCode':'purchaseOrderManagement001','resourceName':'销售订单管理','url':'','level':'3','menuList':[]}]},"+
	"{'resourceCode':'saleManagement001','resourceName':'采购管理','url':'','level':'2','menuList':["+
	"{'resourceCode':'saleOrderManagement001','resourceName':'采购订单管理','url':'','level':'3','menuList':[]}]}]},"+
	"{'moduleCode':'supplyChain001','moduleName':'供应链','level':'1','menuList':["+
	"{'resourceCode':'inventoryManagement001','resourceName':'库存管理','url':'','level':'2','menuList':["+
	"{'resourceCode':'inStorageManagement001','resourceName':'入库管理','url':'','level':'3','menuList':["+
	"{'resourceCode':'saleInStorage001','resourceName':'采购入库单','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'purchaseReturnStorage001','resourceName':'销售退货入库单','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'productionInStorage001','resourceName':'生产入库单','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'productionReturnStorage001','resourceName':'生产退料单','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'allotInStorage001','resourceName':'调拨入库单','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'otherInStorage001','resourceName':'其他入库单','url':'','level':'4','menuList':[]}]},"+
	"{'resourceCode':'outerStorageManagement001','resourceName':'出库管理','url':'','level':'3','menuList':["+
	"{'resourceCode':'purchaseOuterStorage001','resourceName':'销售出库单','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'saleOuterStorage001','resourceName':'采购退货出库单','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'productionOuterStorage001','resourceName':'生产发料单','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'allotOuterStorage001','resourceName':'调拨出库单','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'otherOuterStorage001','resourceName':'其他出库单','url':'','level':'4','menuList':[]}]},"+
	"{'resourceCode':'allotManagement001','resourceName':'调拨管理','url':'','level':'3','menuList':["+
	"{'resourceCode':'allotPlan001','resourceName':'调拨计划单','url':'','level':'4','menuList':[]}]},"+
	"{'resourceCode':'storageAdjustment001','resourceName':'库存调整','url':'','level':'3','menuList':["+
	"{'resourceCode':'positionAdjustment001','resourceName':'仓位调整单','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'numAdjustment001','resourceName':'数量调整单','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'batchAdjustment001','resourceName':'批次调整单','url':'','level':'4','menuList':[]}]},"+
	"{'resourceCode':'checkInventory001','resourceName':'库存盘点','url':'','level':'3','menuList':["+
	"{'resourceCode':'checkPlan001','resourceName':'盘点作业单','url':'','level':'4','menuList':[]}]},"+
	"{'resourceCode':'inventorySearch001','resourceName':'库存查询','url':'','level':'3','menuList':["+
	"{'resourceCode':'inTimeInventory001','resourceName':'即时库存','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'inventoryDetail001','resourceName':'库存明细账','url':'','level':'4','menuList':[]}]},"+
	"{'resourceCode':'baseSettings001','resourceName':'基础设置','url':'','level':'3','menuList':["+
	"{'resourceCode':'storageSite001','resourceName':'站点','url':'','level':'4','menuList':[]},"+
	"{'resourceCode':'storagePosition001','resourceName':'库位','url':'','level':'4','menuList':[]}]}]},"+
	"{'resourceCode':'storageSaleManagement001','resourceName':'采购管理','url':'','level':'2','menuList':["+
	"{'resourceCode':'storageSupplier001','resourceName':'供应商','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'storageSaleOrder001','resourceName':'采购订单','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'storageSaleReturn001','resourceName':'采购退货单','url':'','level':'3','menuList':[]}]},"+
	"{'resourceCode':'storagePurchaseManagement001','resourceName':'销售管理','url':'','level':'2','menuList':["+
	"{'resourceCode':'customer001','resourceName':'客户','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'storagePriceList001','resourceName':'销售价格清单','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'storagePurchaseOrder001','resourceName':'销售订单','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'storagePurchaseReturn001','resourceName':'销售退货单','url':'','level':'3','menuList':[]}]},"+
	"{'resourceCode':'productionManagement001','resourceName':'生产管理','url':'','level':'2','menuList':["+
	"{'resourceCode':'productionOrderManagement001','resourceName':'生产订单管理','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'productionGetMaterialApply001','resourceName':'生产领料申请单','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'productionReturnMaterialApply001','resourceName':'生产退料申请单','url':'','level':'3','menuList':[]}]},"+
	"{'resourceCode':'mainData001','resourceName':'主数据','url':'','level':'2','menuList':["+
	"{'resourceCode':'material001','resourceName':'物料','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'businessPartner001','resourceName':'商业伙伴','url':'','level':'3','menuList':[]},"+
	"{'resourceCode':'BOM001','resourceName':'BOM','url':'','level':'3','menuList':[]}]}]},"+
	"{'moduleCode':'APS001','moduleName':'APS','url':'','level':'1','menuList':[]}";
	
}

