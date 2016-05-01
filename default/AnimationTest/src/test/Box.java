package test;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Box {

	public static BufferedImage BlockImage[] = new BufferedImage[7];
	private int x;
	private int y;
	private int color = 7;
	private boolean visible = true;

	public Box(int x, int y){
		this.x = x;
		this.y = y;
	}

	public void setColor(int parColor){
		this.color = parColor;
	}

	public int getColor(){
		return color;
	}

	public boolean getVisible(){
		return visible;
	}

	public void setVisible(boolean b){
		this.visible = b;
	}

	public void paint(Graphics g){
		if(color == 7 || !visible){
			return;
		}else{
			g.drawImage(BlockImage[color], 24+36*x, 24+36*y, null);
		}
	}

	public static void initialize(){
		//Blockの画像を初期化
		try {
			BlockImage[0] = ImageIO.read(new File("orange.png"));
			BlockImage[1] = ImageIO.read(new File("blue.png"));
			BlockImage[2] = ImageIO.read(new File("yellow.png"));
			BlockImage[3] = ImageIO.read(new File("pink.png"));
			BlockImage[4] = ImageIO.read(new File("green.png"));
			BlockImage[5] = ImageIO.read(new File("red.png"));
			BlockImage[6] = ImageIO.read(new File("purple.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
