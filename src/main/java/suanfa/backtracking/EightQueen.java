package suanfa.backtracking;

import java.util.Date;

/**
 * 八皇后问题：
 * 在8*8的棋盘上摆放8个皇后，使其不能相互攻击（两个皇后不能处于同一行、同一列或斜线），有多少种摆法？
 * 
 * 分析：
 * 1.从第一行第一列开始摆放皇后；
 * 2.调用验证函数，传递整个棋盘的数据验证合理性。安全则摆放下一个，否则尝试摆放下一列直到本行边界；
 * 3.当这一行所有位置都无法保证皇后安全时，需要回退到上一行，清除上一行的摆放记录，
 * 	 并且在上一行尝试摆放下一个位置的皇后（核心）；
 * 4.当摆放到最后一行并调用验证函数正确后，累积数自增1，表示有一个解成功。
 * 
 * PS：验证函数中，需要扫描摆放皇后的左上、中上、右上是否有其他皇后，不需要考虑当前棋盘位置下方的安全性。
 * 
 * 打印->解决:8皇后问题，用时:186毫秒，计算结果:92
 * @author jerry
 *
 */
public class EightQueen {

	private static final short N = 8;//8皇后，行列数
	private static int count = 0;//结果计数器
	
	public static void main(String[] args){
		Date begin = new Date();
		short chess[][] = new short[N][N];
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){
				chess[i][j] = 0;
			}
		}
		putQueenAtRow(chess, 0);
		Date end = new Date();
		System.out.println("解决:" + N + "皇后问题，用时:" + String.valueOf(end.getTime() - begin.getTime())
			+"毫秒，计算结果:" + count);
	}

	private static void putQueenAtRow(short[][] chess, int row) {
		/**
		 * 递归终止判断：
		 * 如果row==N，说明成功摆放了N个皇后
		 * 
		 */
		if(row == N){
			count++;
			System.out.println("第" + count + "种解:");
			for(int i = 0; i < N; i++){
				for(int j = 0; j < N; j++){
					System.out.print(chess[i][j]+ " ");
				}
				System.out.println();
			}
			return;
		}
		
		short[][] chessTemp = chess.clone();
		/**
		 * 向这一行的每一个位置尝试排放皇后
		 * 然后检测状态，如果安全则继续执行递归函数摆放下一个皇后
		 */
		for(int i = 0; i < N; i++){
			for(int j = 0; j < N; j++){//摆放这一行的皇后之前需要清除这一行的摆放记录
				chessTemp[row][j] = 0;
			}
			chessTemp[row][i] = 1;
			if(isSafety(chessTemp, row, i)){
				putQueenAtRow(chessTemp, row + 1);
			}
		}
	}

	//判断中上、左上、右上是否安全
	private static boolean isSafety(short[][] chess, int row, int col) {
		int step = 1;
		while(row - step >= 0){
			if(chess[row-step][col] == 1) //中上
				return false;
			if(col - step >= 0 && chess[row-step][col-step] == 1)//左上
				return false;
			if((col + step) < N && chess[row-step][col+step] == 1)//右上
				return false;
			step++;
		}
		return true;
	}
	
}
