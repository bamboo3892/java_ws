package com.okina.client.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Function;
import com.okina.main.TestCore;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;

public class ConstructBaseModel implements IModel {

	private Block block;
	private int grade;
	private ResourceLocation coreTexture;

	public ConstructBaseModel(Block block, int grade, ResourceLocation coreTexture) {
		this.block = block;
		this.grade = grade;
		this.coreTexture = coreTexture;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		Collection<ResourceLocation> list = new ArrayList<ResourceLocation>();
		list.add(new ResourceLocation(TestCore.MODID, "blocks/construct_pane"));
		if(!coreTexture.getResourceDomain().equals("minecraft")){
			list.add(coreTexture);
		}
		return list;
	}

	@Override
	public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new ConstructBaseBakedModel(bakedTextureGetter, grade, coreTexture);
	}

	@Override
	public IModelState getDefaultState() {
		return ModelRotation.X0_Y0;
	}

}
