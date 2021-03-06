package api;

import java.awt.Color;

/**
 * All AI class must have  default constructor.
 * @author bamboo3892
 */
public abstract class AISheet extends Sheet {

	protected int nextX;
	protected int nextY;

	private IGuiRenderer renderer;

	/**All AI class must have  default constructor.*/
	public AISheet() {
		super();
	}

	/**
	 * Do not use this constructor.
	 * The processor does not call this.
	 * @param box
	 * @param teban
	 */
	@Deprecated
	public AISheet(int[][] box, int teban) {
		super(box, teban);
	}

	/**
	 * Get display AI name
	 * @return AI name
	 */
	public abstract String getAIName();

	public final AISheet setGuiRenderer(IGuiRenderer renderer) {
		this.renderer = renderer;
		return this;
	}

	/**
	 * Called after the processor set the box.
	 * Result should be set in nextX/nextY.
	 */
	public abstract void decideNextPlace();

	/**
	 * Get displayer progress
	 * @return return -1 if renderer == null
	 */
	protected float getProgress() {
		return renderer == null ? -1 : renderer.getProgress();
	}

	/**
	 *Set display progress.
	 * @param progress
	 */
	protected void setProgress(float f) {
		if(renderer != null){
			renderer.setProgress(f);
		}
	}

	/**
	 * Add display progress.
	 * @param progress
	 */
	protected void addProgress(float f) {
		if(renderer != null){
			renderer.addProgress(f);
		}
	}

	/**
	 *	Add display log.
	 * @param message
	 */
	protected void addLog(String message) {
		if(renderer != null){
			renderer.addLog(message, Color.orange);
		}
	}

	/**
	 * Called by the processor after decideNextPlace()
	 * @return nextX
	 */
	public final int getNextX() {
		return nextX;
	}

	/**
	 * Called by the processor after decideNextPlace()
	 * @return nextY
	 */
	public final int getNextY() {
		return nextY;
	}

}
