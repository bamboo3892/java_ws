package test;

import java.awt.Graphics;

public class Blocks {

	public static final int NumberOfTypes = 7;
	public static final int[][][][] Shape = { { { {-1,0}, {0,0}, {1,0}, {0,1} },
											       { {0,-1}, {-1,0}, {0,0}, {0,1} },
											       { {0,-1}, {-1,0}, {0,0}, {1,0} },
											       { {0,-1}, {0,0}, {1,0}, {0,1} }
											    },
											    { { {-1,0}, {0,0}, {0,1}, {1,1} },
											       { {0,-1}, {-1,0}, {0,0}, {-1,1} },
											       {  {-1,0}, {0,0}, {0,1}, {1,1} },
											       { {0,-1}, {-1,0}, {0,0}, {-1,1} }
											    },
											    { { {0,0}, {1,0}, {-1,1}, {0,1} },
											       { {0,-1}, {0,0}, {1,0}, {1,1} },
											       {  {0,0}, {1,0}, {-1,1}, {0,1} },
											       { {0,-1}, {0,0}, {1,0}, {1,1} }
											    },
											    { { {-1,0}, {0,0}, {1,0}, {1,1} },
											       { {1,0}, {1,1}, {0,2}, {1,2} },
											       { {-1,1}, {-1,2}, {0,2}, {1,2} },
											       { {-1,0}, {0,0}, {-1,1}, {-1,2} }
											    },
											    { { {-1,0}, {0,0}, {1,0}, {-1,1} },
											       { {0,0}, {1,0}, {1,1}, {1,2} },
											       { {1,1}, {-1,2}, {0,2}, {1,2} },
											       { {-1,0}, {-1,1}, {-1,2}, {0,2} }
											    },
											    { { {0,0}, {1,0}, {0,1}, {1,1} },
											       { {0,0}, {1,0}, {0,1}, {1,1} },
											       { {0,0}, {1,0}, {0,1}, {1,1} },
											       { {0,0}, {1,0}, {0,1}, {1,1} },
											    },
											    { { {-1,0}, {0,0}, {1,0}, {2,0} },
											       { {2,-1}, {2,0}, {2,1}, {2,2} },
											       { {-1,1}, {0,1}, {1,1}, {2,1} },
											       { {-1,-1}, {-1,0}, {-1,1}, {-1,2} }
											    }
											};
	private GamePanel gp;

	private int type;//0-6
	private int state;//0-3
	private int x = 4;
	private int y = 0;
	private boolean beforePlace = false;
	private boolean needNextPrepare = false;

	public Blocks(GamePanel gp){
		this.gp = gp;
	}

	public boolean fall(){
		boolean b = true;
		for(int i=0;i<4;i++){
			int x = this.x + Shape[type][state][i][0];
			int y = this.y + Shape[type][state][i][1] + 1;
			if(x>=0 && x<=9 && y<0){
				continue;
			}else if(x>=0 && x<=9 && y>=0 && y<=19){
				if(gp.getBoxes()[x][y].getColor() != 7) b = false;
			}else{
				b = false;
			}
		}
		if(b){
			y++;
		}else{
			if(beforePlace){
				place();
				beforePlace = false;
			}else{
				beforePlace = true;
			}
		}
		return b;
	}

	public void fallAll(){
		while(fall());
		while(fall());
	}

	public void moveRight(){
		boolean b = true;
		for(int i=0;i<4;i++){
			int x = this.x + Shape[type][state][i][0] + 1;
			int y = this.y + Shape[type][state][i][1];
			if(x>=0 && x<=9 && y<0){
				continue;
			}else if(x>=0 && x<=9 && y>=0 && y<=19){
				if(gp.getBoxes()[x][y].getColor() != 7) b = false;
			}else{
				b = false;
			}
		}
		if(b){
			x++;
		}
	}

	public void moveLeft(){
		boolean b = true;
		for(int i=0;i<4;i++){
			int x = this.x + Shape[type][state][i][0] - 1;
			int y = this.y + Shape[type][state][i][1];
			if(x>=0 && x<=9 && y<0){
				continue;
			}else if(x>=0 && x<=9 && y>=0 && y<=19){
				if(gp.getBoxes()[x][y].getColor() != 7) b = false;
			}else{
				b = false;
			}
		}
		if(b){
			x--;
		}
	}

	public void turnRight(){
		boolean b = true;
		int nstate  = state==3 ? 0 : state+1;
		for(int i=0;i<4;i++){
			int x = this.x + Shape[type][nstate][i][0];
			int y = this.y + Shape[type][nstate][i][1];
			if(x>=0 && x<=9 && y<0){
				continue;
			}else if(x>=0 && x<=9 && y>=0 && y<=19){
				if(gp.getBoxes()[x][y].getColor() != 7) b = false;
			}else{
				b = false;
			}
		}
		if(b) state = nstate;
	}

	public void turnLeft(){
		boolean b = true;
		int nstate = state==0 ? 3 : state-1;
		for(int i=0;i<4;i++){
			int x = this.x + Shape[type][nstate][i][0];
			int y = this.y + Shape[type][nstate][i][1];
			if(x>=0 && x<=9 && y<0){
				continue;
			}else if(x>=0 && x<=9 && y>=0 && y<=19){
				if(gp.getBoxes()[x][y].getColor() != 7) b = false;
			}else{
				b = false;
			}
		}
		if(b) state = nstate;
	}

	public String nextDeal(){
		if(needNextPrepare){
			nextPrepare();
			return "prepare";
		}
		if(fall()){
			return "fall";
		}else{
			return "place";
		}
	}

	private void place(){
		for(int i=0;i<4;i++){
			int x = this.x + Shape[type][state][i][0];
			int y = this.y + Shape[type][state][i][1];
			gp.getBoxes()[x][y].setColor(type);
		}
		gp.setAfterPlaceCount();
		gp.checkAndDeleteLine();
		needNextPrepare = true;
	}

	public void nextPrepare(){
		this.type = gp.getNextBlocks();
		this.x = 4;
		this.y = 0;
		this.state = 0;
		gp.setNextBlocks( (int)(Math.random()*NumberOfTypes) );
		needNextPrepare = false;
	}

	public void paint(Graphics g){
		for(int i=0;i<4;i++){
			int x = this.x + Shape[type][state][i][0];
			int y = this.y + Shape[type][state][i][1];
			if(y<0) continue;
			g.drawImage(Box.BlockImage[type], 24+36*x, 24+36*y, null);
		}
	}

}
