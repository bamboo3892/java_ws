package api;

/**
 * This class has othello board information.
 * Provides several useful methods.
 * Override those if you find it inefficient.
 * @author bamboo3892
 */
public class Sheet implements Cloneable {

	/**void:0, black:1, white:2*/
	protected int box[][] = new int[8][8];
	/**means turn*/
	protected int teban;

	public Sheet() {

	}

	public Sheet(int[][] box, int teban) {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				this.box[i][j] = box[i][j];
			}
		this.teban = teban;
	}

	public Sheet set(int[][] box, int teban) {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				this.box[i][j] = box[i][j];
			}
		this.teban = teban;
		return this;
	}

	public Sheet set(Sheet sheet) {
		this.box = sheet.box;
		this.teban = sheet.teban;
		return this;
	}

	/**
	 * return whether the box is filled.
	 * @return result
	 */
	public boolean isFilled() {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				if(box[i][j] != 1 && box[i][j] != 2) return false;
			}
		return true;
	}

	/**return whether the teban can place any stone.*/
	public boolean isTherePlaceToPutStone() {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				if(canPlace(box, i, j, teban, true)) return true;
			}
		return false;
	}

	/**
	 * return whether the teban can place any stone for param teban.
	 * @param teban
	 * @return result
	 */
	public boolean isTherePlaceToPutStone(int teban) {
		for (int i = 0; i < 8; i++)
			for (int j = 0; j < 8; j++){
				if(canPlace(box, i, j, teban, true)) return true;
			}
		return false;
	}

	public boolean canPlaceAtAndReverse(int x, int y) {
		return canPlace(box, x, y, teban, false);
	}

	public boolean canPlace(int x, int y) {
		return canPlace(box, x, y, teban, true);
	}

	public boolean canPlace(int x, int y, boolean simulate) {
		return canPlace(box, x, y, teban, simulate);
	}

	public boolean canPlace(int x, int y, int teban, boolean simulate) {
		return canPlace(box, x, y, teban, simulate);
	}

	/**simulate : actualy reverse the stones*/
	public static boolean canPlace(int[][] pbox, int x, int y, int teban, boolean simulate) {
		if(x < 0 || x > 7 || y < 0 || y > 7 || pbox[x][y] != 0){
			return false;
		}
		boolean b = false;
		for (int henniX = -1; henniX < 2; henniX++){
			flag: for (int henniY = -1; henniY < 2; henniY++){
				if(henniX == 0 && henniY == 0) continue flag;
				int reverseCount = 0;
				for (int i = 1; i < 8; i++){
					int xxx = x + henniX * i;
					int yyy = y + henniY * i;
					if(xxx < 0 || xxx > 7 || yyy < 0 || yyy > 7) continue flag;
					if(pbox[xxx][yyy] == (teban == 1 ? 2 : 1)){
						reverseCount++;
					}else if(pbox[xxx][yyy] == teban && reverseCount != 0){
						if(!simulate){
							for (int j = 0; j < reverseCount + 1; j++){
								pbox[x + henniX * j][y + henniY * j] = teban;
							}
							b = true;
							continue flag;
						}else{
							return true;
						}
					}else{
						continue flag;
					}
				}
			}
		}
		return b;
	}

	/**return winner's teban*/
	public int judge() {
		return judge(this.box);
	}

	/**return winner's teban*/
	public static int judge(int[][] pbox) {
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
		return black == white ? 0 : (black < white ? 2 : 1);
	}

	@Override
	public Sheet clone() {
		return new Sheet().set(box.clone(), teban);
	}

}
