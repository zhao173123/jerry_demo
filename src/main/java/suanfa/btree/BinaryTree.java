package suanfa.btree;

public class BinaryTree {

	private TreeNode root = null;

	public BinaryTree() {
		this.root = null;
	}

	/**
	 * 查找 树深（N）O（lgN） 
	 * 1.从结点root开始 
	 * 2.比当前结点值小，则找其左结点 
	 * 3.比当前结点值大，则找其右结点
	 * 4.与当前结点值相同，返回 
	 * 5.未找到
	 * 
	 * @param key
	 * @return
	 */
	public TreeNode search(int key) {
		TreeNode current = root;
		while (current != null && key != current.value) {
			if (key < current.value) {
				current = current.left;
			} else
				current = current.right;
		}
		return current;
	}

	/**
	 * 插入 
	 * 1.从root开始 
	 * 2.如果root为空，root为插入值 
	 * 循环： 
	 * 	3.如果当前结点值大于插入值，找左结点
	 * 	4.如果当前结点值小于插入值，找右结点
	 *
	 * @param key
	 * @return
	 */
	public TreeNode insert(int key) {
		TreeNode newNode = new TreeNode(key);// 新增结点
		TreeNode current = root;// 当前结点
		TreeNode parent = null;// 上个结点
		if (current == null) {// 如果根结点为空
			root = newNode;
			return newNode;
		}
		while (true) {
			parent = current;
			if (key < current.value) {
				current = current.left;
				if (current == null) {
					parent.left = newNode;
					return newNode;
				}
			} else {
				current = current.right;
				if (current == null) {
					parent.right = newNode;
					return newNode;
				}
			}
		}
	}
	
	/**
	 * 删除结点
	 * 1.找到删除结点
	 * 2.如果删除结点左结点为空，右结点也为空
	 * 3.如果删除结点只有一个子结点（右结点或左结点）
	 * 4.如果删除结点左右结点都不为空
	 * @param key
	 * @return
	 */
	public TreeNode delete(int key){
		TreeNode parent = root;
		TreeNode current = root;
		boolean isLeftChild = false;
		//找到删除的结点以及是否在左子树
		while(current.value != key){
			parent = current;
			if(current.value > key){
				isLeftChild = true;
				current = current.left;
			}else{
				isLeftChild = false;
				current = current.right;
			}
			if(current == null){
				return current;	
			}
		}
		//如果删除结点的左结点为空，右结点也为空
		if(current.left == null && current.right == null){
			if(current == root){
				root = null;
			}
			if(isLeftChild == true){
				parent.left = null;
			}else{
				parent.right = null;
			}
		}else if(current.right == null){//如果删除结点只有一个子结点（左结点或者右结点）
			if(current == root){
				root = current.left;
			}else if(isLeftChild){
				parent.left = current.left;
			}else{
				parent.right = current.left;
			}
		}else if(current.left == null){
			if(current == root){
				root = current.right;
			}else if(isLeftChild){
				parent.left = current.right;
			}else{
				parent.right = current.right;
			}
		}
		//如果删除结点的左右子结点都不为空
		else if(current.left != null && current.right != null){
			// 找到删除节点的后继者
            TreeNode successor = getDeleteSuccessor(current);
            if (current == root) {
                root = successor;
            } else if (isLeftChild) {
                parent.left = successor;
            } else {
                parent.right = successor;
            }
            successor.left = current.left;
		}
		return current;
	}
	
	/**
     * 获取删除节点的后继者
     *      删除节点的后继者是在其右节点树种最小的节点
     * @param deleteNode
     * @return
     */
    public TreeNode getDeleteSuccessor(TreeNode deleteNode) {
        // 后继者
        TreeNode successor = null;
        TreeNode successorParent = null;
        TreeNode current = deleteNode.right;

        while (current != null) {
            successorParent = successor;
            successor = current;
            current = current.left;
        }

        // 检查后继者(不可能有左节点树)是否有右节点树
        // 如果它有右节点树,则替换后继者位置,加到后继者父亲节点的左节点.
        if (successor != deleteNode.right) {
            successorParent.left = successor.right;
            successor.right = deleteNode.right;
        }

        return successor;
    }
	
	public void toString(TreeNode root){
		if(root != null){
			toString(root.left);
			System.out.print("value = " + root.value + " -> ");
			toString(root.right);
		}
	}

	class TreeNode {
		private int value;
		private TreeNode left;
		private TreeNode right;

		public TreeNode(int value) {
			this.value = value;
			left = null;
			right = null;
		}

	}
	
	public static void main(String[] args){
		BinaryTree bt = new BinaryTree();
		bt.insert(3);bt.insert(8);bt.insert(1);bt.insert(4);bt.insert(6);
		bt.insert(2);bt.insert(10);bt.insert(9);bt.insert(20);bt.insert(25);
		
		bt.toString(bt.root);
		System.out.println();
		
		TreeNode node01 = bt.search(10);
		System.out.println("是否存在结点值为10 => " + node01.value);
		TreeNode node02 = bt.search(11);
		System.out.println("是否存在结点值为11 => " + node02);
		
	}
	
}
