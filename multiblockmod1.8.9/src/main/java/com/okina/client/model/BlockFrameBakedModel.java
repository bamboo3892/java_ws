package com.okina.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.okina.utils.RenderingHelper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

public class BlockFrameBakedModel implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel {

	private TextureAtlasSprite stone;
	private TextureAtlasSprite coalBlock;
	private FaceBakery faceBakery = new FaceBakery();

	public BlockFrameBakedModel(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		stone = bakedTextureGetter.apply(new ResourceLocation("blocks/stone"));
		coalBlock = bakedTextureGetter.apply(new ResourceLocation("blocks/coal_block"));
	}

	@Override
	public IFlexibleBakedModel handleBlockState(IBlockState state) {
		return this;
	}

	@Override
	public IFlexibleBakedModel handleItemState(ItemStack stack) {
		return this;
	}

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing face) {
		List<BakedQuad> list = new ArrayList<BakedQuad>();
		list.add(RenderingHelper.renderCube(2, 2, 2, 14, 14, 14, face, stone));
		list.addAll(RenderingHelper.renderFrame(0, 0, 0, 16, 16, 16, 3, face, coalBlock));
		return list;
	}

	@Override
	public List<BakedQuad> getGeneralQuads() {
		return Collections.emptyList();
	}

	@Override
	public VertexFormat getFormat() {
		return Attributes.DEFAULT_BAKED_FORMAT;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return coalBlock;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

}
