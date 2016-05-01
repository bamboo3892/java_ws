package main;

import java.io.IOException;

public class Main extends Sheet{

	public Main(Box[][] box) throws Throwable {
		super(box);
	}
	public Main(){

	}

	static String row[] = new String[9];
	static Box box[][] = new Box[9][9];
	static boolean err = false;

	public static void main(String args[]) throws IOException{

		System.out.println("一列目から順番に９つ。");
		System.out.println("空白のマスは０を入力といて。");
		System.out.println("答えがひとつに定まらないような問題でも");
		System.out.println("一つのありえる答えを出すのであしからず");
		System.out.println("--------------------------------------");

		/*
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		for(int i=0;i<9;i++){
			line[i] = br.readLine();
		}
		*/

		/*
		row[0] = "001000500";
		row[1] = "006050040";
		row[2] = "300702006";
		row[3] = "009000000";
		row[4] = "600803004";
		row[5] = "000000200";
		row[6] = "800604007";
		row[7] = "060010400";
		row[8] = "007000900";
		*/
		
		row[0] = "000310000";
		row[1] = "020000048";
		row[2] = "000006500";
		row[3] = "000007280";
		row[4] = "500000004";
		row[5] = "043100000";
		row[6] = "001600000";
		row[7] = "280000070";
		row[8] = "000092000";

		//数字入力
		start();

		//処理部分
		flag: while(true){

			//数字を埋めていく
			boolean b1 = true;//一回も数字が変わってなかったらtrue

			for(int i=0;i<9;i++){
				for(int j=0;j<9;j++){
					if(box[i][j].placeNumber()){
						b1 = false;
					}
				}
			}

			for (int k=1;k<10;k++) {//kは確かめる数字
				for(int i=0;i<9;i++){//縦と横
					int nnn1 = 0;//縦、おける数字の数
					int nnn2 = 0;//おく場所
					int nnn3 = 0;
					int mmm1 = 0;//横
					int mmm2 = 0;
					int mmm3 = 0;
					for(int j=0;j<9;j++){
						if(box[i][j].canPlace[k] && box[i][j].number == 0){//行
							nnn1++;
							nnn2 = i;
							nnn3 = j;
						}
						if(box[j][i].canPlace[k] && box[j][i].number == 0){//列
							mmm1++;
							mmm2 = j;
							mmm3 = i;
						}
					}
					if(nnn1 == 1){
						box[nnn2][nnn3].placeNumber(k);
						box[nnn2][nnn3].disableNumberAtOtherBoxes();
						b1 = false;
					}
					if(mmm1 == 1){
						box[mmm2][mmm3].placeNumber(k);
						box[mmm2][mmm3].disableNumberAtOtherBoxes();
						b1 = false;
					}
				}
				//３ｘ３
				for(int i1=0;i1<3;i1++){
					for(int j1=0;j1<3;j1++){
						int lll1 = 0;
						int lll2 = 0;
						int lll3 = 0;
						for(int i2=i1*3;i2<i1*3+3;i2++){
							for(int j2=j1*3;j2<j1*3+3;j2++){
								if(box[i2][j2].canPlace[k] && box[i2][j2].number ==0){
									lll1++;
									lll2 = i2;
									lll3 = j2;
								}
							}
						}
						if(lll1 == 1){
							box[lll2][lll3].placeNumber(k);
							box[lll2][lll3].disableNumberAtOtherBoxes();
							b1 = false;
						}

					}
				}
			}
			if(b1){
				//System.out.println("こりゃ解けんわ。");
				break flag;
			}

			//どのマスにもエラーがないか確認
			boolean b3 = false;//一つでもエラーがあったらtrue
			for(int i=0;i<9;i++){
				for(int j=0;j<9;j++){
					if(box[i][j].err){
						b3 = true;
					}
				}
			}
			if(b3){
				System.out.println("こりゃ解けぬ。");
				err = true;
				break flag;
			}

			//break flag;
		}

		//すべて数字埋まってるか確認
		boolean b2 = true;//一つでも空白があったらfalse
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				if(box[i][j].number == 0){
					b2 = false;
				}
			}
		}

		//仮定を始めるよ
		displayAnswer();
		if(!b2 && !err){
			System.out.println("仮定開始");
			try {
				Sheet s = new Sheet(box);
				if(s.err){
					err = true;
				}
			} catch (Throwable e) {
				System.out.println("Excption");
				e.printStackTrace();
			}
		}

		if(b2 && !err){
			System.out.println("答えを導けたでござる。");
		}else{
			if(err){
				System.out.println("エラーでたわ。");
			}
			System.out.println("とりあえず表示しとく。");
		}

		//答えを表示
		displayAnswer();

		assess();

	}

	public static void start(){

		for(int i=0;i<9;i++){
			if(row[i].length() == 9){
				for(int j=0;j<9;j++){
					box[i][j] = new Box(i, j);
					box[i][j].placeNumber((int)row[i].charAt(j)-48);
				}
			}else{
				System.out.println("数字は９つ入力してちょ。");
				i--;
			}
		}

		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				if(box[i][j].number != 0){
					box[i][j].disableNumberAtOtherBoxes();
				}
			}
		}

	}

	public static void displayAnswer(){

		for(int i=0;i<9;i++){
			row[i] = "";
			for(int j=0;j<9;j++){
				row[i] += box[i][j].number;
			}
			System.out.println(row[i]);
		}

	}

	private static void assess() {

		if(
				row[0].equals("") &&
				row[1].equals("") &&
				row[2].equals("") &&
				row[3].equals("") &&
				row[4].equals("") &&
				row[5].equals("") &&
				row[6].equals("") &&
				row[7].equals("") &&
				row[8].equals("")
		){
			//System.out.println("当たっとる");
		}else{
			//System.out.println("間違っとる");
		}
		System.out.println("仮定した回数：" + Sheet.aaa);

	}
}





