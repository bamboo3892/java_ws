package ai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Random;

public class EstimateAI extends AISheet implements ILearnableAI {

	//protected int originalTeban;
	transient private LinkedList<int[][]> boxHistory = new LinkedList<int[][]>();
	protected final int roop = 6;
	protected int originalTeban;

	public float value00;//-1~1
	public float value10;
	public float value20;
	public float value30;
	public float value11;
	public float value21;
	public float value31;
	public float value22;
	public float value32;
	public float value33;
	public float valueTesuu;//

	public EstimateAI() {
		setGeneFromMeta();
	}

	public EstimateAI(boolean inheritFromFile) {
		if(inheritFromFile){
			setGeneFromMeta();
		}else{
			setNextGene();
		}
	}

	@Deprecated
	public EstimateAI(int[][] box, int teban) {
		super(box, teban);
		originalTeban = teban;
		changeList(box, 0);
	}

	@Override
	public void decideNextPlace() {
		this.originalTeban = teban;
		//box , teban  have been already put
		float max = -10000;
		int maxX = 0;
		int maxY = 0;
		int nnn1 = 0;
		int nnn2 = 0;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				if(canPlaceAtAndReverse(i, j)){
					//System.out.println("nnn2="+nnn2);
					changeList(box, 1);
					float a = nextAssumption(teban == 1 ? 2 : 1, 1, false);
					if(max <= a){
						max = a;
						maxX = i;
						maxY = j;
						nnn1++;
						if(a == 100) return;
					}
					nnn2++;
					this.box = getList(0);
					this.teban = originalTeban;
				}
			}
		System.out.println("nnn2=" + nnn2 + "  nnn1=" + nnn1 + "  max=" + max);
		this.nextX = maxX;
		this.nextY = maxY;
	}

	private float nextAssumption(int p_teban, int p_index, boolean b) {//return assessment point
		if(p_index >= this.roop){
			return estimate(this.box, this.originalTeban);
		}

		float point = 0;
		int change = 0;
		this.teban = p_teban;
		if(p_teban == originalTeban){
			float max = 0;
			for (int i = 0; i < 8; i++)
				for (int j = 0; j < 8; j++){
					if(canPlaceAtAndReverse(i, j)){
						changeList(box, p_index + 1);
						float p = nextAssumption(p_teban == 1 ? 2 : 1, p_index + 1, false);
						change++;
						max = max < p ? p : max;
						if(p == 100) return p;
						this.box = getList(p_index);
						this.teban = p_teban;
					}
				}
			point = max;
		}else{
			float min = 1000;
			for (int i = 0; i < 8; i++)
				for (int j = 0; j < 8; j++){
					if(canPlaceAtAndReverse(i, j)){
						changeList(box, p_index + 1);
						float p = nextAssumption(p_teban == 1 ? 2 : 1, p_index + 1, false);
						change++;
						min = min > p ? p : min;
						this.box = getList(p_index);
						this.teban = p_teban;
					}
				}
			point = min;
		}
		if(change == 0){
			if(b){
				point = judge(box, originalTeban);
			}else{
				changeList(box, p_index + 1);
				float p = nextAssumption(p_teban == 1 ? 2 : 1, p_index + 1, true);
				point = p;
			}
		}
		//System.out.println("assessment point is " + point);
		return point;
	}

	protected float estimate(int[][] pbox, int pteban) {
		float value = 0;
		//int[][] tbox = changeBox(pbox);
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				int n = pbox[i][j];
				if(n == pteban){
					value += getPoint(i, j);
				}else if(n == (pteban == 1 ? 2 : 1)){
					//value -= getPoint(i, j);
				}
			}
		teban = pteban == 1 ? 2 : 1;
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				if(canPlace(i, j)) value -= valueTesuu;
			}
		teban = pteban == 1 ? 2 : 1;
		//System.out.println("assessment value is " + value);
		return value;
	}

	private float judge(int[][] pbox, int pteban) {
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
		int winner = black < white ? 2 : 1;
		return winner == originalTeban ? 1000f : 0;
	}

	/*
	private int[][] changeBox(int[][] pbox) {
		int[][] tbox = new int[8][8];
		for(int i=0;i<8;i++) for(int j=0;j<8;j++) {
			tbox[i][j] = pbox[i][j];
		}
		;
		;
		;
		;
		return tbox;
	}
	*/

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
	public AISheet set(int[][] box, int teban) {
		super.set(box, teban);
		changeList(box, 0);
		return this;
	}

	@Override
	public void setGeneFromMeta() {
		try{
			File file = new File("src/ai/meta/estimate.a");
			FileReader filereader = new FileReader(file);
			BufferedReader br = new BufferedReader(filereader);
			value00 = Float.valueOf(br.readLine());
			value10 = Float.valueOf(br.readLine());
			value20 = Float.valueOf(br.readLine());
			value30 = Float.valueOf(br.readLine());
			value11 = Float.valueOf(br.readLine());
			value21 = Float.valueOf(br.readLine());
			value31 = Float.valueOf(br.readLine());
			value22 = Float.valueOf(br.readLine());
			value32 = Float.valueOf(br.readLine());
			value33 = Float.valueOf(br.readLine());
			valueTesuu = Float.valueOf(br.readLine());
			br.close();
		}catch (NumberFormatException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void setNextGene() {
		int k = 2;
		if(k == 0){
			Random rand = new Random(Calendar.getInstance().getTimeInMillis());
			value00 = rand.nextFloat();
			value10 = rand.nextFloat();
			value20 = rand.nextFloat();
			value30 = rand.nextFloat();
			value11 = rand.nextFloat();
			value21 = rand.nextFloat();
			value31 = rand.nextFloat();
			value22 = rand.nextFloat();
			value32 = rand.nextFloat();
			value33 = rand.nextFloat();
			valueTesuu = rand.nextFloat();
		}else if(k == 1){
			setGeneFromMeta();
			Random rand = new Random();
			float r = 0.01f;
			value00 += rand.nextFloat() * r * 2 - r;
			value10 += rand.nextFloat() * r * 2 - r;
			value20 += rand.nextFloat() * r * 2 - r;
			value30 += rand.nextFloat() * r * 2 - r;
			value11 += rand.nextFloat() * r * 2 - r;
			value21 += rand.nextFloat() * r * 2 - r;
			value31 += rand.nextFloat() * r * 2 - r;
			value22 += rand.nextFloat() * r * 2 - r;
			value32 += rand.nextFloat() * r * 2 - r;
			value33 += rand.nextFloat() * r * 2 - r;
			valueTesuu += rand.nextFloat() * r * 2 - r;
		}else if(k == 2){
			value00 = 8;
			value10 = -2;
			value20 = 1;
			value30 = 1;
			value11 = -4;
			value21 = 1;
			value31 = 1;
			value22 = 1;
			value32 = 1;
			value33 = 1;
			valueTesuu = 0.1f;
			//return;
		}
		float f = value00 + value10 + value20 + value30 + value11 + value21 + value31 + value22 + value32 + value33;
		value00 *= 1f / f;
		value10 *= 1f / f;
		value20 *= 1f / f;
		value30 *= 1f / f;
		value11 *= 1f / f;
		value21 *= 1f / f;
		value31 *= 1f / f;
		value22 *= 1f / f;
		value32 *= 1f / f;
		value33 *= 1f / f;
		//System.out.println(value00 + " " + value10);
	}

	@Override
	public void learn(ILearnableAI pAI) {
		if(!(pAI instanceof EstimateAI)) return;
		EstimateAI ai = (EstimateAI) pAI;
		float a = 0.01f;
		value00 += (ai.value00 - value00) * a;
		value10 += (ai.value10 - value10) * a;
		value20 += (ai.value20 - value20) * a;
		value30 += (ai.value30 - value30) * a;
		value11 += (ai.value11 - value11) * a;
		value21 += (ai.value21 - value21) * a;
		value31 += (ai.value31 - value31) * a;
		value22 += (ai.value22 - value22) * a;
		value32 += (ai.value32 - value32) * a;
		value33 += (ai.value33 - value33) * a;
		valueTesuu += (ai.valueTesuu - valueTesuu) * a;
		try{
			System.out.println("!!!");
			File file = new File("src/ai/meta/estimate.a");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(String.valueOf(value00));
			bw.newLine();
			bw.write(String.valueOf(value10));
			bw.newLine();
			bw.write(String.valueOf(value20));
			bw.newLine();
			bw.write(String.valueOf(value30));
			bw.newLine();
			bw.write(String.valueOf(value11));
			bw.newLine();
			bw.write(String.valueOf(value21));
			bw.newLine();
			bw.write(String.valueOf(value31));
			bw.newLine();
			bw.write(String.valueOf(value22));
			bw.newLine();
			bw.write(String.valueOf(value32));
			bw.newLine();
			bw.write(String.valueOf(value33));
			bw.newLine();
			bw.write(String.valueOf(valueTesuu));
			bw.newLine();
			bw.close();
		}catch (IOException e){
			System.out.println(e);
		}
	}

	private float getPoint(int x, int y) {
		switch (x) {
		case 0:
			switch (y) {
			case 0:
				return value00;
			case 1:
				return value10;
			case 2:
				return value20;
			case 3:
				return value30;
			case 4:
				return value30;
			case 5:
				return value20;
			case 6:
				return value10;
			case 7:
				return value00;
			}
		case 1:
			switch (y) {
			case 0:
				return value10;
			case 1:
				return value11;
			case 2:
				return value21;
			case 3:
				return value31;
			case 4:
				return value31;
			case 5:
				return value21;
			case 6:
				return value11;
			case 7:
				return value10;
			}
		case 2:
			switch (y) {
			case 0:
				return value20;
			case 1:
				return value21;
			case 2:
				return value22;
			case 3:
				return value32;
			case 4:
				return value32;
			case 5:
				return value22;
			case 6:
				return value21;
			case 7:
				return value20;
			}
		case 3:
			switch (y) {
			case 0:
				return value30;
			case 1:
				return value31;
			case 2:
				return value32;
			case 3:
				return value33;
			case 4:
				return value33;
			case 5:
				return value32;
			case 6:
				return value31;
			case 7:
				return value30;
			}
		case 4:
			switch (y) {
			case 0:
				return value30;
			case 1:
				return value31;
			case 2:
				return value32;
			case 3:
				return value33;
			case 4:
				return value33;
			case 5:
				return value32;
			case 6:
				return value31;
			case 7:
				return value30;
			}
		case 5:
			switch (y) {
			case 0:
				return value20;
			case 1:
				return value21;
			case 2:
				return value22;
			case 3:
				return value32;
			case 4:
				return value32;
			case 5:
				return value22;
			case 6:
				return value21;
			case 7:
				return value20;
			}
		case 6:
			switch (y) {
			case 0:
				return value10;
			case 1:
				return value11;
			case 2:
				return value21;
			case 3:
				return value31;
			case 4:
				return value31;
			case 5:
				return value21;
			case 6:
				return value11;
			case 7:
				return value10;
			}
		case 7:
			switch (y) {
			case 0:
				return value00;
			case 1:
				return value10;
			case 2:
				return value20;
			case 3:
				return value30;
			case 4:
				return value30;
			case 5:
				return value20;
			case 6:
				return value10;
			case 7:
				return value00;
			}
		}
		return 0;
	}
}








