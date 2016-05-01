package main;

import java.io.IOException;

public class Main extends Sheet{

	public Main(int rowNum, int columnNum, int[][] box) {
		super(rowNum, columnNum, box);
	}

	static int[][] box;

	public static void main(String args[]) throws IOException{

		/*

		//まずは入力してもらう
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("絵の縦のマスの数");
		rowNum = Integer.parseInt(br.readLine());

		System.out.println("絵の横のマスの数");
		columnNum = Integer.parseInt(br.readLine());

		*/

		/////////////////////////////////////////////////////////////////////

		rowNum = 20;
		columnNum = 20;

		/////////////////////////////////////////////////////////////////////

		box = new int[rowNum][columnNum];
		for(int i=0;i<rowNum;i++){
			for(int j=0;j<columnNum;j++){
				box[i][j] = 0;
			}
		}

		//String line;
		row = new int[rowNum][];
		column = new int[columnNum][];

		/*

		System.out.println("縦の数字を上から順にどうぞ");
		System.out.println("数字はコンマで区切っといて");
		for(int i=0;i<rowNum;i++){
			System.out.print((i+1) + ":");
			line = br.readLine();
			row[i] = read(line);
			System.out.println("");
		}

		System.out.println("横の数字を左から順にどうぞ");
		for(int i=0;i<columnNum;i++){
			System.out.print((i+1) + ":");
			line = br.readLine();
			column[i] = read(line);
			System.out.println("");
		}

		*/

		////////////////////////////////////////////////////////////////////

		row[0] = read("20");
		row[1] = read("6,7");
		row[2] = read("5,12,1");
		row[3] = read("5,1,4,1");
		row[4] = read("5,1,1,3,1");
		row[5] = read("5,1,1,3,1");
		row[6] = read("2,2,1,2,3");
		row[7] = read("2,2,1,1,2");
		row[8] = read("2,1,1,1,2,1");
		row[9] = read("1,1,1,1,3,1");
		row[10] = read("1,1,1,1,6");
		row[11] = read("1,2,1,6");
		row[12] = read("5,1,6");
		row[13] = read("5,1,1,6");
		row[14] = read("3,2,1,1,6");
		row[15] = read("4,3,1,4");
		row[16] = read("2,2,8,3");
		row[17] = read("2,2,1");
		row[18] = read("2,14");
		row[19] = read("2,1");

		column[0] = read("17");
		column[1] = read("9,6");
		column[2] = read("6,4,2");
		column[3] = read("8,3,2,2");
		column[4] = read("15,2,1");
		column[5] = read("2,2,2");
		column[6] = read("1,1,1,1");
		column[7] = read("1,15,1");
		column[8] = read("1,1,1,1");
		column[9] = read("1,1,1,1");
		column[10] = read("1,1,2,1,1");
		column[11] = read("1,1,3,3,2,1,1");
		column[12] = read("1,1,1,1");
		column[13] = read("3,1,1");
		column[14] = read("17,1");
		column[15] = read("15,1");
		column[16] = read("7,7,1");
		column[17] = read("4,7,1");
		column[18] = read("2,10");
		column[19] = read("6,9");

		////////////////////////////////////////////////////////////////////

		//入力された数字がマス目を超えてないかチェック
		//ついでに簡単にわかるところを色塗り
		boolean b1 = false;//一つでもエラーがあったらtrue
		flag:for(int i=0;i<rowNum;i++){

			int k = 0;//その行の合計
			int a1 = 0;//その行の最低必要マス数
			for(int j=0;j<row[i].length;j++){
				k += row[i][j];
			}
			a1 = k + row[i].length -1;

			if(a1 > columnNum){
				b1 = true;
				//System.out.println("そんな数字入り切らんがな");
				break flag;
			}
			//簡単なところを埋める処理
			int a2 = 0;//そこまでの合計
			for(int j=0;j<row[i].length;j++){
				a2 += row[i][j];
				int f = row[i][j] - (columnNum - a1);
				if(f > 0){
					for(int l=0;l<f;l++){
						box[i][a2-1-l] = 2;
					}
				}
				a2++;
			}

		}

		//showMain();

		flag:for(int i=0;i<columnNum;i++){

			int k = 0;//その列の合計
			int a1 = 0;//その列の最低必要マス数
			for(int j=0;j<column[i].length;j++){
				k += column[i][j];
			}
			a1 = k + column[i].length -1;
			if(a1 > rowNum){
				b1 = true;
				break flag;
			}
			//簡単なところを埋める処理
			int a2 = 0;//そこまでの合計
			for(int j=0;j<column[i].length;j++){
				a2 += column[i][j];
				int f = column[i][j] - (rowNum - a1);
				if(f > 0){
					for(int l=0;l<f;l++){
						box[a2-1-l][i] = 2;
					}
				}
				a2++;
			}
		}

		//showMain();

		if(b1){
			System.out.println("そんな数字入り切らんがな");
			return;
		}

		//一応チェック
		int c1 = checkMain(rowNum, columnNum);
		if(c1 == 2){
			System.out.println("エラー１");
			return;
		}
		if(c1 == 1){
			System.out.println("完成");
		}

		showMain();

		//仮定を始める
		Sheet s = new Sheet(rowNum, columnNum, box);
		if(s.err){
			System.out.println("エラー２");
		}

		int c2 = checkMain(rowNum, columnNum);
		if(c2 == 2){
			System.out.println("エラー１");
			return;
		}
		if(c2 == 1){
			System.out.println("完成");
			showMain();
		}

	}

	public static int checkMain(int rowNum, int columnNum){//空いてるとこあったら０、完成は１、エラーは２
		boolean b1 = true;//全部埋まってるならtrue
		boolean b2 = false;//エラーならtrue
		for(int i=0;i<rowNum;i++){
			for(int j=0;j<columnNum;j++){
				if(box[i][j] == 3) b2 = true;
				if(box[i][j] == 0) b1 = false;
			}
		}
		if(b2) return 2;
		if(b1) return 1;
		return 0;
	}

	private static int[] read(String line) {
		String n[] = line.split(",");
		int m[] = new int[n.length];
		for(int i=0;i<n.length;i++){
			m[i] = Integer.parseInt(n[i]);
		}
		return m;
	}

	public static void showMain(){
		for(int i=0;i<rowNum;i++){
			for(int j=0;j<columnNum;j++){
				if(box[i][j] == 0){
					System.out.print("　");
				}else if(box[i][j] == 1){
					System.out.print("□");
				}else if(box[i][j] == 2){
					System.out.print("■");
				}else{
					System.out.print("◆");
				}
				if(j == columnNum - 1){
					System.out.println("");
				}
			}
		}
	}
}
