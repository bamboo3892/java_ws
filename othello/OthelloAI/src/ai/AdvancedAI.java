package ai;

import java.util.LinkedList;

import api.AISheet;
import api.OthelloAI;
import main.SLock;

@OthelloAI(depend = "1.0", author = "bamboo3892", version = "1.0")
public class AdvancedAI extends AISheet implements Runnable {

	private LinkedList<int[][]> boxHistory = new LinkedList<int[][]>();
	private int originalTeban;
	private float point;//thread only
	private SLock slock;

	public AdvancedAI() {

	}

	@Deprecated
	public AdvancedAI(int[][] box, int teban) {
		super(box, teban);
		addList(box);
		originalTeban = teban;
	}

	public AdvancedAI(int[][] box, int teban, SLock p_slock) {
		super(box, teban);
		addList(box);
		originalTeban = teban;
		this.slock = p_slock;
	}

	@Override
	public AISheet set(int[][] box, int teban) {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				this.box[i][j] = box[i][j];
			}
		this.teban = teban;
		addList(box);
		originalTeban = teban;
		return this;
	}

	@Override
	public void decideNextPlace() {
		if(box[3][4] == 1 && box[4][3] == 1 && box[3][3] == 2 && box[4][4] == 2 && box[2][3] == 0){
			this.nextX = 2;
			this.nextY = 3;
			return;
		}
		int nnn1 = 0;
		LinkedList<AdvancedAI> threadList = new LinkedList<AdvancedAI>();
		LinkedList<Integer> listX = new LinkedList<Integer>();
		LinkedList<Integer> listY = new LinkedList<Integer>();
		SLock slock = new SLock();

		float max = 0f;
		int maxX = 0;
		int maxY = 0;
		this.box = getList(0);
		this.teban = originalTeban;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				this.box = getList(0);
				this.teban = originalTeban;
				if(canPlace(i, j)){
					canPlaceAtAndReverse(i, j);
					threadList.add(new AdvancedAI(box, teban == 1 ? 2 : 1, slock));
					listX.add(i);
					listY.add(j);
					nnn1++;
					this.box = getList(0);
					this.teban = originalTeban;
				}
			}
		slock.setAll(nnn1 + 1);
		for (int i = 0; i < threadList.size(); i++){
			new Thread(threadList.get(i)).start();
		}
		slock.lock();
		for (int i = 0; i < listX.size(); i++){
			float point = threadList.get(i).getPoint();
			if(max <= point){
				max = point;
				maxX = listX.get(i);
				maxY = listY.get(i);
			}
		}
		this.nextX = maxX;
		this.nextY = maxY;
		if(max == 1f) System.out.println("Settled Victory");
	}

	/*
	@Override
	public void decideNextPlace() {
		if(box[3][4]==1 && box[4][3]==1 && box[3][3]==2 && box[4][4]==2 && box[2][3]==0){
			this.nextX = 2;
			this.nextY = 3;
			return;
		}
		int nnn = 0;

		float max = 0f;
		int maxX = 0;
		int maxY = 0;
		this.box = getList(0);
		this.teban = originalTeban;

		for(int i=0;i<8;i++) for(int j=0;j<8;j++) {
			this.box = getList(0);
			this.teban = originalTeban;
			if(canPlace(i, j)){
				System.out.println("nnn="+nnn);
				canPlaceAtAndReverse(i, j);
				changeList(box, 1);
				float a = nextAssumption(teban==1 ? 2 : 1, 1);
				if(max<=a){
					max = a;
					maxX = i;
					maxY = j;
				}
				nnn++;
				this.box = getList(0);
				this.teban = originalTeban;
			}
		}
		this.nextX = maxX;
		this.nextY = maxY;
		if(max == 1f) System.out.println("Settled Victory");
		System.out.println(nnn);
	}
	*/

	@Override
	public void run() {
		float sum = 0f;
		float n = 0;
		this.box = getList(0);
		this.teban = originalTeban;

		int nnn2 = 0;

		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				this.box = getList(0);
				this.teban = originalTeban;
				if(canPlace(i, j)){
					System.out.println("nnn2=" + nnn2);
					canPlaceAtAndReverse(i, j);
					changeList(box, 1);
					float a = nextAssumption(teban == 1 ? 2 : 1, 1, false);
					sum += a;
					nnn2++;
					this.box = getList(0);
					this.teban = originalTeban;
				}
			}
		this.point = sum / n;

		slock.lock();
	}

	public float getPoint() {//thread only
		return point;
	}

	/*
		private float nextAssumption(int p_teban, int p_index){//return assessment point

			if(isFilled()){
				return judge(box, originalTeban) ? 1f : 0f;
			}
			if(!isTherePlaceToPutStone()){
				this.teban = p_teban==1 ? 2 : 1;
				if(!isTherePlaceToPutStone()){
					return judge(box, originalTeban) ? 1f : 0f;
				}
				this.teban = p_teban;

				this.box = getList(p_index);
				this.teban = p_teban;
				changeList(box, p_index+1);
				//assessment
				float point = nextAssumption(p_teban==1 ? 2 : 1, p_index+1);
				return point;
			}

			if(p_teban == originalTeban){
				float max = 0f;
				this.box = getList(p_index);
				this.teban = p_teban;
				for(int i=0;i<8;i++) for(int j=0;j<8;j++) {
					if(canPlace(i, j)){
						if(!canPlaceAtAndReverse(i, j)) continue;
						changeList(box, p_index+1);
						//assessment
						float point = nextAssumption(p_teban==1 ? 2 : 1, p_index+1);
						if(point==1){
							return 1f;
						}else{
							max = max<point ? point : max;
						}
						this.box = getList(p_index);
						this.teban = p_teban;
					}
				}
				return max;
			}else{
				float sum = 0f;
				float n = 0;
				this.box = getList(p_index);
				this.teban = p_teban;
				for(int i=0;i<8;i++) for(int j=0;j<8;j++){
					if(canPlace(i, j)){
						if(!canPlaceAtAndReverse(i, j)) continue;
						changeList(box, p_index+1);
						//assessment
						float point = nextAssumption(p_teban==1 ? 2 : 1, p_index+1);
						sum += point;
						n++;
						this.box = getList(p_index);
						this.teban = p_teban;
					}
				}
				return sum/n;
			}
		}
	*/

	private float nextAssumption(int p_teban, int p_index, boolean b) {//return assessment point

		float point = 0;
		int change = 0;
		//this.box = getList(p_index);
		this.teban = p_teban;
		if(p_teban == originalTeban){
			float max = 0f;
			for (int i = 0; i < 8; i++)
				for (int j = 0; j < 8; j++){
					if(canPlaceAtAndReverse(i, j)){
						changeList(box, p_index + 1);
						float p = nextAssumption(p_teban == 1 ? 2 : 1, p_index + 1, false);
						change++;
						if(p == 1){
							return 1f;
						}else{
							max = max < p ? p : max;
						}
						this.box = getList(p_index);
						this.teban = p_teban;
					}
				}
			point = max;
		}else{
			float sum = 0f;
			float n = 0;
			for (int i = 0; i < 8; i++)
				for (int j = 0; j < 8; j++){
					if(canPlaceAtAndReverse(i, j)){
						changeList(box, p_index + 1);
						float p = nextAssumption(p_teban == 1 ? 2 : 1, p_index + 1, false);
						change++;
						sum += p;
						n++;
						this.box = getList(p_index);
						this.teban = p_teban;
					}
				}
			point = sum / n;
		}
		if(change == 0){
			if(b){
				point = judge(box, originalTeban) ? 1f : 0f;
			}else{
				changeList(box, p_index + 1);
				float p = nextAssumption(p_teban == 1 ? 2 : 1, p_index + 1, true);
				point = p;
			}
		}
		return point;
	}

	private boolean judge(int[][] pbox, int pteban) {
		int black = 0;
		int white = 0;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				if(pbox[i][j] == 1){
					black++;
				}else if(pbox[i][j] == 2){
					white++;
				}
			}
		return pteban == (black < white ? 2 : 1);
	}

	private void addList(int[][] pbox) {
		int[][] tbox = new int[8][8];
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				tbox[i][j] = pbox[i][j];
			}
		boxHistory.add(tbox);
	}

	private void changeList(int[][] pbox, int pindex) {
		int[][] tbox = new int[8][8];
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				tbox[i][j] = pbox[i][j];
			}
		if(boxHistory.size() > pindex){
			boxHistory.set(pindex, tbox);
		}else{
			boxHistory.add(tbox);
			//System.out.println(pindex);
		}
	}

	private int[][] getList(int index) {
		int[][] tbox = new int[8][8];
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				tbox[i][j] = boxHistory.get(index)[i][j];
			}
		return tbox;
	}

	@Override
	public String getAIName() {
		return "Guilty Perfect AI";
	}

}
