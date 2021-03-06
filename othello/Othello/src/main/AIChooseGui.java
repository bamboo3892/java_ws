package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import api.AISheet;

public class AIChooseGui extends JFrame implements MouseListener {

	private static final long serialVersionUID = 7298487213402425577L;

	private IAIChooser chooser;
	private int ID;
	private JScrollPane scrollPane;
	private JList<String> labelList;

	public AIChooseGui(IAIChooser chooser, int ID) {
		this.chooser = chooser;
		this.ID = ID;

		setResizable(false);
		setBounds(120, 120, 300, 400);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		scrollPane = new JScrollPane();
		scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(scrollPane);

		DefaultListModel<String> model = new DefaultListModel<String>();

		for (Entry<String, Class<? extends AISheet>> entry : Start.AIMap.entrySet()){
			model.addElement(entry.getKey());
		}
		labelList = new JList<String>(model);
		labelList.addMouseListener(this);
		scrollPane.setViewportView(labelList);

		setVisible(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getSource() instanceof JList && e.getClickCount() == 2){
			System.out.println(((JList<String>) e.getSource()).getSelectedValue());
			chooser.setAIName(ID, ((JList<String>) e.getSource()).getSelectedValue());
			dispose();
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


	public interface IAIChooser {
		void setAIName(int ID, String AIName);
	}

}
