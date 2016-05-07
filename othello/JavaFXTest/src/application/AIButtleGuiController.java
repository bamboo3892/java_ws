package application;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;

import api.AISheet;
import api.IGuiRenderer;
import application.AIButtleSheet.TurnPolicy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

public class AIButtleGuiController implements IGuiRenderer {

	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label progressTime;
	@FXML
	private Button flagBtn;
	//////////////////////////////////////////////////////////////////////////////
	@FXML
	private TextArea logArea;
	//////////////////////////////////////////////////////////////////////////////
	@FXML
	private LineChart<Float, Integer> rateGraph;
	//////////////////////////////////////////////////////////////////////////////
	@FXML
	private Label stic_match;
	@FXML
	private Label stic_win;
	@FXML
	private Label stic_draw;
	@FXML
	private Label stic_lose;

	private Thread learningThread;
	private AIButtleSheet sheet;

	@FXML
	public void initialize() {

	}

	protected void initData(AISheet ai1, AISheet ai2, int repeat, int saveRate, TurnPolicy turnPolicy) throws FileNotFoundException, ClassNotFoundException, IOException {
		addLearningLog("Preparing...");
		addLearningLog("Repeat = " + repeat + " Save Rate = " + saveRate + "Turn Policy = " + turnPolicy);
		sheet = new AIButtleSheet(this, ai1, ai2, repeat, saveRate, turnPolicy);
	}

	@FXML
	public void onFlagButtonClicked(ActionEvent event) {
		String command = flagBtn.getText();
		if(command.equals("Start")){
			learningThread = new Thread(sheet, "Learning Thread");
			learningThread.start();
			flagBtn.setText("Stop");
		}else if(command.equals("Stop")){
			try{
				learningThread.wait();
				flagBtn.setText("Restart");
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}else if(command.equals("Restart")){
			learningThread.notify();
			flagBtn.setText("Stop");
		}
	}

	public void addLearningLog(String message) {
		logArea.appendText(message + "\n");
	}
	public void setLeaningProgress(float progress) {
		progressBar.setProgress(progress);
	}

	@Override
	public float getProgress() {
		return 0;
	}
	@Override
	public void setProgress(float f) {}
	@Override
	public void addProgress(float f) {}
	@Override
	public void addLog(String str, Color color) {}

}
