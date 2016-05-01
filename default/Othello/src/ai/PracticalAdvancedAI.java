package ai;

public class PracticalAdvancedAI extends AISheet {

	public PracticalAdvancedAI() {

	}

	@Override
	public void decideNextPlace() {
		int n = 0;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++)
				if(box[i][j] != 0) n++;
		if(n < 55){
			AISheet sheet = (AISheet) (new RandomAI().set(box, n));
			sheet.decideNextPlace();
			this.nextX = sheet.getNextX();
			this.nextY = sheet.getNextY();
		}else{
			AISheet sheet = (AISheet) (new RandomAI().set(box, n));
			sheet.decideNextPlace();
			this.nextX = sheet.getNextX();
			this.nextY = sheet.getNextY();
		}
	}

}
