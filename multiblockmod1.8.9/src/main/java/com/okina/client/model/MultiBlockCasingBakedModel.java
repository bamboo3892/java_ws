package com.okina.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.okina.main.TestCore;
import com.okina.multiblock.MultiBlockCasing;
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

public class MultiBlockCasingBakedModel implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel {

	private TextureAtlasSprite pane;
	private TextureAtlasSprite texture;
	private FaceBakery faceBakery = new FaceBakery();
	private IBlockState currentState = null;

	public MultiBlockCasingBakedModel(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		pane = bakedTextureGetter.apply(new ResourceLocation(TestCore.MODID, "blocks/construct_pane"));
		texture = bakedTextureGetter.apply(new ResourceLocation("blocks/coal_block"));
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
		list.add(RenderingHelper.renderCube(1, 1, 1, 15, 15, 15, face, pane));
		if(currentState instanceof IExtendedBlockState){
			IExtendedBlockState state = (IExtendedBlockState) currentState;
			boolean[] connection = UtilMethods.convertConnectionInfo(state.getValue(MultiBlockCasing.CONNECTION));
			list.addAll(RenderingHelper.renderConnectedFrame(1, connection, face, texture));
			return list;
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
