package suanfa.btree;

/**
 * 红黑树
 * 
 * 特征：
 * 1.根节点总是黑色的
 * 2.节点是红色或黑色的
 * 3.所有叶子(Nil)都是黑色的
 * 4.如果一个节点是红色的，则它的两个儿子都是黑色的
 * 5.从任一节点到每个叶子节点的所有简单路径都包含相同数目的黑色节点
 * 
 */
public class RBTree<T extends Comparable<T>> {

	private RBNode<T> root;
	private static final boolean RED = false;
	private static final boolean BLACK = true;
	
	public RBTree(){
		root = null;
	}
	
	/**
	 *  左旋操作示意图：
	 *          p				p
	 *        /				  /
	 *       x				 y
	 *      / \				/ \
	 *     lx  y		   x   ry
	 *     	  /	\		  / \	
	 *       ly  ry	     lx  ly
	 *  左旋做了3件事：
	 *  1.将y的左子节点赋给x的右子节点，并将x赋给y左子节点(y左子节点非空时)
	 *  2.将x的父节点(非空时)赋给y的父节点，同时更新p的子节点为y(左或右)
	 *  3.将y的左子节点设为x，将x的父节点设为y
	 *  
	 */
	public void leftRotate(RBNode<T> x){
		//1.将y的左子节点赋给x的右子节点，并将x赋给y左子节点(y左子节点非空时)
		RBNode<T> y = x.right;
		x.right = y.left;
		if(y.left != null)
			y.left.parent = x;
		//2.将x的父节点(非空时)赋给y的父节点，同时更新p的子节点为y
		y.parent = x.parent;
		if(x.parent == null){
			this.root = y;//如果x的父节点为空，则将y设为父节点
		}else{
			if(x == x.parent.left)//如果x时左子节点
				x.parent.left = y;//则将y也设为左子节点
			else
				x.parent.right = y;//否则将y设为右子节点
		}
		//3.将y的左子节点设为x，将x的父节点设为y
		y.left = x;
		x.parent = y;
	}
	
	/**
	 * 右旋示意图：
	 * 			p				p
	 * 		   /			   / 			
	 *        y	  		      x
	 * 	     / \			 / \
	 * 		x   ry			lx  y
	 * 	   / \				   / \
	 *    lx  rx		      rx  ry
	 * 
	 * 右旋的操作：
	 * 1.将x的右子节点赋给y的左子节点，并将y赋给x右子节点的父节点(x右子节点非空时)
	 * 2.将y的父节点p(非空时)赋给x的父节点，同时更新p的子节点为x(左或右)
	 * 3.将x的右子节点设为y，将y的父节点设为x
	 * 
	 */
	public void rightRotate(RBNode<T> y){
		//1.将x的右子节点赋给y的左子节点，并将y赋给x右子节点的父节点(x右子节点非空时)
		RBNode<T> x = y.left;
		y.left = x.right;
		if(x.right != null)
			x.right.parent = y;
		//2.将y的父节点p赋给x的父节点，更新p的子节点为x(x右子节点非空)
		x.parent = y.parent;
		if(y.parent == null){
			this.root = x;//如果y的父节点为空，则将x改为父节点
		}else{
			if(y == y.parent.left){//如果y是左子节点
				y.parent.left = x;//将x也设为左子节点
			}else{
				y.parent.right = x;//否则将x设为右子节点
			}
		}
		//3.将x的右子节点设为y，将y的父节点设为x
		x.right = y;
		y.parent = x;
	}
	
	//插入操作
	public void insert(T key){
		RBNode<T> node = new RBNode<T>(key, RED, null, null, null);
		if(node != null)
			insert(node);
	}
	
	//将节点插入到红黑树中
	private void insert(RBNode<T> node){
		RBNode<T> current = null;//表示最后node的父节点
		RBNode<T> x = this.root; //用来乡下搜索
		
		//1.找到插入位置
		while(x != null){
			current = x;
			int cmp = node.key.compareTo(x.key);
			if(cmp < 0)
				x = x.left;
			else
				x = x.right;
		}
		node.parent = current;//找到了位置，将当前current作为node的父节点
		
		//2.判断node是插入左子节点还是右子节点
		if(current != null){
			int cmp = node.key.compareTo(current.key);
			if(cmp < 0)
				current.left = node;
			else
				current.right = node;
		}else{
			this.root = node;
		}
		
		//3.重新休整为一颗红黑树
		insertFixUp(node);
	}
	
	/**
	 * 因为插入可能会导致树的不平衡(何时着色|左旋|右旋)
	 * 插入情况的几种可能情况：
	 * 1.第一次插入，原树为空，只需要把根节点设为黑色
	 * 2.如果插入节点的父节点是黑色，不会违背规则
	 * 3.插入节点的父节点和其叔叔节点均为红色
	 * 4.插入节点的父节点是红色，叔叔节点是黑色，且插入节点是其父节点的右子节点
	 * 5.插入节点的父节点是红色，叔叔节点是黑色，且插入节点是其父节点的左子节点
	 * 其中3，4，5需要做旋转和变色处理。
	 * 
	 * *******************分析***********************
	 * ******************对3，4，5的处理**************
	 * 图示：带*表示黑色
	 * 				 	11*									11*
	 * 				   /  \    							   /  \
	 * 				  2    14*						      2     14*
	 * 			     / \   / \							 / \    /
	 * 				1*  7* 15  Nil --------------->     1*  7  15	
	 * 			       / \ 							       / \ 	
	 * 			      5    8							  5*  8*
	 * 				 / \  / \							 /
	 * 				❹   Nil  Nil					    4
	 * 情况3:插入节点4的父节点5和其叔叔节点8均为红色
	 * 此时肯定存在祖父节点，但是不知道父节点是其左节点还是右节点(对称性，只需要分析一种情况即可)
	 * 插入时4为红色
	 * 将当前节点4(红色)的父节点5和叔叔节点8涂黑，将祖父节点7涂红；
	 * 再将当前节点指向其祖父节点7，再次从新的当前节点开始算法(此时变成了情况4)
	 * ---------------------------------------------------------------
	 * 情况4:插入节点7的父节点2是红色，叔叔节点14是黑色，且插入节点7是其父节点2的右子节点
	 * 将当前节点7的父节点2做为新的节点，以新的节点为支点做左旋转(此时变成了情况5)
	 * 图示：带*为黑色
	 * 					11*									        11*
	 * 				   /  \										   /  \
	 * 				  2    14*  								  7	   14*
	 * 				 / \   / 									 / \   /
	 * 				1*  ❼ 15       ------------------->  		❷  8*15
	 * 			   	   / \ 									   / \
	 * 			  	  5*  8* 								  1*  5*
	 * 			 	/ 									         /
	 * 			   4  									        4 
	 * ---------------------------------------------------------------
	 * 情况5：插入节点2的父节点7是红色，叔叔节点14是黑色，且插入节点2是其父节点7的左子节点
	 * 将当前节点2的父节点7涂黑，将祖父节点11涂红，以祖父节点11为支点做右旋操作
	 * 最后把根节点涂黑，整个红黑树回复平衡
	 * 图示：带*为黑色
	 * 				   7*
	 * 				  /  \
	 * 				 2    11
	 * 				/ \  /  \
	 *             1* 5* 8*  14*
	 *               /		/
	 * 				4      15
	 * 至此，插入操作完成！
	 * PS:插入节点理论上都是着红色，后续做调整（旋转|着色）
	 * 如果从情况3开始，必然会走完情况4和5；如果从情况4开始，那么再走个5就可以了，
	 * 如果直接是情况5，那么3和4均不需要调整。
	 * ***************************************************************
	 * 
	 * @param node
	 */
	private void insertFixUp(RBNode<T> node){
		RBNode<T> parent, gparent;//定义父节点和祖父节点
		//需要修正的条件：父节点存在且父节点是红色节点
		while((parent = parentOf(node)) != null &&
				isRed(parent)){
			gparent = parentOf(parent);//获取祖父节点
			//若父节点是祖父节点的左子节点
			if(parent == gparent.left){
				RBNode<T> uncle = gparent.right;//获取叔叔节点
				//case3:叔叔节点也是红色节点
				if(uncle != null && isRed(uncle)){
					setBlack(parent);//把父节点和叔叔节点涂黑
					setBlack(uncle);
					setRed(gparent);//把祖父节点涂红
					node = gparent;//将祖父节点设为当前节点
					continue;//继续while
				}
				//case4:叔叔节点是黑色，且当前节点是右子节点
				if(node == parent.right){
					leftRotate(parent);//从父节点处左旋
					RBNode<T> tmp = parent;//然后将父节点和自己调换，为下面的右旋做准备
					parent = node;
					node = tmp;
				}
				//case5:叔叔节点是黑色，且当前节点是左子节点
				setBlack(parent);
				setRed(gparent);
				rightRotate(gparent);
			}else{//若父节点是祖父节点的右子节点，与上面的完全相反，本质是一样的
				RBNode<T> uncle = gparent.left;
				//case3:叔叔节点也是红色  
				if(uncle != null && isRed(uncle)){
					setBlack(parent);
					setBlack(uncle);
					setRed(gparent);
					node = gparent;
					continue;
				}
				//case4:叔叔节点是黑色的，且当前节点是左子节点 
				if(node == parent.left){
					rightRotate(parent);
					RBNode<T> tmp = parent;
					parent = node;
					node = tmp;
				}
				//case5:叔叔节点是黑色的，且当前节点是右子节点  
				setBlack(parent);
				setRed(gparent);
				leftRotate(gparent);
			}
		}
		//将根节点设置为黑色
		setBlack(this.root);
	}
	
	/**
	 * 删除操作
	 * 1.如果待删除节点没有子结点，那么直接删除即可
	 * 2.如果待删除节点只有一个子节点，那么直接删除，并用子节点顶替即可
	 * 3.待删除节点有2个子节点，需要选出后续节点，然后处理“后续节点”和“被删除节点的父节点”的关系，
	 * 最后处理“后续节点的子节点”和“被删除节点的子节点”之间的关系
	 * 
	 * PS：删除节点的关键是
	 * 1.如果删除的节点是红色的，不破坏性质
	 * 2.如果删除的节点是黑色的，那么路径上就少了一个黑色节点，就必须修正
	 * 
	 * @param node
	 * @return
	 */
	public void remove(T key){
		RBNode<T> node;
		if((node = search(root, key)) != null)
			remove(node);
	}
	
	/**
	 * 需要存照后续节点来替换待删除节点，关键点就是如何寻找后续节点
	 * 方式:到“待删除节点”的右子节点的左子节点(如果存在的话)，
	 * 	    然后到这个左子节点的左子节点，以此类推，顺着左子节点一直找下去，
	 *      这个路径下的最后一个左子节点就是待删除节点的替代；
	 *      如果待删除节点的右子节点没有左子节点，那么这个右子节点就是后续
	 *  示意图：
	 *   			34												34
	 *   		   /  \											  /    \
	 *   		  26   72										 26     78
	 *   		      /  \									           /    \
	 *   			 55   91				---->					  55     91
	 *   		    /  \  / \  										 /  \    /  \
	 *             41  61 78 100          						    41   61  89   100
	 *              \      \										  \
	 *               43  	 89											43
	 *  假设要删除节点72，则节点72的右子节点是91，节点91的左子节点是78，
	 *  因为78没有左子节点，所有78就是后续节点(替换节点)
	 *  找到了后续节点78后，需要执行4个步骤：
	 *  步骤1.把后续节点78的父节点的左子节点设为后续节点的右子节点89
	 *  步骤2.把后续节点78的右子节点设为要删除节点的右子节点91
	 *  步骤3.把待删除节点72从它的父节点的左子节点或右子节点删除，把这个字段设为后续
	 *  步骤4.把待删除的左子节点55移除，将后续节点78的左子节点设为待删除节点的左子节点55
	 *  
	 * @param node
	 */
	private void remove(RBNode<T> node){
		RBNode<T> child, parent;
		boolean color;
		//1.被删除节点的左右子节点都不为空
		if(node.left != null && node.right != null){
			//先找到被删除节点的后续节点，用它来取代被删除节点的位置
			RBNode<T> replace = node;
			//1.1 获取后续节点
			replace = replace.right;
			while(replace.left != null){
				replace = replace.left;
			}
			//1.2 处理“后续节点”和“被删除节点的父节点”的关系
			if(parentOf(node) != null){//待删除的节点不是根节点
				//待删除节点是左子节点
				if(node == parentOf(node).left)
					parentOf(node).left = replace;
				else //待删除节点是右子节点
					parentOf(node).right = replace;
			}else{
				this.root = replace; 
			}
			//1.3 处理“后续节点的子节点”和“待删除节点的子节点”之间的关系
			child = replace.right; //后续节点的右子节点(肯定不存在左子节点)
			parent = parentOf(replace);//后续节点的父节点
			color = colorOf(replace);//后续节点的颜色
			if(parent == node){ //后续节点是被删除节点的子节点
				parent = replace;//直接用后续节点替换待删除节点
			}else{
				if(child != null){ //后续节点的右子节点不为空
					setParent(child, parent); //步骤1，将后续节点的右子节点的父节点设为后续节点的父节点
				}
				parent.left = child;//步骤1，将后续节点的父节点的左子节点设为后续节点的右子节点
				replace.right = node.right;//步骤2，将后续节点的右子节点设为待删除节点的右子节点
				setParent(node.right, replace);//步骤3，待删除节点的父节点设为后续节点
			}
			//步骤4，后续节点的父节点设为待删除节点的父节点
			replace.parent = node.parent;
			//步骤4，后续节点的颜色设为待删除节点的颜色
			replace.color = node.color;
			//步骤4，待删除节点的左子节点的父节点设为后续节点
			node.left.parent = replace;
			
			if(color == BLACK) //1.4 如果移走的后续节点颜色是黑色，重新修正红黑树
				removeFixUp(child, parent);//将后续节点的child和parent传进去
		}
	}
	
	/**
	 * remove可能导致红黑树失衡
	 * remove操作在删除某个节点后，会用它的后续节点替换，
	 * 并且后续节点会设置和被删除节点相同的颜色，所以被删除节点的位置不会失衡。
	 * 可能破坏平衡的是后续节点的位置。
	 * 因为后续节点拿走后，原来的位置结构发生了变化。
	 * 
	 * ****************************分析******************************
	 * 删除操作后:
	 * 如果当前节点是黑色的根节点，不用操作；
	 * 如果当前节点是红色的，则被移走的后续节点是黑色的，只要将当前节点设为黑色即可；
	 *
	 * 但对以下4种情况的处理过程：A表示当前节点
	 * 1.当前节点是黑色的，且兄弟节点是红色的(那么父节点和兄弟节点的子节点肯定是黑色的)；
	 * ->将父节点B涂红，将兄弟节点D涂黑，然后将当前节点A的父节点B作为支点左旋
	 * 				B*								 D*
	 * 			   / \								/ \
	 * 			  A*  D			----------->   	   B   E*
	 * 				 / \						  / \    
	 * 				C*  E*						 A*  C*
	 * 2.当前节点是黑色的，且兄弟节点是黑色的，且兄弟节点的2个子节点均为黑色的；
	 * ->将兄弟节点D涂红，将当前节点指向其父节点B，将其父节点指向当前节点的祖父节点，继续新的算法，不需要旋转
	 * 
	 * 				B							    B		
	 * 			   / \							   / \
	 * 			  A*  D*        ----------->      A*  D 
	 * 				 / \ 							 / \
	 * 				C*  E* 							C*  E*
	 * 3.当前节点是黑色的，且兄弟节点是黑色的，且兄弟节点的左子节点是红色的&右子节点是黑色的
	 * ->把当前节点的兄弟节点D涂红，把兄弟节点的左子节点C涂黑，然后以兄弟节点为支点右旋;
	 *   然后兄弟节点就变成黑色的，且兄弟节点的右子节点变成红色（情况4）
	 * 				B							     B
	 * 			   / \								/ \
	 * 			  A*  D* 		------------>	   A*  C*
	 * 				 / \						        \
	 * 				C   E*								 D
	 * 													  \
	 * 													   E*
	 * 4.当前节点是黑色的，且兄弟节点是黑色的，且兄弟节点的右子节点是红色的&左子节点任意颜色	
	 * ->把兄弟节点D涂成父节点的颜色，再把父节点B涂黑，把兄弟节点的右子节点E涂黑，
	 * 	  然后以当前节点的父节点为支点左旋
	 * 				B								   D		
	 * 			   / \								  / \
	 * 			  A*  D*		------------>		 B*  E*
	 * 				 / \							/ \
	 * 			    C   E						   A*  C
	 * 可以看出，从情况1开始，可能是情况2、3、4中的一种；如果是情况2，就不可能再出现情况3和4；
	 * 如果是情况3，必然会导致情况4的出现；如果2和3都不是，必然是4；
	 * 
	 * @param node 表示待修正的子节点，即后续节点的子节点（因为后续节点被挪到删除节点的位置）
	 * @param parent 后续节点的父节点
	 */
	private void removeFixUp(RBNode<T> node, RBTree<T>.RBNode<T> parent) {
		RBNode<T> other;
		while(node == null || (isBlack(node) && node != this.root)){
			if(parent.left == node){ // node是左子节点
				other = parent.right;//other是node的兄弟节点
				//case1:node的兄弟节点other是红色的
				if(isRed(other)){
					setBlack(other);//兄弟节点涂黑
					setRed(parent);//父节点涂红
					leftRotate(parent);
					other = parent.right;//为后面做准备，此时node的兄弟节点是parent.right
				}
				//******************other都是黑色的******************
				//case2:node的兄弟节点other是黑色的，且other的两个子节点也都是黑色的 
				if((other.left == null || isBlack(other.left)) && 
						(other.right == null || isBlack(other.right))){
					setRed(other);
					node = parent;
					parent = parentOf(node); 
				}else{
					//case3:node的兄弟节点other是黑色的，且other的左子节点是红色，右子节点是黑色
					if(other.right == null || isBlack(other.right)){
						setBlack(other.left);
						setRed(other);
						rightRotate(other);
						other = parent.right;
					}
					//case4:node的兄弟节点other是黑色的，且other的右子节点是红色，左子节点任意颜色
					setColor(other, colorOf(parent));
					setBlack(parent);
					setBlack(other.right);
					leftRotate(parent);
					node = this.root;
					break;
				}
			}else{//node是右子节点
				
			}
		}
		if(node != null)
			setBlack(node);
	}
	
	/**********查找最小节点的值**********/
	public T minValue(){
		RBNode<T> node = minNode(root);
		if(node != null)
			return node.key;
		return null;
	} 
	
	private RBNode<T> minNode(RBNode<T> tree){
		if(tree == null){
			return null;
		}
		while(tree.left != null){
			tree = tree.left;
		}
		return tree;
	}
	
	/**********查找最大节点的值**********/
	public T maxValue(){
		RBNode<T> node = maxNode(root);
		if(node != null)
			return node.key;
		return null;
	}

	private RBNode<T> maxNode(RBNode<T> tree){
		if(tree == null)
			return null;
		while(tree.right != null)
			tree = tree.right;
		return tree;
	}
	
	/**
	 * 查找节点x的后续节点，即大于节点x的最小节点
	 * 
	 * @param x
	 * @return
	 */
	public RBNode<T> successor(RBNode<T> x){
		//如果x有右子节点，那么后续节点“以右子节点为根的子树的最小节点”
		if(x.right != null){
			return minNode(x.right);
		}
		/**
		 * 如果x没有右子节点，会出现以下两种情况：
		 * 1.x是其父节点的左子节点，则x的后续节点为它的父节点
		 * 2.x是其父节点的右子节点，则先查找x的父节点p，然后对p再次进行这2个条件的判断
		 */
		RBNode<T> p = x.parent;
		while(p != null && x == p.right){
			x = p;
			p = x.parent;
		}
		return p;
	}
	
	/**
	 * 查找节点x的前驱节点，即小于节点x的最大节点
	 * 
	 * @param x
	 * @return
	 */
	public RBNode<T> presuccessor(RBNode<T> x){
		if(x.left != null)
			return maxNode(x.left);
		RBNode<T> p = x.parent;
		while(p != null && x == p.left){
			x = p;
			p = x.parent;
		}
		return p;
	}
	
	//销毁红黑树
	public void clear(){
		destory(root);
		root = null;
	}
	
	private void destory(RBNode<T> tree){
		if(tree == null)
			return;
		if(tree.left != null)
			destory(tree.left);
		if(tree.right != null)
			destory(tree.right);
		tree = null;
	}
	

    /******************* 打印红黑树 *********************/  
    public void print() {  
        if(root != null) {  
            print(root, root.key, 0);  
        }  
    } 
	
    /* 
     * key---节点的键值 
     * direction--- 0:表示该节点是根节点 
     *              1:表示该节点是它的父节点的左子节点 
     *              2:表示该节点是它的父节点的右子节点 
     */
    private void print(RBNode<T> tree, T key, int direction) {  
        if(tree != null) {  
            if(0 == direction)   
                System.out.printf("%2d(B) is root\n", tree.key);  
            else  
                System.out.printf("%2d(%s) is %2d's %6s child\n",   
                        tree.key, isRed(tree)?"R":"b", key, direction == 1?"right":"left");  
            print(tree.left, tree.key, -1);  
            print(tree.right, tree.key, 1);  
        }  
    }  
	
	//获取父节点
	public RBNode<T> parentOf(RBNode<T> node){
		return node != null ? node.parent : null;
	}
		
	//设置父节点
	public void setParent(RBNode<T> node, RBNode<T> parent){
		if(node != null)
			node.parent = parent;
	}
		
	//获取节点的颜色
	public boolean colorOf(RBNode<T> node){
		return node != null ? node.color : BLACK;
	}
		
	//设置节点的颜色
	public void setRed(RBNode<T> node){
		if(node != null)
			node.color = RED;
	}
		
	public void setBlack(RBNode<T> node){
		if(node != null)
			node.color = BLACK;
	}
		
	//判断节点的颜色(是否红色)
	public boolean isRed(RBNode<T> node){
		return (node != null) && (node.color == RED) ? true : false; 
	}
		
	public boolean isBlack(RBNode<T> node){
		return !isRed(node); 
	}
		
	public void setColor(RBNode<T> node, boolean color){
		if(node != null)
			node.color = color;
	}
		
	//查找红黑树中键值为key的节点
	public RBNode<T> search(T key){
		return search(root, key);
//		return search2(root, key);//递归方式
	}
		
	private RBNode<T> search(RBNode<T> x, T key){
		while(x != null){
			int cmp = key.compareTo(x.key);
			if(cmp < 0)
				x = x.left;
			else if(cmp > 0)
				x = x.right;
			return x;
		}
		return x;
	}
		
	//使用递归  
	private RBNode<T> search2(RBNode<T> x, T key){
		if(x == null)
			return x;
		int cmp = key.compareTo(x.key);
		if(cmp < 0)
			return search2(x.left, key);
		else if(cmp > 0)
			return search2(x.right, key);
		else
			return x;
	}
	
	@SuppressWarnings("hiding")
	public class RBNode<T extends Comparable<T>> {
		boolean color;// 颜色
		T key;// 关键字(数值)
		RBNode<T> left;// 左子节点
		RBNode<T> right;// 右子节点
		RBNode<T> parent;// 父节点

		public RBNode(T key, boolean color, RBNode<T> parent, RBNode<T> left, RBNode<T> right) {
			this.key = key;
			this.color = color;
			this.parent = parent;
			this.left = left;
			this.right = right;
		}
		
		public T getKey() {
			return key;
		}

		public String toString() {
			return "" + key + (this.color == RED ? "R" : "B");
		}
	}

}
