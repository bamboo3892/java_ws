package com.okina.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.okina.multiblock.BlockBaseFrame;
import com.okina.utils.RenderingHelper;
import com.okina.utils.UtilMethods;

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
import net.minecraftforge.common.property.IExtendedBlockState;

public class BaseFrameBakedModel implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel {

	public static final ResourceLocation[] frame = new ResourceLocation[] { new ResourceLocation("blocks/log_oak"), new ResourceLocation("blocks/iron_block"), new ResourceLocation("blocks/gold_block"), new ResourceLocation("blocks/gold_block"), new ResourceLocation("blocks/diamond_block"), new ResourceLocation("blocks/emerald_block") };

	private TextureAtlasSprite texture;
	private FaceBakery faceBakery = new FaceBakery();
	private final int grade;
	private IBlockState currentState = null;

	public BaseFrameBakedModel(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, int grade) {
		this.grade = grade;
		texture = bakedTextureGetter.apply(frame[grade]);
	}

	@Override
	public IFlexibleBakedModel handleBlockState(IBlockState state) {
		currentState = state;
		return this;
	}

	@Override
	public IFlexibleBakedModel handleItemState(ItemStack stack) {
		currentState = null;
		return this;
	}

	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing face) {
		List<BakedQuad> list = new ArrayList<BakedQuad>();
		if(!(currentState == null)){
			if(currentState instanceof IExtendedBlockState){
				IExtendedBlockState state = (IExtendedBlockState) currentState;
				boolean[] connection = UtilMethods.convertConnectionInfo(state.getValue(BlockBaseFrame.CONNECTION));
				list.addAll(RenderingHelper.renderConnectedFrame(1, connection, face, texture));
				return list;
			}
		}
		list.addAll(RenderingHelper.renderConnectedFrame(1, new boolean[] { false, false, false, false, false, false }, face, texture));
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
		return texture;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

}
