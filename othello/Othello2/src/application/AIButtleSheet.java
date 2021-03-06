package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import api.AISheet;
import api.ILearnableAI;
import api.Sheet;

public class AIButtleSheet extends Sheet implements Runnable {

	private AIButtleGuiController controller;
	private FileSaver saver;
	private final AISheet learnable;
	private AISheet otherAI;
	public final int repeat;
	public final int saveRate;
	public final TurnPolicy turnPolicy;

	public AIButtleSheet(AIButtleGuiController gui, AISheet learnable, AISheet otherAI, int repeat, int saveRate, TurnPolicy turnPolicy) throws FileNotFoundException, IOException, ClassNotFoundException {
		if(gui == null || learnable == null || otherAI == null || repeat <= 0 || saveRate <= 0) throw new IllegalArgumentException();
		if(learnable instanceof ILearnableAI && ((ILearnableAI) learnable).canLearnFrom(otherAI)){
			gui.addLearningLog(learnable.getAIName() + " can learn from " + otherAI.getAIName());
		}else{
			gui.addLearningLog(learnable.getAIName() + " cannot learn from " + otherAI.getAIName());
		}
		this.controller = gui;
		this.learnable = learnable;
		this.otherAI = otherAI;
		this.repeat = repeat;
		this.saveRate = saveRate;
		this.turnPolicy = turnPolicy;
		if(learnable instanceof ILearnableAI){
			File saveFile = Lancher.getSaveFileFromClass(learnable.getClass());
			System.out.println("input : " + saveFile.getPath());
			try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(saveFile))){
				Object o = input.readObject();
				((ILearnableAI) learnable).setMetaFromSavedObject(o);
				gui.addLearningLog("Successfully read from saved object.");
			}catch (Exception e){
				e.printStackTrace();
				((ILearnableAI) learnable).setMetaFromSavedObject(null);
			}
			this.saver = new FileSaver(learnable);
		}
	}

	@Override
	public void run() {

		int duel = 0;
		int win = 0;
		for (int i = 0; i < repeat; i++){

			//set other ai.
			try{
				otherAI = otherAI.getClass().newInstance();
			}catch (InstantiationException | IllegalAccessException e){
				System.out.println(otherAI.getAIName() + " does not have default constructor.");
				e.printStackTrace();
				return;
			}

			int originalTeban;
			if(turnPolicy == TurnPolicy.SENTE_FIXED){
				originalTeban = 1;
			}else if(turnPolicy == TurnPolicy.GOTE_FIXED){
				originalTeban = 2;
			}else{
				originalTeban = (int) (Math.random() * 2) + 1;
			}
			int winner = duel(originalTeban);
			controller.addLearningLog("Winner is " + (winner == 1 ? "Sente" : "Gote") + " (" + (winner == originalTeban ? learnable.getAIName() : otherAI.getAIName()) + ")");
			duel++;

			//learn
			if(winner == originalTeban){
				win++;
				if(learnable instanceof ILearnableAI && ((ILearnableAI) learnable).canLearnFrom(otherAI)){
					controller.addLearningLog(learnable.getAIName() + " learn from " + otherAI.getAIName());
					controller.addLearningLog(((ILearnableAI) learnable).learnFrom(otherAI));
				}
			}

			//save
			if(win % saveRate == saveRate - 1 && learnable instanceof ILearnableAI){
				saver.addSaveObject(((ILearnableAI) learnable).getSaveObjectClone());
				controller.addLearningLog("Save: " + learnable.getAIName());
			}

			controller.setLeaningProgress(i / (float) repeat);

		}

		//save
		if(learnable instanceof ILearnableAI){
			saver.addSaveObject(((ILearnableAI) learnable).getSaveObjectClone());
			controller.addLearningLog("Save: " + learnable.getAIName());
		}
		controller.setLeaningProgress(1.0f);
		controller.addLearningLog("Finish!");
		controller.addLearningLog("win rate : " + ((double) win / (double) duel));

	}

	private int duel(int originalTeban) {//return winner
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				box[i][j] = 0;
		box[3][4] = box[4][3] = 1;
		box[3][3] = box[4][4] = 2;

		while (true){

			///////////////////////////////////////////////////////////////////////////
			if(!isTherePlaceToPutStone()){
				teban = teban == 1 ? 2 : 1;
				if(!isTherePlaceToPutStone()){
					return judge();
				}
			}
			///////////////////////////////////////////////////////////////////////////

			AISheet sheet = teban == originalTeban ? learnable : otherAI;
			sheet.set(box, teban);
			sheet.decideNextPlace();
			if(!canPlaceAtAndReverse(sheet.getNextX(), sheet.getNextY())) System.out.println("error?");
			teban = teban == 1 ? 2 : 1;
		}
	}


	private class FileSaver {

		private Thread saveThread = new Thread("Save Thread");
		private ObjectOutputStream output;
		private List<Object> saveList = Collections.synchronizedList(new LinkedList<Object>());

		private FileSaver(AISheet saveAI) throws FileNotFoundException, IOException {
			File saveFile = Lancher.getSaveFileFromClass(saveAI.getClass());
			if(saveFile == null) throw new IllegalArgumentException("This is my fault. I'm sorry.");
			if(!saveFile.exists()){
				saveFile.getParentFile().mkdirs();
				saveFile.createNewFile();
			}
			System.out.println("output : " + saveFile.getPath());
			output = new ObjectOutputStream(new FileOutputStream(saveFile));
		}

		private void addSaveObject(Object object) {
			if(object != null){
				saveList.add(object);
				if(saveThread.getState() == Thread.State.NEW || saveThread.getState() == Thread.State.TERMINATED){
					saveThread = new Thread(() -> {
						while (true){
							System.out.println("try to save");
							if(saveList.isEmpty()) break;
							Object save = saveList.get(0);
							//System.out.println(save);
							try{
								output.reset();
								output.writeObject(save);
							}catch (IOException e){
								System.out.println("Excception while saving.");
								System.out.println("Stop saving.");
								e.printStackTrace();
								break;
							}
							saveList.remove(0);
						}
					}, "Save Thread");
					saveThread.start();
				}
			}
		}
	}

	public enum TurnPolicy {

		RANDOM_TURN(),

		SENTE_FIXED(),

		GOTE_FIXED();

		@Override
		public String toString() {
			return super.toString().replace("_", " ");
		}

	}

}








