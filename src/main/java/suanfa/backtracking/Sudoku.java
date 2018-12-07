package suanfa.backtracking;

/**
 * <p>
 * 8 1 2 7 5 3 6 4 9 
 * 9 4 3 6 8 2 1 7 5 
 * 6 7 5 4 9 1 2 8 3 
 * 1 5 4 2 3 7 8 9 6 
 * 3 6 9 8 4 5 7 2 1 
 * 2 8 7 1 6 9 5 3 4 
 * 5 2 1 9 7 4 3 6 8 
 * 4 3 8 5 2 6 9 1 7 
 * 7 9 6 3 1 8 4 5 2 
 * </p>
 * 
 * @author jerry
 *
 */
public class Sudoku {

	private int[][] matrix;

	public Sudoku(int[][] matrix){
		this.matrix = matrix;
	}
	
	public static void main(String[] args){
		int[][] sudoku = {
				{8, 0, 0, 0, 0, 0, 0, 0, 0},
				{0, 0, 3, 6, 0, 0, 0, 0, 0},
				{0, 7, 0, 0, 9, 0, 2, 0, 0},
				{0, 5, 0, 0, 0, 7, 0, 0, 0},
				{0, 0, 0, 0, 4, 5, 7, 0, 0},
				{0, 0, 0, 1, 0, 0, 0, 3, 0},
				{0, 0, 1, 0, 0, 0, 0, 6, 8},
				{0, 0, 8, 5, 0, 0, 0, 1, 0},
				{0, 9, 0, 0, 0, 0, 4, 0, 0}};
		Sudoku s = new Sudoku(sudoku);
		s.backTrace(0, 0);
	}

	/**
	 * 数独算法
	 * 原理：从0行0列开始，依次填入1-9的数字，然后判断填入的这个数字是否满足条件
	 * （该行该列和它所在小九宫格是否有重复数字）
	 * 如果满足条件，继续用1-9去试该行的下一列，一直到该行的最后一列。
	 * 接着换行重复上述步骤，一直执行到最后一行最后一列。
	 * 
	 * @param i
	 * @param j
	 */
	private void backTrace(int i, int j) {
		//已经成功，打印结果
		if(i == 8 && j == 9){
			System.out.println("获取正确解");
			printArray();
			return;
		}
		//已经到了列末尾，还没到行尾，换行
		if(j == 9){
			i++;
			j = 0;
		}
		
		//如果i行j列是空格，才进入给空格填值的逻辑
		if(matrix[i][j] == 0){
			for(int k = 1; k <=9; k++){
				if(check(i, j, k)){//判断给i行j列放1-9中的任意一个数字是否满足规则
					matrix[i][j] = k;
					backTrace(i, j + 1);
					matrix[i][j] = 0;//初始化该空格
				}
			}
		}else{
			backTrace(i, j + 1);//如果该位置已经有值，就进入下一个空格进行计算
		}
	}

	private void printArray() {
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * 判断给i行j列的赋值是否满足要求
	 * 
	 * @param row 待赋值的行
	 * @param col 待赋值的列
	 * @param number 待赋值
	 * @return
	 */
	private boolean check(int row, int col, int number) {
		//判断该行是否有重复数字
		for(int i = 0; i < 9; i++){
			if(matrix[row][i] == number || matrix[i][col] == number)
				return false;
		}
		//判断小九宫格是否有重复
		int tempRow = row / 3;
		int tempLine = col / 3;
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(matrix[tempRow * 3 + i][tempLine * 3 + j] == number)
					return false;
			}
		}
		return true;
	}
}
