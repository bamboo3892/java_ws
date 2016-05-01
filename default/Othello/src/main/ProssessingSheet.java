package main;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import ai.AISheet;

public class ProssessingSheet extends Sheet implements Runnable {

	private boolean player[] = new boolean[3];
	private MyPanel panel;
	private AISheet AI1;
	private AISheet AI2;
	private List<Sheet> history = new LinkedList<Sheet>();
	private int currentHistoryPointer = 0;
	private boolean isProcessing = false;
	private int nextX;
	private int nextY;
	private int lastPlacedStoneX;
	private int lastPlacedStoneY;

	public ProssessingSheet(MyPanel panel, AISheet ai1, AISheet ai2) {
		super();
		box[3][4] = box[4][3] = 1;
		box[3][3] = box[4][4] = 2;
		this.panel = panel;
		this.teban = 1;
		player[1] = ai1 == null;
		player[2] = ai2 == null;
		AI1 = ai1;
		AI2 = ai2;
		history.add(clone());
	}

	public void setNextPlaceLocation(int x, int y) {
		this.nextX = x;
		this.nextY = y;
	}

	@Override
	public void run() {
		if(!isProcessing){
			isProcessing = true;
			next(nextX, nextY);
			nextX = -999;
			nextY = -999;
			isProcessing = false;
		}
	}
	private void next(int x, int y) {//called only by clicking

		boolean dealParamCoord = false;//should wait next player's click

		while (true){

			///////////////////////////////////////////////////////////////////////////
			if(isFilled()){
				panel.addLog("Game Finished", Color.green);
				panel.calculateAndPrintMessage();
				return;
			}
			if(!isTherePlaceToPutStone()){
				teban = teban == 1 ? 2 : 1;
				if(!isTherePlaceToPutStone()){
					panel.addLog("Both can't place stone at any place.", Color.green);
					panel.calculateAndPrintMessage();
					return;
				}
				panel.addLog((teban == 1 ? "White" : "Black") + " can't place stone at any place.", Color.red);
			}
			///////////////////////////////////////////////////////////////////////////

			if(player[teban]){//player
				if(dealParamCoord) return;
				if(!canPlaceAtAndReverse(x, y)){
					System.out.println("Can't place");
					return;
				}
				panel.addLog("(" + (teban == 1 ? "Black" : "White") + ")Place:" + x + ", " + y, Color.white);
				lastPlacedStoneX = x;
				lastPlacedStoneY = y;
				teban = teban == 1 ? 2 : 1;
				currentHistoryPointer++;
				history = getSubList(history, 0, currentHistoryPointer);
				history.add(clone());
				panel.repaint();
				dealParamCoord = true;
			}else{//ai
				AISheet sheet = getAISheet(box, teban);
				panel.setProgress(0f);
				sheet.decideNextPlace();
				if(!canPlaceAtAndReverse(sheet.getNextX(), sheet.getNextY())){
					panel.illegalFinishLog(teban == 1 ? 2 : 1, sheet.getNextX(), sheet.getNextY());
					return;
				}
				panel.addLog("(" + (teban == 1 ? "Black" : "White") + ")Place:" + sheet.getNextX() + "," + sheet.getNextY(), Color.white);
				lastPlacedStoneX = sheet.getNextX();
				lastPlacedStoneY = sheet.getNextY();
				teban = teban == 1 ? 2 : 1;
				currentHistoryPointer++;
				history = getSubList(history, 0, currentHistoryPointer);
				history.add(clone());
				panel.setProgress(1f);
				panel.repaint();
			}
		}
	}

	public void back() {
		if(currentHistoryPointer > 0){
			currentHistoryPointer--;
			set(history.get(currentHistoryPointer));
		}
	}

	private AISheet getAISheet(int[][] box2, int teban2) {
		if(teban2 == 1){
			return (AISheet) AI1.set(box2, teban2);
		}else{
			return (AISheet) AI2.set(box2, teban2);
		}
	}

	public int getLastPlacedStoneX() {
		return lastPlacedStoneX;
	}
	public int getLastPlacedStoneY() {
		return lastPlacedStoneY;
	}

	/***/
	public int getBoxForRender(int x, int y) {
		if(x < 0 || x > 7 || y < 0 || y > 7) return 0;
		if(box[x][y] == 0){
			return canPlace(x, y) ? teban + 2 : 0;
		}else{
			return box[x][y];
		}
	}

	public boolean isProcessing() {
		return this.isProcessing;
	}

	public static <T> List<T> getSubList(List<T> list, int fromIndex, int toIndex) {
		List<T> rList = new LinkedList<T>();
		for (int i = fromIndex; i < toIndex; i++){
			rList.add(list.get(i));
		}
		return rList;
	}

}





