package com.example.examplemod;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class SampleModelLoader implements ICustomModelLoader {
	/**
	どんな物にこのModelLoaderを適応するか返す。
	今回は、判定が面倒いのでこのMod内で作られたModelなら全て適応する。
	*/
	public boolean accepts(ResourceLocation modelLocation) {
		return modelLocation.getResourceDomain().equals(ExampleMod.MODID.toLowerCase());
	}

	/**
	描画するIModelを返す。 IResourceManagerがあれば、ここで外部ファイルを読み込んでIModelに渡せる。
	今回は、パターンが一つだけなのでそのまま返している。
	
	@see #onResourceManagerReload
	*/
	public IModel loadModel(ResourceLocation modelLocation) {
		return new SampleModel();
	}

	/**
	外部リソースを利用する場合はresourceManagerをここで取得しておく必要がある。
	今回は、内部で完結するので必要ない。
	*/
	public void onResourceManagerReload(IResourceManager resourceManager) {}
}
