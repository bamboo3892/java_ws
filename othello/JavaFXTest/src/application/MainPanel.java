package application;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import api.AISheet;
import api.IGuiRenderer;

public class MainPanel extends JPanel implements IGuiRenderer {

	private static final long serialVersionUID = 1L;

	private static final int panelSize = 604;
	private static Image backGround;
	private static Image blackStone;
	private static Image whiteStone;
	private static Image clearBlackStone;
	private static Image clearWhiteStone;

	private Thread processingThread = new Thread("ProcessingThread");
	private ProssessingSheet pp;
	private float aiProgress = 1.0f;
	private boolean canClick = false;
	private boolean isShowingLog = false;
	private boolean isFinished = false;
	private LinkedList<String> logList = new LinkedList<String>();
	private LinkedList<Color> logColorList = new LinkedList<Color>();

	public static void openNewGamePanel(AISheet ai1, AISheet ai2) throws IOException {
		JFrame jf = new JFrame("Othello " + (ai1 == null ? "Player" : ai1.getAIName()) + " v.s. " + (ai2 == null ? "Player" : ai2.getAIName()));
		jf.setBounds(300, 300, panelSize + 6, panelSize + 35 + 5);
		jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jf.setResizable(false);
		MainPanel panel = null;
		panel = new MainPanel(ai1, ai2);
		if(ai1 != null) ai1.setGuiRenderer(panel);
		if(ai2 != null) ai2.setGuiRenderer(panel);
		jf.getContentPane().add(panel);
		jf.setVisible(true);
	}

	protected MainPanel(AISheet ai1, AISheet ai2) throws IOException {
		setSize(panelSize, panelSize);

		try{
			backGround = ImageIO.read(new File("image/othello.png"));
			blackStone = ImageIO.read(new File("image/blackStone.png"));
			whiteStone = ImageIO.read(new File("image/whiteStone.png"));
			clearBlackStone = ImageIO.read(new File("image/clearBlackStone.png"));
			clearWhiteStone = ImageIO.read(new File("image/clearWhiteStone.png"));
		}catch (IIOException e){
			System.out.println("Cannot load image file.");
			throw e;
		}

		pp = new ProssessingSheet(this, ai1, ai2);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				mouseClickedEvent(me);
			}
		});

		addLog("Game Start", Color.blue);
		this.canClick = true;
		repaint();
	}

	private void mouseClickedEvent(MouseEvent e) {
		if(e.getButton() == 1){
			if(!canClick || isFinished || pp.isProcessing()) return;
			canClick = false;
			int coordX = e.getX();
			int coordY = e.getY();
			int x = (int) ((coordX - 8) / 74.0);
			int y = (int) ((coordY - 8) / 74.0);
			if(x < 0 || x > 7 || y < 0 || y > 7) return;
			pp.setNextPlaceLocation(x, y);
			processingThread = new Thread(pp, "ProcessingThread");
			processingThread.start();
			canClick = true;
		}else if(e.getButton() == 2){
			if(!canClick || isFinished || pp.isProcessing()) return;
			canClick = false;
			pp.back();
			addLog("Call a Halt!", Color.blue);
			canClick = true;
		}else if(e.getButton() == 3){
			canClick = !canClick;
			isShowingLog = !isShowingLog;
		}
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(backGround, 0, 0, this);

		//draw stone
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				if(pp.getBoxForRender(i, j) == 0){
					continue;
				}else if(pp.getBoxForRender(i, j) == 1){
					g.drawImage(blackStone, 8 + 74 * i, 8 + 74 * j, 70, 70, this);
				}else if(pp.getBoxForRender(i, j) == 2){
					g.drawImage(whiteStone, 8 + 74 * i, 8 + 74 * j, 70, 70, this);
				}else if(pp.getBoxForRender(i, j) == 3){
					g.drawImage(clearBlackStone, 8 + 74 * i, 8 + 74 * j, 70, 70, this);
				}else if(pp.getBoxForRender(i, j) == 4){
					g.drawImage(clearWhiteStone, 8 + 74 * i, 8 + 74 * j, 70, 70, this);
				}
			}

		//draw grid
		g.setColor(Color.blue);
		g.drawRect(8 + 74 * pp.getLastPlacedStoneX(), 8 + 74 * pp.getLastPlacedStoneY(), 69, 69);

		//draw progress bar
		g.setColor(Color.black);
		g.fillRect(0, panelSize, (int) (panelSize), 10);
		g.setColor(Color.blue);
		g.fillRect(0, panelSize, (int) (panelSize * aiProgress), 10);

		if(isShowingLog){
			g.setColor(new Color(0F, 0F, 0F, 0.75F));
			g.fillRect(panelSize / 2 - 200, panelSize / 2 - 175, 400, 350);
			g.setFont(new Font(Font.DIALOG, Font.PLAIN, 24));
			int size = logList.size();
			size = size < 14 ? size : 14;
			for (int i = 0; i < size; i++){
				g.setColor(logColorList.get(logList.size() - i - 1));
				g.drawString(logList.get(logList.size() - i - 1), panelSize / 2 - 200 + 4, panelSize / 2 - 175 + i * 25 + 22);
			}
		}
	}

	public synchronized void addLog(String str, Color color) {
		logList.add(str);
		logColorList.add(color);
	}

	protected void calculateAndPrintMessage() {
		int black = 0;
		int white = 0;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				if(pp.getBoxForRender(i, j) == 1){
					black++;
				}else if(pp.getBoxForRender(i, j) == 2){
					white++;
				}
			}
		addLog("Black:" + black + " White:" + white, Color.green);
		if(black > white){
			addLog("Black is Winner!", Color.green);
		}else if(black < white){
			addLog("White is Winner!", Color.green);
		}else{
			addLog("Draw!", Color.green);
		}
		this.canClick = false;
		isShowingLog = true;
		isFinished = true;
		repaint();
	}

	protected void illegalFinishLog(int winner, int illegalX, int illegalY) {
		addLog("on illegal position. (" + illegalX + ", " + illegalY + ")", Color.green);
		addLog((winner == 1 ? "White" : "Black") + " AI try to put a stone", Color.green);
		addLog((winner == 1 ? "Black" : "White") + " is Winner!", Color.green);
		this.canClick = false;
		isShowingLog = true;
		isFinished = true;
		repaint();
	}

	@Override
	public float getProgress() {
		return this.aiProgress;
	}
	@Override
	public void setProgress(float f) {
		this.aiProgress = f;
		repaint();
	}
	@Override
	public void addProgress(float f) {
		this.aiProgress += f;
		if(f > 1f) f = 1f;
		if(f < 0f) f = 0f;
		repaint();
	}

	public enum GameMode {

		NORMAL_BUTTLE(),

		AI_BUTTLE();

		@Override
		public String toString() {
			return super.toString().replace("_", " ");
		}

	}

}
