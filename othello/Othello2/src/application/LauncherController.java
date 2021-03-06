package application;

import api.AISheet;
import application.AIButtleSheet.TurnPolicy;
import application.Lancher.AIAttribute;
import application.MainPanel.GameMode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LauncherController {

	@FXML
	private ComboBox<GameMode> cBoxGameMode;
	@FXML
	private Label goteLabel;
	@FXML
	private Label senteLabel;
	@FXML
	private ComboBox<AIAttribute> senteAI;
	@FXML
	private ComboBox<AIAttribute> goteAI;
	@FXML
	private TextArea senteInfo;
	@FXML
	private TextArea goteInfo;
	@FXML
	FlowPane learnCfgPane;
	@FXML
	TextField repeat;
	@FXML
	TextField saveRate;
	@FXML
	ComboBox<TurnPolicy> turnPolicy;
	@FXML
	private Button startBtn;
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@FXML
	private TableView<AIAttribute> aiTable;
	@FXML
	TableColumn<AIAttribute, String> nameColumn;
	@FXML
	TableColumn<AIAttribute, String> versionColumn;
	@FXML
	TableColumn<AIAttribute, String> authorColumn;
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@FXML
	public void initialize() {
		cBoxGameMode.getItems().clear();
		cBoxGameMode.getItems().addAll(GameMode.values());
		cBoxGameMode.getSelectionModel().select(0);

		turnPolicy.getItems().addAll(TurnPolicy.values());
		turnPolicy.getSelectionModel().select(0);

		for (AIAttribute attribute : Lancher.AIList){
			senteAI.getItems().add(attribute);
			goteAI.getItems().add(attribute);
			aiTable.getItems().add(attribute);
		}
		senteAI.getSelectionModel().select(0);
		goteAI.getSelectionModel().select(0);

		nameColumn.setCellValueFactory(new PropertyValueFactory<AIAttribute, String>("name"));
		versionColumn.setCellValueFactory(new PropertyValueFactory<AIAttribute, String>("version"));
		authorColumn.setCellValueFactory(new PropertyValueFactory<AIAttribute, String>("author"));

		updateStartBtnState();
	}

	@FXML
	public void onGameModeChanged(ActionEvent event) {
		updateStartBtnState();
	}

	@FXML
	public void onSenteAIChanged(ActionEvent event) {
		updateStartBtnState();
	}

	@FXML
	public void onGoteAIChanged(ActionEvent event) {
		updateStartBtnState();
	}

	@FXML
	public void onStartButtonClicked(ActionEvent event) {
		updateStartBtnState();
		if(!startBtn.isDisabled()){
			if(cBoxGameMode.getSelectionModel() != null && cBoxGameMode.getSelectionModel().getSelectedItem() != null){
				switch (cBoxGameMode.getSelectionModel().getSelectedItem()) {
				case NORMAL_BUTTLE:
					try{
						AISheet ai1 = null;
						AISheet ai2 = null;
						AIAttribute aiClass1 = senteAI.getSelectionModel() == null ? null : senteAI.getSelectionModel().getSelectedItem();
						if(aiClass1 != null && aiClass1.alClass != null){
							ai1 = aiClass1.alClass.newInstance();
						}
						AIAttribute aiClass2 = goteAI.getSelectionModel() == null ? null : goteAI.getSelectionModel().getSelectedItem();
						if(aiClass2 != null && aiClass2.alClass != null){
							ai2 = aiClass2.alClass.newInstance();
						}
						MainPanel.openNewGamePanel(ai1, ai2);
					}catch (Exception e1){
						System.err.println("Error");
						e1.printStackTrace();
					}
					break;
				case AI_BUTTLE:
					//					AIButtleGui gui = null;
					//					try{
					//						AISheet ai1 = null;
					//						AISheet ai2 = null;
					//						AIAttribute aiClass1 = senteAI.getSelectionModel() == null ? null : senteAI.getSelectionModel().getSelectedItem();
					//						if(aiClass1 != null && aiClass1.alClass != null){
					//							ai1 = aiClass1.alClass.newInstance();
					//						}
					//						AIAttribute aiClass2 = goteAI.getSelectionModel() == null ? null : goteAI.getSelectionModel().getSelectedItem();
					//						if(aiClass2 != null && aiClass2.alClass != null){
					//							ai2 = aiClass2.alClass.newInstance();
					//						}
					//						int repeat = Integer.valueOf(this.repeat.getText());
					//						int saveRate = Integer.valueOf(this.saveRate.getText());
					//						int turnPolicy = this.turnPolicy.getSelectionModel().getSelectedItem().ID;
					//						gui = new AIButtleGui(ai1, ai2, 0, repeat, saveRate, turnPolicy);
					//						ai1.setGuiRenderer(gui);
					//						ai2.setGuiRenderer(gui);
					//					}catch (Exception e1){
					//						e1.printStackTrace();
					//						return;
					//					}
					//					if(gui != null) gui.setVisible(true);
					try{
						AISheet ai1 = null;
						AISheet ai2 = null;
						AIAttribute aiClass1 = senteAI.getSelectionModel() == null ? null : senteAI.getSelectionModel().getSelectedItem();
						if(aiClass1 != null && aiClass1.alClass != null){
							ai1 = aiClass1.alClass.newInstance();
						}else{
							return;
						}
						AIAttribute aiClass2 = goteAI.getSelectionModel() == null ? null : goteAI.getSelectionModel().getSelectedItem();
						if(aiClass2 != null && aiClass2.alClass != null){
							ai2 = aiClass2.alClass.newInstance();
						}else{
							return;
						}
						int repeat = Integer.valueOf(this.repeat.getText());
						int saveRate = Integer.valueOf(this.saveRate.getText());
						TurnPolicy turnPolicy = this.turnPolicy.getSelectionModel().getSelectedItem();

						FXMLLoader loader = new FXMLLoader(getClass().getResource("AIButtle.fxml"));
						Scene scene = new Scene((Pane) loader.load());
						scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
						AIButtleGuiController controller = loader.<AIButtleGuiController> getController();
						controller.initData(ai1, ai2, repeat, saveRate, turnPolicy);
						Stage stage = new Stage();
						stage.setTitle("AI Buttle   " + ai1.getAIName() + " v.s. " + ai2.getAIName());
						stage.setScene(scene);
						stage.setResizable(false);
						stage.show();

						((Node) (event.getSource())).getScene().getWindow().hide();
					}catch (Exception e){
						e.printStackTrace();
					}
					break;
				default:
					break;
				}
			}
		}
	}

	private void updateStartBtnState() {
		if(cBoxGameMode.getSelectionModel() != null && cBoxGameMode.getSelectionModel().getSelectedItem() != null){
			if(cBoxGameMode.getSelectionModel().getSelectedItem() == GameMode.NORMAL_BUTTLE){
				learnCfgPane.setDisable(true);
				startBtn.setDisable(senteAI.selectionModelProperty() == null || goteAI.selectionModelProperty() == null);
			}else if(cBoxGameMode.getSelectionModel().getSelectedItem() == GameMode.AI_BUTTLE){
				learnCfgPane.setDisable(false);
				if(isValidSelected(senteAI) && isValidSelected(goteAI)){
					try{
						int repeat = Integer.valueOf(String.valueOf(this.repeat.getCharacters()));
						int saveRate = Integer.valueOf(String.valueOf(this.saveRate.getCharacters()));
						if(repeat > 0 && saveRate > 0){
							startBtn.setDisable(false);
							return;
						}else{
							startBtn.setDisable(true);
						}
					}catch (Exception e){
						startBtn.setDisable(true);
					}
				}
				startBtn.setDisable(true);
			}
		}else{
			startBtn.setDisable(true);
			learnCfgPane.setDisable(true);
		}
	}

	private static boolean isValidSelected(ComboBox<AIAttribute> cBox) {
		return cBox != null && cBox.getSelectionModel() != null && cBox.getSelectionModel().getSelectedItem() != null && !cBox.getSelectionModel().getSelectedItem().equals(AIAttribute.PLAYER);
	}

}
