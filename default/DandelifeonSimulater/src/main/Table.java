package main;

import javax.swing.SwingUtilities;

public class Table extends Thread{

	public static int[][] maxTable = new int[25][25];
	public static int maxAge;
	public static int maxFirstLives;
	public static int maxPoint;
	public static double maxEfficiency = 0;
	public static int progress = 0;

	public int[][] firstTable = new int[25][25];
	private int[][] table = new int[25][25];
	private int[][] nextTable = new int[25][25];
	public int age = 1;
	public int firstLives;
	public int point = 0;

	public Table(int[][] ptable){
		int live = 0;
		for(int i=0;i<25;i++) for(int j=0;j<25;j++) {
			this.firstTable[i][j] = ptable[i][j];
			this.table[i][j] = ptable[i][j];
			if(ptable[i][j] == 1) live++;
		}
		firstLives = live;
	}

	@Override
	public void run(){
		if(firstLives < Start.minCell || firstLives > Start.maxCell) return;
		for(;age<=60;age++){
			setNextTable();
			if(check()) {
				checkAndSetMax(this);
				return;
			}
		}
	}

	private void setNextTable(){
		for(int i=0;i<25;i++) for(int j=0;j<25;j++) {
			this.nextTable[i][j] = getNextCell(i, j);
		}
		this.table = this.nextTable;
	}

	/**
	 *return true if it consumes or there's no live
	 */
	private boolean check(){
		//check consume
		for(int i=11;i<14;i++) for(int j=11;j<14;j++) {
			if(table[i][j] == 1){
				point += age;
			}
		}
		if(point != 0){
			return true;
		}
		//check all dead
		for(int i=0;i<25;i++) for(int j=0;j<25;j++) {
			if(table[i][j] == 1) return false;
		}
		return true;
	}

	private int getNextCell(int x, int y){
		int type = table[x][y];
		if(type == 2) return 2;
		int live = 0;
		for(int i=-1;i<2;i++) for(int j=-1;j<2;j++) {
			if(i ==0 && j == 0) continue;
			if(x+i<0 || x+i>=25 || y+j<0 || y+j>=25) {
				continue;
			}
			if(table[x+i][y+j] == 1) {
				live++;
				continue;
			}
		}
		if(type == 0) {
			if(live == 3) {
				return 1;
			}else{
				return 0;
			}
		}else if(type == 1){
			if(live == 2 || live == 3) {
				return 1;
			}else{
				return 0;
			}
		}
		return 0;
	}

	private synchronized static void checkAndSetMax(Table table){
		double efficiency = table.point / table.firstLives;
		//System.out.println(table.firstLives);
		if(efficiency > maxEfficiency){
			System.out.print("a");
			maxTable = table.firstTable;
			maxAge = table.age;
			maxFirstLives = table.firstLives;
			maxEfficiency = efficiency;
			if(!Start.visible) {
				System.out.println("maxAge = " + Table.maxAge +
						"\nmaxFirstLives = " + Table.maxFirstLives +
						"\nmaxEfficiency = " + Table.maxEfficiency);
			}
		}

		if(Start.visible){
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Start.maxAgeLabel.setText("maxAge = " + maxAge);
					Start.maxAgeLabel.setText("maxFirstLives = " + maxFirstLives);
					Start.maxAgeLabel.setText("maxEfficiency = " + maxEfficiency);
					Start.bar.setValue(Start.bar.getValue() + 1);
					Start.progressLabel.setText(Start.bar.getValue() + "/" + Start.n);
				}
			});
		}else{
			progress++;
			if(progress >= 1000000) {
				System.out.println("b");
				progress = 1;
			}
		}
	}

}
