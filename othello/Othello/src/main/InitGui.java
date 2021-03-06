package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import api.AISheet;
import main.AIChooseGui.IAIChooser;

public class InitGui extends JFrame implements MouseListener, ActionListener, IAIChooser {

	private static final long serialVersionUID = 2L;

	private JTabbedPane contentPane;

	private JPanel gamePanel;
	private JCheckBox cBox1;
	private JCheckBox cBox2;
	private JLabel chooseAI_game_1;
	private JLabel chooseAI_game_2;
	private JButton btnGameStart;

	private JPanel aiButtlePanel;
	private JLabel chooseAI_buttle_1;
	private JLabel chooseAI_buttle_2;
	private JTextField repeat;
	private JTextField saveRate;
	private ButtonGroup tebanCommand;
	private JButton buttleBtn;

	public InitGui() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 326, 379);
		contentPane = new JTabbedPane();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		//game//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		gamePanel = new JPanel();
		gamePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		gamePanel.setLayout(new BorderLayout(0, 0));

		//sente
		JPanel panel_game_1 = new JPanel();
		panel_game_1.setLayout(new GridLayout(0, 1, 0, 0));
		gamePanel.add(panel_game_1, BorderLayout.CENTER);

		JLabel label_game_1 = new JLabel("sente------------------------------------------------------------------------");
		panel_game_1.add(label_game_1);
		cBox1 = new JCheckBox("use AI");
		cBox1.addActionListener(this);
		panel_game_1.add(cBox1);
		chooseAI_game_1 = new JLabel("choose AI");
		chooseAI_game_1.setEnabled(false);
		chooseAI_game_1.setBorder(new EmptyBorder(5, 5, 5, 5));
		chooseAI_game_1.addMouseListener(this);
		panel_game_1.add(chooseAI_game_1);

		//gote
		JLabel label_game_2 = new JLabel("gote------------------------------------------------------------------------");
		panel_game_1.add(label_game_2);
		cBox2 = new JCheckBox("use AI");
		cBox2.addActionListener(this);
		panel_game_1.add(cBox2);
		chooseAI_game_2 = new JLabel("choose AI");
		chooseAI_game_2.setEnabled(false);
		chooseAI_game_2.setBorder(new EmptyBorder(5, 5, 5, 5));
		chooseAI_game_2.addMouseListener(this);
		panel_game_1.add(chooseAI_game_2);

		btnGameStart = new JButton("game start");
		btnGameStart.setActionCommand("game start");
		btnGameStart.addActionListener(this);
		gamePanel.add(btnGameStart, BorderLayout.SOUTH);

		contentPane.addTab("Game", gamePanel);

		//ai buttle////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		aiButtlePanel = new JPanel();
		aiButtlePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		aiButtlePanel.setLayout(new BorderLayout(0, 0));

		JPanel panel_buttle_1 = new JPanel();
		panel_buttle_1.setLayout(new GridLayout(0, 1, 0, 0));
		aiButtlePanel.add(panel_buttle_1, BorderLayout.CENTER);

		JLabel label_buttle_sente = new JLabel("Learning AI------------------------------------------------------------------------");
		panel_buttle_1.add(label_buttle_sente);

		chooseAI_buttle_1 = new JLabel("choose AI");
		chooseAI_buttle_1.setBorder(new EmptyBorder(5, 5, 5, 5));
		chooseAI_buttle_1.addMouseListener(this);
		panel_buttle_1.add(chooseAI_buttle_1);

		JLabel label_buttle_gote = new JLabel("Other AI------------------------------------------------------------------------");
		panel_buttle_1.add(label_buttle_gote);

		chooseAI_buttle_2 = new JLabel("choose AI");
		chooseAI_buttle_2.setBorder(new EmptyBorder(5, 5, 5, 5));
		chooseAI_buttle_2.addMouseListener(this);
		panel_buttle_1.add(chooseAI_buttle_2);

		repeat = new JTextField("Number of Repeat");
		panel_buttle_1.add(repeat);

		saveRate = new JTextField("Save Rate");
		panel_buttle_1.add(saveRate);

		tebanCommand = new ButtonGroup();
		JRadioButton btn_buttle_0 = new JRadioButton("Teban Random");
		btn_buttle_0.setActionCommand("0");
		tebanCommand.add(btn_buttle_0);
		panel_buttle_1.add(btn_buttle_0);
		JRadioButton btn_buttle_1 = new JRadioButton("Sente Fixed");
		btn_buttle_1.setActionCommand("1");
		tebanCommand.add(btn_buttle_1);
		panel_buttle_1.add(btn_buttle_1);
		JRadioButton btn_buttle_2 = new JRadioButton("Gote Fixed");
		btn_buttle_2.setActionCommand("2");
		tebanCommand.add(btn_buttle_2);
		panel_buttle_1.add(btn_buttle_2);

		buttleBtn = new JButton("Start");
		buttleBtn.addActionListener(this);
		aiButtlePanel.add(buttleBtn, BorderLayout.SOUTH);

		contentPane.addTab("AI Buttle", aiButtlePanel);

		//option//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnGameStart){
			try{
				contentPane.setEnabled(false);
				this.setVisible(false);
				AISheet ai1 = null;
				AISheet ai2 = null;
				if(cBox1.isSelected()){
					Class<? extends AISheet> aiClass = Start.AIMap.get(chooseAI_game_1.getText());
					if(aiClass != null){
						ai1 = aiClass.newInstance();
					}
				}
				if(cBox2.isSelected()){
					Class<? extends AISheet> aiClass = Start.AIMap.get(chooseAI_game_2.getText());
					if(aiClass != null){
						ai2 = aiClass.newInstance();
					}
				}
				JFrame jf = new JFrame("Othello " + (ai1 == null ? "Player" : ai1.getAIName()) + " v.s. " + (ai2 == null ? "Player" : ai2.getAIName()));
				jf.setBounds(300, 300, Start.panelSize + 6, Start.panelSize + 35 + 5);
				jf.setDefaultCloseOperation(3);
				jf.setResizable(false);
				MainPanel panel = null;
				panel = new MainPanel(ai1, ai2);
				if(ai1 != null) ai1.setGuiRenderer(panel);
				if(ai2 != null) ai2.setGuiRenderer(panel);
				jf.getContentPane().add(panel);
				jf.setVisible(true);
			}catch (Exception e1){
				System.err.println("Error");
				e1.printStackTrace();
			}
		}else if(e.getSource() == cBox1){
			chooseAI_game_1.setEnabled(cBox1.isSelected());
		}else if(e.getSource() == cBox2){
			chooseAI_game_2.setEnabled(cBox2.isSelected());
		}else if(e.getSource() == buttleBtn){
			contentPane.setEnabled(false);
			this.setVisible(false);
			AIButtleGui gui = null;
			try{
				AISheet ai1 = Start.AIMap.get(chooseAI_buttle_1.getText()).newInstance();
				AISheet ai2 = Start.AIMap.get(chooseAI_buttle_2.getText()).newInstance();
				int repeat = Integer.valueOf(this.repeat.getText());
				int saveRate = Integer.valueOf(this.saveRate.getText());
				int tebanCommnad = Integer.valueOf(this.tebanCommand.getSelection().getActionCommand());
				gui = new AIButtleGui(ai1, ai2, 0, repeat, saveRate, tebanCommnad);
				ai1.setGuiRenderer(gui);
				ai2.setGuiRenderer(gui);
			}catch (Exception e1){
				e1.printStackTrace();
				return;
			}
			if(gui != null) gui.setVisible(true);
		}
	}

	@Override
	public void setAIName(int ID, String AIName) {
		if(ID == 1){
			chooseAI_game_1.setText(AIName);
		}else if(ID == 2){
			chooseAI_game_2.setText(AIName);
		}else if(ID == 3){
			chooseAI_buttle_1.setText(AIName);
		}else if(ID == 4){
			chooseAI_buttle_2.setText(AIName);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2){
			if(e.getSource() == chooseAI_game_1 && chooseAI_game_1.isEnabled()){
				new AIChooseGui(this, 1);
			}else if(e.getSource() == chooseAI_game_2 && chooseAI_game_2.isEnabled()){
				new AIChooseGui(this, 2);
			}else if(e.getSource() == chooseAI_buttle_1){
				new AIChooseGui(this, 3);
			}else if(e.getSource() == chooseAI_buttle_2){
				new AIChooseGui(this, 4);
			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

}




