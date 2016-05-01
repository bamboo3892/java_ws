package main;

public class Box {

	int row;//縦
	int column;//横
	int number = 0;
	boolean canPlace[] = new boolean[10];
	boolean err = false;
	Sheet sheet;

	public Box(int i,int j){
		this.row = i;
		this.column = j;
		canPlace[0] = false;
		for(int k=1;k<10;k++){
			canPlace[k] = true;
		}
	}

	public void placeNumber(int number){
		this.number = number;
	}

	public boolean placeNumber(){
		if(this.number != 0) return false;
		int nnn = 0;//置ける数字の数
		int mmm = 0;//置ける数字
		for(int k=1;k<10;k++){
			if(canPlace[k]){
				nnn++;
				mmm = k;
			}
		}
		if(nnn == 1){
			this.number = mmm;
			disableNumberAtOtherBoxes();
			return true;
		}else if(nnn == 0){
			this.err = true;
			System.out.println("もう置けんエラー１：" + row + ", " + column);
			return false;
		}else{
			return false;
		}
	}

	public void disableNumberAtOtherBoxes(){//自分の数字が決まった時だけ呼んでね
		if(this.number == 0) return;
		//縦横
		for(int k=0;k<9;k++){
			if (sheet == null) {
				Main.box[this.row][k].disableNumber(this.number);
				Main.box[k][this.column].disableNumber(this.number);
			}else{
				sheet.box[this.row][k].disableNumber(this.number);
				sheet.box[k][this.column].disableNumber(this.number);
			}
		}
		//３ｘ３
		int i1=row - row % 3;
		int j1=column - column % 3;
		for(int i2=i1;i2<i1+3;i2++){
			for(int j2=j1;j2<j1+3;j2++){
				if (sheet == null) {
					Main.box[i2][j2].disableNumber(this.number);
				}else{
					sheet.box[i2][j2].disableNumber(this.number);
				}
			}
		}

	}

	public void disableNumber(int number){
		canPlace[number] = false;
		int nnn = 0;//置ける数字の数
		for(int k=1;k<10;k++){
			if(canPlace[k]){
				nnn++;
			}
		}
		if(nnn == 0 && this.number == 0){
			this.err = true;
			System.out.println("もう置けんエラー２：" + row + ", " + column);
		}

	}
	
	public Box clone(){
		Box box = new Box(this.row, this.column);
		box.number = this.number;
		for(int i=0;i<10;i++){
			box.canPlace[i] = this.canPlace[i];
		}
		box.err = this.err;
		box.sheet = this.sheet;
		return box;
	}
	
	
}
