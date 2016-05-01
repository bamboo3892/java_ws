package main;

public class Sheet {

	Box original[][] = new Box[9][9];
	Box box[][] = new Box[9][9];
	//今仮定している場所と番号
	int nowRow = 0;
	int nowColumn = 0;
	int nowNumber = 0;
	boolean err = false;

	static int aaa = 0;

	public Sheet(){

	}

	public Sheet(Box parbox[][]) throws Throwable{

		//this.box = parbox.clone();
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				this.box[i][j] = parbox[i][j].clone();
				this.box[i][j].sheet = this;
				this.original[i][j] = parbox[i][j].clone();
				this.original[i][j].sheet = this;
			}
		}

		if(checkError()){
			System.out.println("err");
			err = true;
			return;
		}
		
		System.out.println("Sheetなう、仮定始めるよ");

		flag1: while(assumption()){

			aaa++;

			//初等的な処理開始
			flag2: while(true){

				//数字を埋めていく
				boolean b1 = true;//一回も数字が変わってなかったらtrue
				for(int i=0;i<9;i++){
					for(int j=0;j<9;j++){
						if(box[i][j].placeNumber()){
							b1 = false;
						}
					}
				}
				if(!checkRowAndColumnAnd3x3()){
					b1 = false;
				}
				if(b1){
					break flag2;
				}

				//どのマスにもエラーがないか確認
				boolean b3 = false;//一つでもエラーがあったらtrue
				for(int i=0;i<9;i++){
					for(int j=0;j<9;j++){
						if(box[i][j].err){
							System.out.println("Sheetでエラー");
							b3 = true;
						}
					}
				}
				if(b3){
					continue flag1;
				}

				//break flag;
			}

			//チェック
			if(checkError()){
				continue flag1;
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
			if(b2){
				Main.box = this.box;
				System.out.println("でけた！");
				err = false;
				break flag1;
			}

			//数字が動かなくなったら新しくシートを作る
			//シートがエラーを出したら次の仮定
			//falseならerrはfalseのまま終了
			Sheet s = new Sheet(box);
			if(!s.err){
				break flag1;
			}else{
				continue flag1;
				//s.finalize();
				//System.gc();
			}
		}

		//終わったら呼んだとこがerrを参照

	}

	protected boolean checkError(){//エラーがあったらtrue

		//どのマスにもエラーがないか確認
		boolean b3 = false;//一つでもエラーがあったらtrue
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				if(box[i][j].err){
					b3 = true;
				}
			}
		}
		return b3;

	}

	public boolean checkRowAndColumnAnd3x3(){
		boolean b1 = true;//一つでも変わったらfalse
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
		return b1;
	}

	private boolean assumption(){//次の過程がなくなったらfalse

		/*
		if(nowRow==8 && nowColumn==8 && nowNumber==9){
			this.err = true;
			return false;
		}else{
			flag: while(true){
				if(box[nowRow][nowColumn].number!=0 && nowRow==8 && nowColumn==8){
					this.err = true;
					return false;
				}
				if(box[nowRow][nowColumn].number!=0 || nowNumber==9){
					if(nowColumn == 8){
						nowRow++;
						nowColumn = 0;
						nowNumber = 1;
					}else{
						nowColumn++;
						nowNumber = 1;
					}
				}else{
					nowNumber++;
				}
				if(box[nowRow][nowColumn].number==0 && box[nowRow][nowColumn].canPlace[nowNumber]){
					box[nowRow][nowColumn].placeNumber(nowNumber);
					box[nowRow][nowColumn].disableNumberAtOtherBoxes();
					break flag;
				}
			}
			return true;
		}
		*/
		if(nowNumber == 9){
			System.out.println("もういっぱいいっぱいエラー１");
			this.err = true;
			return false;
		}
		
		if(nowNumber != 0){
			System.out.println("次の過程いっくよーーーーーーーーーーーーー");
			for(int i=0;i<9;i++){
				for(int j=0;j<9;j++){
					this.box[i][j] = original[i][j].clone();
				}
			}
			reset();
			nowNumber++;
			this.box[nowRow][nowColumn].placeNumber(nowNumber);
		}

		if(nowNumber == 0){
			
			System.out.println("nowNumberが0");
			
			flag: while(true){//数字が入ってないところまで送っていく
				if(nowRow==8 && nowColumn==8 && (nowNumber==9 || box[nowRow][nowColumn].number!=0)){
					System.out.println("最後まで行ってしまった");
					this.err = true;
					return false;
				}
				if(box[nowRow][nowColumn].number!=0){//０じゃなかったら次のところに送る
					if(nowColumn == 8){
						nowRow++;
						nowColumn = 0;
						nowNumber = 1;
					}else{
						nowColumn++;
						nowNumber = 1;
					}
				}
				if(box[nowRow][nowColumn].number==0){//ゼロだったらループを出る
					break flag;
				}
			}
			nowNumber = 1;
			System.out.println("nowNumberが1");
			System.out.println(nowRow + ", " + nowColumn + "を仮定するよ");
		}

		//これ以降nowRowとnowColumnは変えない
		while(true){
			if(box[nowRow][nowColumn].canPlace[nowNumber]){
				System.out.println(nowRow + ", " + nowColumn + "を" + nowNumber + "に仮定");
				box[nowRow][nowColumn].placeNumber(nowNumber);
				box[nowRow][nowColumn].disableNumberAtOtherBoxes();
				return true;
			}else{
				System.out.println(nowNumber + "->" + (nowNumber+1));
				nowNumber++;
			}
			if(nowNumber >= 10){
				System.out.println("もういっぱいいっぱいエラー２");
				this.err = true;
				return false;
			}
		}

	}
	
	public void reset(){
		display();
		System.out.println("まずリセット");
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				for(int k=1;k<10;k++){
					this.box[i][j].canPlace[k] = true;
					this.box[i][j].err = false;
				}
			}
		}
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				this.box[i][j].disableNumberAtOtherBoxes();
			}
		}
		System.out.println("リセット終了");
		display();
	}
	
	public void display(){
		
		String row[] = new String[9];

		for(int i=0;i<9;i++){
			row[i] = "";
			for(int j=0;j<9;j++){
				row[i] += box[i][j].number;
			}
			System.out.println(row[i]);
		}

	}

}
