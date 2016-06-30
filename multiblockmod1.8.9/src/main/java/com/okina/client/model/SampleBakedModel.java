package com.okina.client.model;

import java.util.Collections;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockPartFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;

public class SampleBakedModel implements IFlexibleBakedModel, ISmartBlockModel, ISmartItemModel {
	//Textureの保持。
	private TextureAtlasSprite stone;
	//BakedQuadを作るためのクラスを保持。
	private FaceBakery faceBakery = new FaceBakery();
	private IBlockState currentState = null;

	public SampleBakedModel(Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		//ResourceLocationをTextureに変換する。
		stone = bakedTextureGetter.apply(new ResourceLocation("blocks/cobblestone"));
	}

	/**
	 IBlockStateにあわせてBlockのModelの形状を変えるときに用いる。
	 IBlockStateはBlock#getActualStateでいろいろ詰め込めるのでこのメソッドの使い方は大変重要。
	 今回は、Stateの変更がないのでそのまま返す。
	
	 @see net.minecraft.block.Block#getActualState
	 @see ISmartBlockModel
	 */
	@Override
	public IFlexibleBakedModel handleBlockState(IBlockState state) {
		currentState = state;
		return this;
	}

	/**
	 ItemStackの状況でItemのModelの形状を変えるときに用いる。
	 NBTは知っての通りいろいろ詰め込めるのでこのメソッドの使い方は大変重要。
	 今回は、NBTやMetadataに変更がないのでそのまま返す。
	
	 @see ItemStack#getTagCompound
	 @see ISmartItemModel
	 */
	@Override
	public IFlexibleBakedModel handleItemState(ItemStack stack) {
		return this;
	}

	/**
	 面が不透明なBlockに接していないときのみ描画する面を指定する。
	 必要な時しか描画しないのでgetGeneralQuadsで指定するより大幅に軽量化できる。
	 が、万能ではないので注意。
	 もちろん、一つの面につき一つのQuadのような制約はない。
	 今回は、目一杯1Block分の範囲を使っているのでここで全て指定している。
	
	 @see #getGeneralQuads
	 */
	@Override
	public List<BakedQuad> getFaceQuads(EnumFacing face) {
		//面の始点
		Vector3f from = new Vector3f(0, 0, 0);

		//面の終点
		Vector3f to = new Vector3f(16, 16, 16);

		//TextureのUVの指定
		BlockFaceUV uv = new BlockFaceUV(new float[] { 0.0F, 0.0F, 16.0F, 16.0F }, 0);

		//面の描画の設定、ほぼ使用されないと思われる。
		//第一引数:cullface(使用されない)
		//第二引数:tintindex兼layer兼renderPass
		//第三引数:テクスチャの場所(使用されない)
		//第四引数:TextureのUVの指定
		BlockPartFace partFace = new BlockPartFace(face, face.getIndex(), new ResourceLocation("blocks/stone").toString(), uv);

		//Quadの設定
		//第一引数:面の始点
		//第二引数:面の終点
		//第三引数:面の描画の設定
		//第四引数:テクスチャ
		//第五引数:面の方向
		//第六引数:モデルの回転
		//第七引数:面の回転(nullで自動)
		//第八引数:モデルの回転に合わせてテクスチャを回転させるか
		//第九引数:陰らせるかどうか
		BakedQuad bakedQuad = faceBakery.makeBakedQuad(from, to, partFace, stone, face, ModelRotation.X0_Y0, null, true, true);

		return Lists.newArrayList(bakedQuad);
	}

	/**
	 面が不透明なBlockに接しているか否かを問わず、描画する面を指定する。
	 見ての通り引数にEnumFacingはないので、このメソッドの中でfor(EnumFacing facing : EnumFacing.values())などしてあげる必要あり。
	 もちろん、一つの面につき一つのQuadのような制約はない。
	 今回は、全てgetFaceQuadsで指定するので空Listを返す。
	
	 @see #getFaceQuads
	 */
	@Override
	public List<BakedQuad> getGeneralQuads() {
		return Collections.emptyList();
	}

	/**
	 BakedQuadのVertexの書式を指定するっぽい？
	 使用されている痕跡がないので取り敢えずデフォルトらしきものを返す。
	 */
	@Override
	public VertexFormat getFormat() {
		return Attributes.DEFAULT_BAKED_FORMAT;
	}

	/**
	 光を通すかどうか。
	 trueなら、光を通さず影が生じる。
	 */
	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	/**
	 GUI内で3D描画するかどうか。
	 Blockなのでtrue。
	 */
	@Override
	public boolean isGui3d() {
		return true;
	}

	/**
	 通常、falseである。
	 */
	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	/**
	 パーティクルに使われる。
	 Randomを通せば全部の面をパーティクルにのせるとかもできるかもしれない。
	 が、Modelを代表したTextureとして求められる場合が多そうなので、普通に返したほうが無難。
	 */
	@Override
	public TextureAtlasSprite getParticleTexture() {
		return stone;
	}

	/**
	 非推奨メソッド。デフォルトを返しておけば間違いはないはず。
	 */
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

}
