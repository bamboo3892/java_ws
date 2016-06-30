package com.okina.client.model;

import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Function;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;

public class BaseFrameModel implements IModel {

	private int grade;

	public BaseFrameModel(int grade) {
		this.grade = grade;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return Collections.emptyList();
	}

	@Override
	public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new BaseFrameBakedModel(bakedTextureGetter, grade);
	}

	@Override
	public IModelState getDefaultState() {
		return ModelRotation.X0_Y0;
	}

}
