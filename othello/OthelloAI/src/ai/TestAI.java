package ai;

import java.io.Serializable;

import api.AISheet;
import api.ILearnableAI;
import api.OthelloAI;

@OthelloAI(depend = "1.0", author = "bamboo3892", version = "1.0")
public class TestAI extends AISheet implements ILearnableAI {

	@Override
	public String getAIName() {
		return "Test AI";
	}

	@Override
	public void decideNextPlace() {
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				if(canPlace(i, j)){
					this.nextX = i;
					this.nextY = j;
				}
			}
		}
	}

	@Override
	public void setMetaFromSavedObject(Object o) {}

	@Override
	public boolean canLearnFrom(AISheet ai) {
		return true;
	}

	@Override
	public String learnFrom(AISheet otherAI) {
		return "Learn";
	}

	@Override
	public Serializable getSaveObjectClone() {
		return 1000;
	}

}
