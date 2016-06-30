package application;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import api.AISheet;
import api.IGuiRenderer;
import application.AIButtleSheet.TurnPolicy;
import javafx.concurrent.Task;
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

	private MessageTask logTask = null;
	private ProgressTask progressTask = null;
	private ExecutorService service = Executors.newSingleThreadExecutor();

	@FXML
	public void initialize() {
		progressTask = new ProgressTask();
		progressBar.progressProperty().bind(progressTask.progressProperty());
		logTask = new MessageTask();
		logArea.textProperty().bind(logTask.messageProperty());
	}

	protected void initData(AISheet ai1, AISheet ai2, int repeat, int saveRate, TurnPolicy turnPolicy) throws FileNotFoundException, ClassNotFoundException, IOException {
		addLearningLog("Preparing...");
		addLearningLog("Repeat = " + repeat + ", Save Rate = " + saveRate + ", Turn Policy = " + turnPolicy);
		sheet = new AIButtleSheet(this, ai1, ai2, repeat, saveRate, turnPolicy);
		addLearningLog("Done");
	}

	@FXML
	public void onFlagButtonClicked(ActionEvent event) {
		String command = flagBtn.getText();
		if(command.equals("Start")){
			learningThread = new Thread(sheet, "Learning Thread");
			learningThread.start();
			flagBtn.setText("Stop");
		}else if(command.equals("Stop")){
			flagBtn.setText("Restart");
		}else if(command.equals("Restart")){
			flagBtn.setText("Stop");
		}
	}

	public void addLearningLog(String message) {
		//		service.submit(logTask.addMessage(logArea.textProperty().get() + message + "\n"));
		System.out.println(message);
	}
	public void setLeaningProgress(float progress) {
		service.submit(progressTask.setProgress(progress));
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

	private class ProgressTask extends Task<Void> {
		protected ProgressTask setProgress(double progress) {
			updateProgress(progress, 1.0);
			return this;
		}
		@Override
		protected Void call() {
			updateProgress(0.0, 1.0);
			return null;
		}
	}

	private class MessageTask extends Task<Void> {
		protected MessageTask addMessage(String message) {
			updateMessage(message);
			return this;
		}
		@Override
		protected Void call() {
			return null;
		}
	}

}
