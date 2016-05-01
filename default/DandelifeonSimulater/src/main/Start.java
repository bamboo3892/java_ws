package main;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class Start {


	public static int[][] table = new int[25][25];
	public static JProgressBar bar;
	public static JLabel maxAgeLabel;
	public static JLabel maxFirstLivesLabel;
	public static JLabel maxEfficiencyLabel;
	public static JLabel progressLabel;
	//public static final int n = (int)Math.pow(3, 25*25);
	public static int n = 0;
	public static final int minCell = 4;
	public static final int maxCell = 10;
	//0 : dead
	//1 : live
	//2 : dead2 (always dead)

	public static final boolean visible = true;

	public static void main(String args[]){

		n = (int)Math.pow(3, 13*13);

		/*
		for(int i=minCell;i<=maxCell;i++){
			int nnn = 1;
			int mmm = 1;
			for(int j=1;j<=i;j++){
				nnn *= (25*25 - j +1);
				mmm *= j;
			}
			n += (int)(nnn / mmm);
		}
		*/
		if(visible){
			JFrame frame = new JFrame("Progress");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	frame.setBounds(10, 10, 400, 130);
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
			bar = new JProgressBar(0, n);
			progressLabel = new JLabel("0/" + n);
			maxAgeLabel = new JLabel("maxAge = 0");
			maxFirstLivesLabel = new JLabel("maxFirstLives = 0");
			maxEfficiencyLabel = new JLabel("maxEfficiency = 0");
			panel.add(maxAgeLabel);
			panel.add(maxFirstLivesLabel);
			panel.add(maxEfficiencyLabel);
			panel.add(progressLabel);
			panel.add(bar);
	    	frame.getContentPane().add(panel, BorderLayout.CENTER);
			frame.setVisible(true);
		}

		table[0][2] = table[1][2] = table[2][2] = table[1][0] = table[2][1] = 1;
		Table t = new Table(table);
		t.start();

		nextAssumption(0, 0);
		System.out.println("maxAge = " + Table.maxAge +
			"\nmaxFirstLives = " + Table.maxFirstLives +
			"\nmaxEfficiency = " + Table.maxEfficiency);
		for(int i=0;i<25;i++) {
			for(int j=0;j<25;j++) {
				System.out.print(Table.maxTable[i][j]);
			}
			System.out.println("");
		}
	}

	public static void nextAssumption(int x, int y){
		if(x  >= 25) {
			x = 0;
			y++;
		}
		if(y >= 25) {
			Table t = new Table(table);
			t.start();
			return;
		}

		if(x >= 13 || y >= 13){
			table[x][y] = 0;
		}

		if((x==11||x==12||x==13) && (y==11||y==12||y==13)) {
			table[x][y] = 0;
		}
		for(int type=0;type<3;type++){
			table[x][y] = type;
			nextAssumption(x+1, y);
		}
	}

}
