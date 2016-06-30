package com.example.examplemod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod {
	public static final String MODID = "examplemod";
	public static final String VERSION = "1.0";

	/** MOD名称 */
	public static final String MOD_NAME = "SampleMod";
	/** MODのバージョン */
	public static final String MOD_VERSION = "0.0.1";
	/** 早紀に読み込まれるべき前提MODをバージョン込みで指定 */
	public static final String MOD_DEPENDENCIES = "required-after:Forge@[1.8-11.14.0.1239,)";
	/** 起動出来るMinecraft本体のバージョン。記法はMavenのVersion Range Specificationを検索すること。 */
	public static final String MOD_ACCEPTED_MC_VERSIONS = "[1.8,1.8.9]";

	public static Block sampleBlock2;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		//		sampleBlock = new SampleBlock();
		//		//ブロックの登録。登録文字列はMOD内で被らなければ何でも良い。
		//
		//		GameRegistry.registerBlock(sampleBlock, SampleItemBlock.class, "sampleblock");
		//
		//		//テクスチャ・モデル指定JSONファイル名の登録。
		//		if(event.getSide().isClient()){
		//			//モデルJSONファイルのファイル名を登録。1IDで1つだけなら、登録名はGameRegistryでの登録名と同じものにする。
		//			//ItemStackのmetadataで種類を分けて描画させたい場合。登録名を予め登録する。
		//			ModelBakery.addVariantName(Item.getItemFromBlock(sampleBlock), MODID + ":" + "sampleblock0", MODID + ":" + "sampleblock1");
		//			//1IDで複数モデルを登録するなら、上のメソッドで登録した登録名を指定する。
		//			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(sampleBlock), 0, new ModelResourceLocation(MODID + ":" + "sampleblock0", "inventory"));
		//			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(sampleBlock), 1, new ModelResourceLocation(MODID + ":" + "sampleblock1", "inventory"));
		//			//ModelLoader.setCustomStateMapper(sampleBlock, (new StateMap.Builder()).addPropertiesToIgnore(sampleBlock.METADATA).build());
		//			//上記のようにして無視させたいPropertyを指定することもできる。
		//			//その他にもここで設定できる項目もあるが割愛させていただく。
		//		}

		sampleBlock2 = new Block(Material.rock) {
			public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
				return ItemDye.dyeColors[renderPass];
			}
		}.setUnlocalizedName("sampleBlock2").setCreativeTab(CreativeTabs.tabBlock);

		GameRegistry.registerBlock(sampleBlock2, SampleItemBlock.class, "sampleBlock2");//ブロックの描画はこっちの名前のblockstatesの中のファイルを探す。

		//テクスチャ・モデル指定JSONファイル名の登録。
		if(event.getSide().isClient()){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(sampleBlock2), 0, new ModelResourceLocation(MODID + ":sampleBlock2", null));//アイテムの描画だけ？
			//			ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(sampleBlock2), new ItemMeshDefinition() {
			//				public ModelResourceLocation getModelLocation(ItemStack stack) {
			//					return new ModelResourceLocation(new ResourceLocation(MODID, "sampleBlock2"), "inventory");
			//				}
			//			});
		}

		//ModelLoaderの登録。
		ModelLoaderRegistry.registerLoader(new SampleModelLoader());
	}

}
