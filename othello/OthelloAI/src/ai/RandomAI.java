package ai;

import java.util.LinkedList;

import api.AISheet;
import api.OthelloAI;

@OthelloAI(depend = "1.0", author = "bamboo3892", version = "1.0")
public class RandomAI extends AISheet {

	public RandomAI() {

	}

	@Deprecated
	public RandomAI(int[][] box, int teban) {
		super(box, teban);
	}

	@Override
	public void decideNextPlace() {
		LinkedList<Integer> listX = new LinkedList<Integer>();
		LinkedList<Integer> listY = new LinkedList<Integer>();
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				if(canPlace(i, j)){
					listX.add(i);
					listY.add(j);
				}
			}
		int index = (int) (Math.random() * listX.size());
		this.nextX = listX.get(index);
		this.nextY = listY.get(index);
	}

	@Override
	public String getAIName() {
		return "Random AI";
	}

}
