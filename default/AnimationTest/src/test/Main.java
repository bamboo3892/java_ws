package test;

import javax.swing.JFrame;

public class Main{

	static final int WIDTH = 712;
	static final int HEIGHT = 768;
	static JFrame jf;

	public static void main(String args[]){
		initialize();

		jf = new JFrame("TestAnimation!");
		jf.setBounds(30, 30, WIDTH+5, HEIGHT+29);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		jf.setResizable(false);
		new GamePanel(jf);
		jf.setVisible(true);
	}

	public static void doRestart(){
		jf.removeAll();
		new GamePanel(jf);
		jf.validate();
	}

	private static void initialize(){
		GamePanel.initialize();
		Box.initialize();
	}

}
