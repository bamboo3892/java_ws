package ai;

import java.awt.Color;

import main.IGuiRenderer;
import main.Sheet;

public abstract class AISheet extends Sheet {

	transient protected int nextX;
	transient protected int nextY;

	transient private IGuiRenderer renderer;

	/**all ai must have  default constructor*/
	public AISheet() {
		super();
	}

	@Deprecated
	public AISheet(int[][] box, int teban) {
		super(box, teban);
	}

	public final AISheet setGuiRenderer(IGuiRenderer renderer) {
		this.renderer = renderer;
		return this;
	}

	public abstract void decideNextPlace();

	protected float getProgress() {
		return renderer.getProgress();
	}
	protected void setProgress(float f) {
		renderer.setProgress(f);
	}
	protected void addProgress(float f) {
		renderer.addProgress(f);
	}
	protected void addLog(String message) {
		renderer.addLog(message, Color.orange);
	}

	public final int getNextX() {
		return nextX;
	}
	public final int getNextY() {
		return nextY;
	}

}
