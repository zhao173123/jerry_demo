package suanfa.backtracking;

import java.util.Date;

/**
 * <pre>
 * EightQueen的二维数组棋盘全是用0和1组成，而且在递归的时候都使用clone新建一个棋盘。
 * 当处理9皇后-15皇后的时候，随着皇后的增多，效率越来越低
 * </pre>
 * <p>
 * 改进： 将二维数组用一维数组取代，模仿棋盘结构，如chess[R]=C，表示棋盘上R行C列有一个皇后。
 * </p>
 * <p>
 * 结果集：
 * 解决 9皇后问题，用时：4毫秒，计算结果：352
 * 解决 10皇后问题，用时：8毫秒，计算结果：1076
 * 解决 11皇后问题，用时：26毫秒，计算结果：3756
 * 解决 12皇后问题，用时：176毫秒，计算结果：17956
 * 解决 13皇后问题，用时：780毫秒，计算结果：91668
 * 解决 14皇后问题，用时：4879毫秒，计算结果：457264
 * 解决 15皇后问题，用时：32237毫秒，计算结果：2736448
 * </p>
 * 
 * @author jerry
 *
 */
public class EightQueen2 {

	private static final short K = 15;
	private static int count = 0;
	private static short N = 0;

	public static void main(String[] args) {
		for (N = 9; N <= K; N++) {
			Date begin = new Date();
			/**
			 * 初始化棋盘，使用一堆数组存放棋盘信息，chess[n] = X 表示n行X列有皇后
			 */
			short chess[] = new short[N];
			for (int i = 0; i < N; i++) {
				chess[i] = 0;
			}
			putQueenAtRow(chess, (short) 0);
			Date end = new Date();
			System.out.println(
					"解决 " + N + "皇后问题，用时：" + String.valueOf(end.getTime() - begin.getTime()) + "毫秒，计算结果：" + count);
		}
	}

	private static void putQueenAtRow(short[] chess, short row) {
		/**
         * 递归终止判断：如果row==N，则说明已经成功摆放了8个皇后
         * 输出结果，终止递归
         */
		if (row == N) {
			count++;
			return;
		}
		short[] chessTemp = chess.clone();
		for (short i = 0; i < N; i++) {
			chessTemp[row] = i;
			if (isSafety(chessTemp, row, i)) {
				putQueenAtRow(chessTemp, (short) (row + 1));
			}
		}
	}

	private static boolean isSafety(short[] chess, short row, short col) {
		// 判断中上、左上、右上是否安全
		short step = 1;
		for (short i = (short) (row - 1); i >= 0; i--) {
			if (chess[i] == col) // 中上
				return false;
			if (chess[i] == col - step) // 左上
				return false;
			if (chess[i] == col + step) // 右上
				return false;
			step++;
		}
		return true;
	}
}
