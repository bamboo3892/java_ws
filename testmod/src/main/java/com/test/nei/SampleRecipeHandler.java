package com.test.nei;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map.Entry;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.item.ItemStack;

/*NEIにて、レシピ表示ボタンを押した際の処理の追加部分。
  NEI対応のメイン部分です。
  TemplateRecipeHamdlerを継承します。*/
public class SampleRecipeHandler extends TemplateRecipeHandler {

	/*表示用のレシピをHashMap型で用意します。*/
	private HashMap<ItemStack, ItemStack> newRecipe;

	/*表示用のHashMapの取得。
	  ここでは、NewRecipeRegister.classで用意しておいた架空のレシピを取得します。
	  （独自レシピの作成については割愛します。）*/
	private HashMap<ItemStack, ItemStack> getRecipes() {
		if(NewRecipeRegister.newRecipeList != null && !NewRecipeRegister.newRecipeList.isEmpty()){
			this.newRecipe = NewRecipeRegister.newRecipeList;
		}
		return this.newRecipe;
	}

	public class SampleRecipe extends CachedRecipe {

		/*インプット、アウトプットそれぞれの表示用ItemStackです。
		  表示させる位置などの情報を持たせることが出来ます。*/
		private PositionedStack input;
		private PositionedStack result;

		/*表示用のItemStackの、画面上の座標を登録しています。
		  この座標がずれると、レシピ表示画面に表示されるアイコンがズレるので気をつけましょう。*/
		public SampleRecipe(ItemStack in, ItemStack out) {
			in.stackSize = 1;
			this.input = new PositionedStack(in, 48, 21);
			this.result = new PositionedStack(out, 102, 21);
		}

		@Override
		public PositionedStack getResult() {
			return this.result;
		}

		@Override
		public PositionedStack getIngredient() {
			return this.input;
		}

	}

	//	/**ここではダミー用のGUIを登録していますが、
	//	  独自クラフトシステム用に用意したGUIがあれば流用できます。
	//	  <br>ここで登録をしたguiからレシピ画面に飛ぶことが出来るようになります。
	//	  <br>もちろん、（魔術系MODのように）可愛らしいイラストなどを用意して表示させることも出来ます。*/
	//	@Override
	//	public Class<? extends GuiContainer> getGuiClass() {
	//		return GuiDummy.class;
	//	}

	/*登録用の文字列です*/
	@Override
	public String getOverlayIdentifier() {
		return "SampleRecipe";
	}

	//ここで登録をした範囲内をGUIでクリックすると文字列のレシピ画面を表示します.
	@Override
	public void loadTransferRects() {
		transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(65, 25, 20, 20), "SampleRecipe"));
	}

	/*以下のメソッドは、NEIのGUI画面上でアイコンをクリックしたり、
	  プログレスバーの部分をクリックした時に呼び出されるメソッド*/
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		System.out.println("loadCraftingRecipese");
		for (Object o : results){
			System.out.println(o);
		}
		if(outputId.equals("SampleRecipe")){
			HashMap<ItemStack, ItemStack> recipes = (HashMap<ItemStack, ItemStack>) this.getRecipes();

			if(recipes == null || recipes.isEmpty()) return;
			for (Entry<ItemStack, ItemStack> recipe : recipes.entrySet()){
				ItemStack item = recipe.getValue();
				ItemStack in = recipe.getKey();
				arecipes.add(new SampleRecipe(in, item));
			}
		}else{
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		System.out.println("loadCraftingRecipe " + result);
		HashMap<ItemStack, ItemStack> recipes = (HashMap<ItemStack, ItemStack>) this.getRecipes();

		if(recipes == null || recipes.isEmpty()) return;
		for (Entry<ItemStack, ItemStack> recipe : recipes.entrySet()){
			ItemStack item = recipe.getValue();
			ItemStack in = recipe.getKey();
			if(NEIServerUtils.areStacksSameType(item, result)){
				arecipes.add(new SampleRecipe(in, item));
			}
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		System.out.println("loadUsageRecipes " + ingredient);
		HashMap<ItemStack, ItemStack> recipes = (HashMap<ItemStack, ItemStack>) this.getRecipes();

		if(recipes == null || recipes.isEmpty()) return;
		for (Entry<ItemStack, ItemStack> recipe : recipes.entrySet()){
			ItemStack item = recipe.getValue();
			ItemStack in = recipe.getKey();
			if(ingredient.getItem() == in.getItem() && ingredient.getItemDamage() == in.getItemDamage()){
				arecipes.add(new SampleRecipe(ingredient, item));
			}
		}
	}

	/*レシピ画面に表示される名前です。*/
	@Override
	public String getRecipeName() {
		return "This is Dummy Recipe for Tutorial.";
	}

	/*レシピ画面に使われる背景画像の場所です。*/
	@Override
	public String getGuiTexture() {
		return "tutorial:textures/gui/dummygui.png";
	}


}
