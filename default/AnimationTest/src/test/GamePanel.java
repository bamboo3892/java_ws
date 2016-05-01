package test;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, KeyListener{

	private static final int FPS = 20;
	private static BufferedImage[] ImageNumber = new BufferedImage[10];
	private final String path = "BackGround.png";

	private BufferedImage backGround;
	private Timer timer;
	private Box box[][] = new Box[10][20];
	private int score = 0;
	private int count = 0;
	private int blankTimeCount = 20;

	private Blocks blocks;
	private int nextBlocks;

	private int pressingKer;
	private int pressingCount;

	private boolean isStop;
	private boolean isDeleting;
	private int deletingCount = 0;
	private String deletingLines = "00000000000000000000";//左から０行目
	private boolean isFinish;

	public GamePanel(JFrame jf) {
		try {
			backGround = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		setSize(Main.WIDTH,Main.HEIGHT);
		//いろいろ初期化
		timer = new Timer(1000/FPS,this);
		timer.start();
		this.blocks = new Blocks(this);
		for(int i=0;i<10;i++){
			for(int j=0;j<20;j++){
				box[i][j] = new Box(i, j);
			}
		}
		jf.add(this);
		jf.addKeyListener(this);
	}

	public void paintComponent(Graphics g) {
		//背景描写/////////////////////////////////
		paintBackground(g);

		//Blockを動かす処理///////////////////////
		if (!isDeleting && !isStop && count>=blankTimeCount ) {
			count = 0;
			String command = blocks.nextDeal();
			if(command == "prepare"){
				//別に何もしない
			}else if (command == "fall") {
				//何もしない
			} else if (command == "place") {
				//何もしない
			}else if(command == "gameover"){
				isStop = true;
			}else{
				System.out.println("Unknowen command : "+command);
			}
		}else if(isDeleting){
			deleteLine();
		}

		//Blockを描画//////////////////////////////
		if(!isDeleting) blocks.paint(g);
		for(int i=0;i<10;i++){
			for(int j=0;j<20;j++){
				box[i][j].paint(g);
			}
		}

		//ストップ画面を描画//////////////////////
		if(isStop) paintStopPanel(g);

		//スコアを描画/////////////////////////////
		paintNumber(g);

		//NEXTを描画//////////////////////////////
		paintNEXT(g);

		//カウントアップ///////////////////////////
		count++;
	}

	public void checkAndDeleteLine() {
		deletingLines = "";
		for(int i=0;i<20;i++){
			boolean b = true;
			for(int j=0;j<10;j++){
				if(box[j][i].getColor() == 7) b = false;
			}
			if(b){
				deletingLines += "1";
				isDeleting = true;
				score++;
			}else{
				deletingLines += "0";
			}
		}
		if(isDeleting){
			deleteLine();
		}
	}

	private void deleteLine(){
		if(deletingCount >= 16){
			//消えたところを下に落とす処理
			for(int i=0;i<10;i++){
				String str ="";
				for(int j=0;j<20;j++){
					if(deletingLines.charAt(19-j) != 49){
						str += box[i][19-j].getColor();
					}
					box[i][19-j].setColor(7);
					box[i][19-j].setVisible(true);
				}
				for(int j=0;j<str.length();j++){
					box[i][19-j].setColor(str.charAt(j)-48);
				}
			}
			isDeleting = false;
			count = blankTimeCount-4;
			deletingCount = 0;
			blocks.nextPrepare();
			return;
		}
		if(deletingCount%4 == 0){
			for(int i=0;i<deletingLines.length();i++){
				if(deletingLines.charAt(i) == 49){
					for(int j= 0;j<10;j++){
						box[j][i].setVisible(!box[j][i].getVisible());
					}
				}
			}
		}
		deletingCount++;
	}

	private void paintNumber(Graphics g) {
		String score = String.valueOf(this.score);
		for(int i=0;i<score.length();i++){
			int n = score.charAt(score.length()-i-1) - 48;
			g.drawImage(ImageNumber[n], 652-i*60, 662, null);
		}
	}

	private void paintNEXT(Graphics g){
		//nextBlockを使ってNEXTを描画
		for(int i=0;i<4;i++){
			int x = Blocks.Shape[nextBlocks][0][i][0];
			int y = Blocks.Shape[nextBlocks][0][i][1];
			g.drawImage(Box.BlockImage[nextBlocks], 513+36*x, 142+36*y, null);
		}
	}

	private void paintBackground(Graphics g) {
		g.drawImage(backGround, 0, 0, this);
	}

	private void paintStopPanel(Graphics g){
		g.fillRect(Main.WIDTH/2-150, Main.HEIGHT/2-100, 300, 200);
	}

	public void setAfterPlaceCount(){
		count = blankTimeCount - 10;
	}

	public void setBeforePlaceCount(){
		count = (int)(-blankTimeCount/2.0);
	}

	public void setBlankTimeCount(int n){
		this.blankTimeCount = n;
	}

	public Box[][] getBoxes(){
		return box;
	}

	public int getNextBlocks(){
		return nextBlocks;
	}

	public void setNextBlocks(int type){
		this.nextBlocks = type;
	}

	public static void initialize(){
		for(int i=0;i<10;i++){
			try {
				ImageNumber[i] = ImageIO.read(new File(i+".png"));
			} catch (IOException e) {e.printStackTrace();}
		}
	}

	public static void doFinish(){
		System.exit(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//this.score = e.getKeyCode();
		int key = e.getKeyCode();
		switch (key){

		case 37://左
			if(!isStop || !isFinish) blocks.moveLeft();
			break;
		case 38://上
			if(!isStop || !isFinish) blocks.fallAll();
			break;
		case 39://右
			if(!isStop || !isFinish) blocks.moveRight();
			break;
		case 40://下
			if(!isStop || !isFinish) blocks.fall();
			break;
		case 90://Zが押された
			if(!isStop || !isFinish) blocks.turnLeft();
			break;
		case 88://Xが押された
			if(!isStop || !isFinish) blocks.turnRight();
			break;
		case 67://Cが押された
			if(!isStop || !isFinish) blocks.fallAll();
			break;

		case 27: //Escが押された
			isStop = !isStop;
			break;
		case 69://Eが押された
			if(isStop || isFinish) doFinish();
			break;
		case 82://Rが押された
			if(isStop || isFinish) //Main.doRestart();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
