package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

public class LauncherController {

	@FXML
	private ComboBox<String> cBoxGameMode;
	@FXML
	private Label goteLabel;
	@FXML
	private Label senteLabel;
	@FXML
	private ComboBox<?> senteAI;
	@FXML
	private ComboBox<?> goteAI;
	@FXML
	private TextArea senteInfo;
	@FXML
	private TextArea goteInfo;
	@FXML
	private Button startBtn;
	@FXML
	private TableView<?> aiTable;

	@FXML
	public void initialize() {
		assert cBoxGameMode != null : "fx:id=\"cBoxGameMode\" was not injected: check your FXML file 'DesignFile.fxml'.";
		cBoxGameMode.getItems().clear();
		cBoxGameMode.getItems().addAll("John Lennon", "Mick Jagger", "David Bowie");
		cBoxGameMode.getItems().add("Others...");

		//add ai to combo box
	}

	@FXML
	public void onSenteAIChanged(ActionEvent event) {
		System.out.println("Controller: sente");
		updateStartBtnState();
	}

	@FXML
	public void onGoteAIChanged(ActionEvent event) {
		System.out.println("Controller: gote");
		updateStartBtnState();
	}

	@FXML
	public void onGameModeChanged(ActionEvent event) {
		System.out.println("Controller: mode");
		updateStartBtnState();
	}

	@FXML
	public void onStartButtonClicked(ActionEvent event) {

	}

	private void updateStartBtnState() {
		System.out.println("update start button state");
	}

}
