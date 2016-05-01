package main;

public class Sheet {

	static int rowNum = 0;
	static int columnNum = 0;
	static int row[][];
	static int column[][];
	int[][] box;
	int nowRow = -1;
	int nowColumn = 0;
	static int sheetNum = 0;
	boolean err = false;

	public Sheet(int parRowNum, int parColumnNum, int box[][]){

		sheetNum++;

		set(parRowNum, parColumnNum);
		this.box = new int[rowNum][columnNum];
		for(int i=0;i<rowNum;i++){
			for(int j=0;j<columnNum;j++){
				this.box[i][j] = box[i][j];
			}
		}

		flag:while(assumption()){

			//System.out.println("仮定を始めるよ（" + sheetNum + "）");
			//System.out.println("今の行：" + nowRow);
			//System.out.println("今の列：" + nowColumn);

			//判定
			if(nowColumn == columnNum - 1){
				//System.out.println("行の判定");
				if(!checkRow(nowRow)){
					//System.out.println("この行はおかしい");
					continue flag;
				}
				//System.out.println("この行は大丈夫////////////////////////////////////////////////////////////////");
				if(nowRow == rowNum - 1){
					//show();
					if(checkAll()){
						System.out.println("でけた！");
						err = false;
						return;
					}else{
						continue flag;
					}
				}
			}

			//次の仮定
			Sheet s = new Sheet(rowNum, columnNum, this.box);
			if(s.err){
				continue flag;
			}else{
				err = false;
				return;
			}
		}
		err = true;
		//System.out.println("このシートでの仮定終わり");
	}

	public boolean checkAll() {//エラーがなかったらtrue

		for(int i=0;i<rowNum;i++){
			if(!checkRow(i)){
				return false;
			}
		}

		for(int i=0;i<columnNum;i++){
			if(!checkColumn(i)){
				return false;
			}
		}
		return true;
	}

	public int check(){//空いてるとこあったら０、完成は１、エラーは２
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

	public boolean checkRow(int line){//全部合ってたらtrue

		String str = "";
		int n1 = 1;
		int n2 = 0;
		for(int i=0;i<columnNum;i++){
			if(box[line][i] == 0){
				return false;
			}else if(box[line][i] == 1){
				if(n1 == 2){
					str += String.valueOf(n2) + ",";
					n2 = 0;
				}
				n1 = 1;
			}else if(box[line][i] == 2){
				n2++;
				n1 = 2;
			}
			if(i == columnNum - 1){
				if(n1 == 2){
					str += String.valueOf(n2) + ",";
					n2 = 0;
				}
			}
		}

		if (str.length() > 0) {
			if (str.charAt(str.length() - 1) == ',') {
				str = str.substring(0, str.length() - 1);
			}
		}

		//System.out.println(str);

		String mmm[] = str.split(",");
		int m[] = new int[mmm.length];
		if(mmm[0] != ""){
			for(int i = 0; i < mmm.length; i++){
				m[i] = Integer.parseInt(mmm[i]);
			}
		}else{
			if(row[line][0] == 0){
				return true;
			}
		}
		if(m.length != row[line].length){
			return false;
		}

		boolean b = true;
		for(int i=0;i<m.length;i++){
			if(m[i] != row[line][i]){
				b = false;
			}
		}
		if(b){
			//System.out.println("true");
			return true;
		}else{
			return false;
		}
	}

	public boolean checkColumn(int line){//全部合ってたらtrue

		String str = "";
		int n1 = 1;
		int n2 = 0;
		for(int i=0;i<rowNum;i++){
			if(box[i][line] == 0){
				return false;
			}else if(box[i][line] == 1){
				if(n1 == 2){
					str += String.valueOf(n2) + ",";
					n2 = 0;
				}
				n1 = 1;
			}else if(box[i][line] == 2){
				n2++;
				n1 = 2;
			}
			if(i == rowNum - 1){
				if(n1 == 2){
					str += String.valueOf(n2) + ",";
					n2 = 0;
				}
			}
		}

		if (str.length() > 0) {
			if (str.charAt(str.length() - 1) == ',') {
				str = str.substring(0, str.length() - 1);
			}
		}

		//System.out.println(str);

		String mmm[] = str.split(",");
		int m[] = new int[mmm.length];
		if(mmm[0] != ""){
			for(int i = 0; i < mmm.length; i++){
				m[i] = Integer.parseInt(mmm[i]);
			}
		}else{
			if(column[line][0] == 0){
				return true;
			}
		}
		if(m.length != column[line].length){
			return false;
		}

		boolean b = true;
		for(int i=0;i<m.length;i++){
			if(m[i] != column[line][i]){
				b = false;
			}
		}
		if(b){
			//System.out.println("true");
			return true;
		}else{
			return false;
		}
	}

	public void show(){
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

	public static void set(int parRowNum, int parColumnNum){
		rowNum = parRowNum;
		columnNum = parColumnNum;
	}

	public boolean assumption() {

		if(nowRow != -1){
			if(box[nowRow][nowColumn] == 1){
				box[nowRow][nowColumn] = 2;
				//System.out.println(nowRow + "," + nowColumn + "を２に仮定する");
				return true;
			}else if(box[nowRow][nowColumn] == 2){
				this.err = true;
				return false;
			}else{
				System.out.println("変なコト起こっとる");
				this.err =true;
				return false;
			}
		}

		for(int i=0;i<rowNum;i++){
			for(int j=0;j<columnNum;j++){
				if(this.box[i][j] == 0){
					this.nowRow = i;
					this.nowColumn = j;
					box[i][j] = 1;
					//System.out.println(nowRow + "," + nowColumn + "を１に仮定");
					return true;
				}
			}
		}

		return false;

	}

}
