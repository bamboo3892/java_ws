package ai;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MonteCarloAI extends AISheet {

	protected int roop = 10000;
	protected int originalTeban;
	private Random rand = new Random();

	public MonteCarloAI() {
		super();
	}

	@Override
	public void decideNextPlace() {
		int originalBox[][] = new int[8][8];
		originalTeban = teban;
		int n = 0;
		List<Integer> x = new LinkedList<Integer>();
		List<Integer> y = new LinkedList<Integer>();
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				originalBox[i][j] = this.box[i][j];
				if(canPlace(i, j)){
					n++;
					x.add(i);
					y.add(j);
				}
			}
		}
		float duel[] = new float[n];
		float win[] = new float[n];
		for (int k = 0; k < this.roop; k++){
			for (int i = 0; i < 8; i++){
				for (int j = 0; j < 8; j++){
					this.box[i][j] = originalBox[i][j];
				}
			}
			setProgress(k / (float) roop);
			int num = k % n;
			if(!canPlaceAtAndReverse(x.get(num), y.get(num))) ;
			int winner = nextAssumption(teban == 1 ? 2 : 1, false);
			duel[num]++;
			if(winner == originalTeban){
				win[num]++;
			}
		}

		int max = 0;
		float maxRate = 0;
		float rate;
		for (int i = 0; i < n; i++){
			rate = win[i] / duel[i];
			if(maxRate < rate){
				max = i;
				maxRate = rate;
			}
		}

		System.out.println("n = " + n + "  maxRate = " + maxRate);
		this.nextX = x.get(max);
		this.nextY = y.get(max);
	}

	private int nextAssumption(int pteban, boolean b) {//return winner
		this.teban = pteban;
		int n = 0;
		List<Integer> x = new LinkedList<Integer>();
		List<Integer> y = new LinkedList<Integer>();
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				if(canPlace(i, j)){
					n++;
					x.add(i);
					y.add(j);
				}
			}
		if(n == 0){
			if(b){
				return judge(box);
			}else{
				return nextAssumption(teban == 1 ? 2 : 1, true);
			}
		}
		int num = (int) (rand.nextFloat() * n);
		if(!canPlaceAtAndReverse(x.get(num), y.get(num))) System.out.println("error?");
		return nextAssumption(teban == 1 ? 2 : 1, false);
	}

}
