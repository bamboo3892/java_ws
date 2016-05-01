package test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Test extends JPanel implements ActionListener{
	
	static JFrame jf;
	static Test jp;
	static Timer timer;
	static int fps = 20;
	int n=0;
	
	public static void main(String args[]){
		
		jp = new Test();
		jp.setSize(400,600);
		timer = new Timer(1000/fps,jp);
		timer.start();
		jf = new JFrame("Test");
		jf.setBounds(30, 30, 418, 647);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE );
		jf.add(jp);
		jf.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
	public void paintComponent(Graphics g){
		g.setColor(jp.getBackground());
		g.fillRect(n-1, n-1, 30, 30);
		g.setColor(Color.red);
		g.fillRect(n, n, 30, 30);
		n++;
	}

}
