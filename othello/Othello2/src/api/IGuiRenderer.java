package api;

import java.awt.Color;

/**
 * Users do not have to concern this interface
 * @author bamboo3892
 */
public interface IGuiRenderer {

	float getProgress();

	void setProgress(float f);

	void addProgress(float f);

	void addLog(String str, Color color);

}
