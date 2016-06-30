package com.okina.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.okina.multiblock.BlockPipe;
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

public class BlockPipeBakedModel implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel {

	public static final ResourceLocation[] frame = new ResourceLocation[] { new ResourceLocation("blocks/iron_block"), new ResourceLocation("blocks/gold_block"), new ResourceLocation("blocks/diamond_block"), new ResourceLocation("blocks/emerald_block") };

	private TextureAtlasSprite texture[] = new TextureAtlasSprite[4];
	private TextureAtlasSprite redstone;
	private TextureAtlasSprite coalBlock;
	private FaceBakery faceBakery = new FaceBakery();
	private IBlockState currentState = null;

	public BlockPipeBakedModel(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		for (int i = 0; i < 4; i++){
			texture[i] = bakedTextureGetter.apply(frame[i]);
		}
		redstone = bakedTextureGetter.apply(new ResourceLocation("blocks/redstone_block"));
		coalBlock = bakedTextureGetter.apply(new ResourceLocation("blocks/coal_block"));
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
		list.add(RenderingHelper.renderCube(6, 6, 6, 10, 10, 10, face, redstone));
		int color = 0;
		if(currentState != null){
			if(currentState instanceof IExtendedBlockState){
				IExtendedBlockState state = (IExtendedBlockState) currentState;
				boolean[] connection = UtilMethods.convertConnectionInfo(state.getValue(BlockPipe.CONNECTION));
				if(connection[0]){
					list.add(RenderingHelper.renderCube(6F, 0F, 6F, 10F, 6F, 10F, face, coalBlock));
				}
				if(connection[1]){
					list.add(RenderingHelper.renderCube(6F, 10F, 6F, 10F, 16F, 10F, face, coalBlock));
				}
				if(connection[2]){
					list.add(RenderingHelper.renderCube(6F, 6F, 0F, 10F, 10F, 6F, face, coalBlock));
				}
				if(connection[3]){
					list.add(RenderingHelper.renderCube(6F, 6F, 10F, 10F, 10F, 16F, face, coalBlock));
				}
				if(connection[4]){
					list.add(RenderingHelper.renderCube(0F, 6F, 6F, 6F, 10F, 10F, face, coalBlock));
				}
				if(connection[5]){
					list.add(RenderingHelper.renderCube(10F, 6F, 6F, 16F, 10F, 10F, face, coalBlock));
				}
			}
			color = currentState.getValue(BlockPipe.COLOR);
		}
		list.addAll(RenderingHelper.renderFrame(5, 5, 5, 11, 11, 11, 1, face, texture[color]));
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
		return redstone;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

}
