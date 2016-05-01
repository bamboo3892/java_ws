package main;

import java.awt.Color;

public interface IGuiRenderer {

	float getProgress();

	void setProgress(float f);

	void addProgress(float f);

	void addLog(String str, Color color);

}
