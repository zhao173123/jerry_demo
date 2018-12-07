package suanfa.backtracking;

import java.util.Date;
import java.util.Stack;

/**
 * 这里采用数据结构“栈”来模拟解决15皇后的问题
 * 递归函数手工实现，处理递归的本质就是栈
 * 时间复杂度是一样的，因为自定义了class，空间复杂度有所上升
 * 
 * <p>解决 15皇后问题，用时：45589毫秒，计算结果：2279184</p>
 * @author jerry
 *
 */
public class EightQueen3 {

	private static final short N = 15;
	
	public static void main(String[] args){
		Date begin = new Date();
		long count = 0;
		//初始化栈和棋盘，并向栈中压入第一张初始化的棋盘
		Stack<Chess> stack = new Stack<Chess>();
		short[] chessData = new short[N];
		for(int i = 1; i < N; i++){
			chessData[i] = -1; //初始化棋盘，所有行都没有皇后，赋值-1
		}
		Chess initChess = new Chess(chessData);
		stack.push(initChess);
		/*
		 * 访问出口处的棋盘，判断是否访问过
		 * 如果访问过，尝试对此棋盘col++寻找此行的合法解
		 * 	  寻找直至溢出边界，pop掉。在寻找过程中如果发现合法解，
		 *    修改col，访问量状态恢复到false，跳出isEmpty循环去访问它
		 * 如果没有访问过，访问标志改为true，构建下层数据
		 */
		EMPTY:while(!stack.isEmpty()){
			Chess chess = stack.peek();//查看栈顶元素而不移除
			if(chess.visited){
				while(chess.moveCol()){
					if(isSafety(chess)){
						chess.visited = false;
						continue EMPTY;
					}
				}
				stack.pop();
			}else{
				/**
				 * 构建下层数据
				 * 构建栈顶元素的克隆，访问状态置为false
				 * row下移一层，如果溢出边界丢弃（这种情况不该发生），
				 * 
				 */
				chess.visited = true;
				Chess chessTemp = chess.clone();
				if(chessTemp.moveRow()){
					while(chessTemp.moveCol()){
						if(isSafety(chessTemp)){
							if(chessTemp.currentRow == N -1){
								count++;
								continue;
							}else{
								stack.push(chessTemp);
								continue EMPTY;
							}
						}
					}
				}
			}
		}
		Date end =new Date();
        System.out.println("解决 " +N+ "皇后问题，用时：" +String.valueOf(end.getTime()-begin.getTime())+ "毫秒，计算结果："+count);
	}

	//判断中上、左上、右上是否安全
	private static boolean isSafety(Chess chess) {
		short step = 1;
		for(short i = (short)(chess.currentRow - 1); i >= 0; i--){
			if(chess.chess[i] == chess.currentCol) // 中上
				return false;
			if(chess.chess[i] == chess.currentCol - step) //左上 
				return false; 
			if(chess.chess[i] == chess.currentCol + step) //右上
				return false;
			step++;
		}
		return true;
	}
	

}

class Chess implements Cloneable{
	public short[] chess;//棋盘数据
	public short currentRow = 0;//当前行
	public short currentCol = 0;//当前列
	public boolean visited = false;
	
	public Chess(short[] chess){
		this.chess = chess;
	}
	
	public boolean moveCol(){
		this.currentCol++;
		if(this.currentCol >= chess.length)
			return false;
		else{
			this.chess[currentRow] = currentCol;
			return true;
		}
	}
	
	public boolean moveRow(){
		this.currentRow++;
		if(this.currentRow >= chess.length)
			return false;
		else
			return true;
	}
	
	public Chess clone(){
		short[] chessData = this.chess.clone();
		Chess chess = new Chess(chessData);
		chess.currentCol = -1;
		chess.currentRow = this.currentRow;
		chess.visited = false;
		return chess;
	}
	
}
