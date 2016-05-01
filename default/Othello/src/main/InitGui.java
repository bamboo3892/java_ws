package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map.Entry;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import ai.AISheet;

public class InitGui extends JFrame implements ActionListener {

	private static final long serialVersionUID = 2L;

	private JPanel contentPane;
	private JCheckBox cBox1;
	private JCheckBox cBox2;
	private ButtonGroup group1;
	private ButtonGroup group2;

	public InitGui() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 326, 379);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		//sente
		JPanel panel1 = new JPanel();
		contentPane.add(panel1, BorderLayout.NORTH);
		panel1.setLayout(new GridLayout(0, 1, 0, 0));

		JLabel label1 = new JLabel("先手------------------------------------------------------------------------");
		panel1.add(label1);
		cBox1 = new JCheckBox("AIを使う");
		panel1.add(cBox1);
		group1 = new ButtonGroup();

		for (Entry<String, Class<? extends AISheet>> entry : Start.AIMap.entrySet()){
			JRadioButton rButton = new JRadioButton(entry.getKey());
			rButton.setActionCommand(entry.getKey());
			panel1.add(rButton);
			group1.add(rButton);
		}

		//gote
		JPanel panel2 = new JPanel();
		contentPane.add(panel2, BorderLayout.CENTER);
		panel2.setLayout(new GridLayout(0, 1, 0, 0));

		JLabel label2 = new JLabel("後手------------------------------------------------------------------------");
		panel2.add(label2);

		cBox2 = new JCheckBox("AIを使う");
		panel2.add(cBox2);
		group2 = new ButtonGroup();

		for (Entry<String, Class<? extends AISheet>> entry : Start.AIMap.entrySet()){
			JRadioButton rButton = new JRadioButton(entry.getKey());
			rButton.setActionCommand(entry.getKey());
			panel2.add(rButton);
			group2.add(rButton);
		}

		JButton btnStart = new JButton("start");
		btnStart.addActionListener(this);
		contentPane.add(btnStart, BorderLayout.SOUTH);

		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try{
			contentPane.setEnabled(false);
			this.setVisible(false);
			AISheet ai1 = null;
			AISheet ai2 = null;
			if(cBox1.isSelected() && group1.getSelection() != null){
				Class<? extends AISheet> aiClass = Start.AIMap.get(group1.getSelection().getActionCommand());
				if(aiClass != null){
					ai1 = aiClass.newInstance();
				}
			}
			if(cBox2.isSelected() && group2.getSelection() != null){
				Class<? extends AISheet> aiClass = Start.AIMap.get(group2.getSelection().getActionCommand());
				if(aiClass != null){
					ai2 = aiClass.newInstance();
				}
			}
			JFrame jf = new JFrame("Othello");
			jf.setBounds(300, 300, Start.panelSize + 6, Start.panelSize + 35);
			jf.setDefaultCloseOperation(3);
			jf.setResizable(false);
			MyPanel panel = null;
			panel = new MyPanel(ai1, ai2);
			if(ai1 != null) ai1.setGuiRenderer(panel);
			if(ai2 != null) ai2.setGuiRenderer(panel);
			jf.getContentPane().add(panel);
			jf.setVisible(true);
		}catch (Exception e1){
			System.err.println("Error");
			e1.printStackTrace();
		}
	}

}




