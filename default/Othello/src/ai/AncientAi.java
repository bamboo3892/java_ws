package ai;

import api.AISheet;

public class AncientAi extends AISheet {

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
	public String getAIName() {
		return "Ancient AI";
	}

}
