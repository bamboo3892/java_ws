package main;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ai.AISheet;
import ai.AdvancedAI;
import ai.AncientAi;
import ai.EstimateAI;
import ai.EstimateAIMk2;
import ai.MonteCarloAI;
import ai.OldAI;
import ai.RandomAI;

public class Start {

	public static final int panelSize = 604;
	protected static Map<String, Class<? extends AISheet>> AIMap = new HashMap<String, Class<? extends AISheet>>();;

	public static void main(String[] args) throws IOException {

		AIMap.put("Random AI", RandomAI.class);
		AIMap.put("Estimate AI", EstimateAI.class);
		AIMap.put("MonteCarlo AI", MonteCarloAI.class);
		AIMap.put("Guilty Parfect AI", AdvancedAI.class);
		AIMap.put("Ancient AI", AncientAi.class);
		AIMap.put("EstimateMk2 AI", EstimateAIMk2.class);
		AIMap.put("Old AI", OldAI.class);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try{
					InitGui frame = new InitGui();
					frame.setVisible(true);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
	}

}