package com.test.nei;

import codechicken.nei.api.API;

public class LoadNEIConfig {

	/*別クラスで作るレシピ表示用インターフェースの取得*/
	public static SampleRecipeHandler catchRecipe;

	public static void load() {
		catchRecipe = new SampleRecipeHandler();

		API.registerRecipeHandler(catchRecipe);
		API.registerUsageHandler(catchRecipe);
		API.registerGuiOverlay(GuiDummy.class, catchRecipe.getOverlayIdentifier(), 0, 0);
	}

}
