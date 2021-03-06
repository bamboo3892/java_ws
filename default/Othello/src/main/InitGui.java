package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import api.AISheet;
import main.AIChooseGui.IAIChooser;

public class InitGui extends JFrame implements MouseListener, ActionListener, IAIChooser {

	private static final long serialVersionUID = 2L;

	private JTabbedPane contentPane;
	private JPanel gamePanel;
	private JPanel learningPanel;
	private JCheckBox cBox1;
	private JCheckBox cBox2;
	private JLabel chooseAI1;
	private JLabel chooseAI2;
	private JButton btnGameStart;

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
		JPanel panel1 = new JPanel();
		gamePanel.add(panel1, BorderLayout.NORTH);
		panel1.setLayout(new GridLayout(0, 1, 0, 0));

		JLabel label1 = new JLabel("先手------------------------------------------------------------------------");
		panel1.add(label1);
		cBox1 = new JCheckBox("AIを使う");
		cBox1.addActionListener(this);
		panel1.add(cBox1);
		chooseAI1 = new JLabel("AIを選択");
		chooseAI1.setEnabled(false);
		chooseAI1.setBorder(new EmptyBorder(5, 5, 5, 5));
		chooseAI1.addMouseListener(this);
		panel1.add(chooseAI1);

		//gote
		JPanel panel2 = new JPanel();
		gamePanel.add(panel2, BorderLayout.CENTER);
		panel2.setLayout(new GridLayout(0, 1, 0, 0));

		JLabel label2 = new JLabel("後手------------------------------------------------------------------------");
		panel2.add(label2);
		cBox2 = new JCheckBox("AIを使う");
		cBox2.addActionListener(this);
		panel2.add(cBox2);
		chooseAI2 = new JLabel("AIを選択");
		chooseAI2.setEnabled(false);
		chooseAI2.setBorder(new EmptyBorder(5, 5, 5, 5));
		chooseAI2.addMouseListener(this);
		panel2.add(chooseAI2);

		btnGameStart = new JButton("game start");
		btnGameStart.setActionCommand("game start");
		btnGameStart.addActionListener(this);
		gamePanel.add(btnGameStart, BorderLayout.SOUTH);

		contentPane.addTab("Game", gamePanel);

		//learning//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		learningPanel = new JPanel();

		contentPane.addTab("Learning", learningPanel);

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
					Class<? extends AISheet> aiClass = Start.AIMap.get(chooseAI1.getText());
					if(aiClass != null){
						ai1 = aiClass.newInstance();
					}
				}
				if(cBox2.isSelected()){
					Class<? extends AISheet> aiClass = Start.AIMap.get(chooseAI2.getText());
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
			chooseAI1.setEnabled(cBox1.isSelected());
		}else if(e.getSource() == cBox2){
			chooseAI2.setEnabled(cBox2.isSelected());
		}
	}

	@Override
	public void setAIName(int ID, String AIName) {
		if(ID == 1){
			chooseAI1.setText(AIName);
		}else if(ID == 2){
			chooseAI2.setText(AIName);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount() == 2){
			if(e.getSource() == chooseAI1 && chooseAI1.isEnabled()){
				new AIChooseGui(this, 1);
			}else if(e.getSource() == chooseAI2 && chooseAI2.isEnabled()){
				new AIChooseGui(this, 2);
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




