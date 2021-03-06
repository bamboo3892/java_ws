package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import api.AISheet;
import api.IGuiRenderer;

public class AIButtleGui extends JFrame implements ActionListener, IGuiRenderer {

	private static final long serialVersionUID = -584037256605831173L;

	public static final int TEBAN_RANDOM = 0;
	public static final int TEBAN_SENTE_FIXED = 1;
	public static final int TEBAN_GOTE_FIXED = 2;

	private Thread learningThread;
	private AIButtleSheet sheet;
	private JPanel contentPane;
	private JScrollPane textPane;
	private JTextArea textArea;
	private JProgressBar bar;
	private JButton btn;

	/**
	 *
	 * @param learnable
	 * @param otherAI
	 * @param repeat
	 * @param saveRate
	 * @param tebanCommand
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	public AIButtleGui(AISheet learnable, AISheet otherAI, int buttleMode,int repeat, int saveRate, int tebanCommand) throws FileNotFoundException, IOException, ClassNotFoundException {
		setTitle(learnable.getAIName() + " v.s. " + otherAI.getAIName());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 326, 379);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		textPane = new JScrollPane();
		contentPane.add(textPane, BorderLayout.CENTER);
		textArea = new JTextArea();
		textPane.setViewportView(textArea);

		bar = new JProgressBar();
		bar.setMinimum(0);
		bar.setMaximum(100);
		bar.setValue(0);
		bar.setStringPainted(true);
		bar.setString("0%");
		contentPane.add(bar, BorderLayout.NORTH);

		btn = new JButton("start");
		btn.addActionListener(this);
		contentPane.add(btn, BorderLayout.SOUTH);

		sheet = new AIButtleSheet(this, learnable, otherAI, repeat, saveRate, tebanCommand);
	}

	public void addLearningLog(String message) {
		SwingUtilities.invokeLater(() -> {
			textArea.append(message + "\n");
		});
	}
	public void setLeaningProgress(float progress) {
		SwingUtilities.invokeLater(() -> {
			bar.setValue((int) (100 * progress));
			bar.setString(100 * progress + "%");
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btn && learningThread == null){
			learningThread = new Thread(sheet, "Learning Thread");
			learningThread.start();
			btn.setEnabled(false);
			SwingUtilities.invokeLater(() -> {
				btn.setEnabled(false);
			});
		}
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
