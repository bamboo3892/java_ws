package com.okina.main;

import static com.okina.main.TestCore.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.okina.item.ItemConnector;
import com.okina.item.ItemCraftingFilter;
import com.okina.item.ItemFilter;
import com.okina.item.ItemFilteringGlyph;
import com.okina.item.ItemHandySignalEmitter;
import com.okina.item.ItemWrench;
import com.okina.item.itemBlock.ItemBlockTestMod;
import com.okina.item.itemBlock.ItemMultiBlock;
import com.okina.multiblock.BlockBaseFrame;
import com.okina.multiblock.BlockFrame;
import com.okina.multiblock.BlockFrameLine;
import com.okina.multiblock.BlockFrameTileEntity;
import com.okina.multiblock.BlockPipe;
import com.okina.multiblock.BlockPipeTileEntity;
import com.okina.multiblock.DisassemblyTable;
import com.okina.multiblock.DisassemblyTableTileEntity;
import com.okina.multiblock.MultiBlockCasing;
import com.okina.multiblock.MultiBlockCasingTileEntity;
import com.okina.multiblock.MultiBlockCore;
import com.okina.multiblock.MultiBlockCoreTileEntity;
import com.okina.multiblock.construct.block.ConstructClockPulser;
import com.okina.multiblock.construct.block.ConstructContainer;
import com.okina.multiblock.construct.block.ConstructCrusher;
import com.okina.multiblock.construct.block.ConstructDispatcher;
import com.okina.multiblock.construct.block.ConstructEnergyProvider;
import com.okina.multiblock.construct.block.ConstructEventCatcher;
import com.okina.multiblock.construct.block.ConstructFurnace;
import com.okina.multiblock.construct.block.ConstructInterface;
import com.okina.multiblock.construct.block.ConstructRepeater;
import com.okina.multiblock.construct.block.ConstructStorage;
import com.okina.multiblock.construct.block.ConstructVirtualGlower;
import com.okina.multiblock.construct.parts.ClockPulserPart;
import com.okina.multiblock.construct.parts.ContainerPart;
import com.okina.multiblock.construct.parts.CrusherPart;
import com.okina.multiblock.construct.parts.DispatcherPart;
import com.okina.multiblock.construct.parts.EnergyProviderPart;
import com.okina.multiblock.construct.parts.EventCatcherPart;
import com.okina.multiblock.construct.parts.FurnacePart;
import com.okina.multiblock.construct.parts.RepeaterPart;
import com.okina.multiblock.construct.parts.StoragePart;
import com.okina.multiblock.construct.parts.VirtualGlowerPart;
import com.okina.multiblock.construct.tileentity.ConstructClockPulserTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructContainerTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructCrusherTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructDispatcherTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructEnergyProviderTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructEventCatcherTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructFurnaceTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructInterfaceTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructRepeaterTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructStorageTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructVirtualGrowerTileEntity;
import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;
import com.okina.network.SimpleTilePacket.SimpleTilePacketHandler;
import com.okina.network.SimpleTilePacket.SimpleTileReplyPacketHandler;
import com.okina.register.ConstructPartRegistry;
import com.okina.register.CrusherRecipeRegister;
import com.okina.register.EnergyProdeceRecipeRegister;
import com.okina.register.VirtualGrowerRecipeRegister;
import com.okina.tileentity.ISimpleTilePacketUser;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CommonProxy {

	public void loadConfiguration(File file) {
		Configuration config = new Configuration(file);
		try{
			config.load();
			particleLevel = config.getInt("Particle Level", "EFFECT", 3, 0, 3, "");
		}catch (Exception e){
			FMLLog.severe("config load errer");
		}finally{
			config.save();
		}
	}

	public void registerBlock() {
		pipe = new BlockPipe();
		GameRegistry.registerBlock(pipe, "pipe");

		baseFrame = new BlockBaseFrame[5];
		constructStorage = new ConstructStorage[5];
		constructClockPulser = new ConstructClockPulser[5];
		constructRepeater = new ConstructRepeater[5];
		constructEventCatcher = new ConstructEventCatcher[5];
		constructContainer = new ConstructContainer[5];
		constructFurnace = new ConstructFurnace[5];
		constructVirtualGrower = new ConstructVirtualGlower[5];
		constructEnergyProvider = new ConstructEnergyProvider[5];
		constructCrusher = new ConstructCrusher[5];
		constructDispatcher = new ConstructDispatcher[5];
		constructInterface = new ConstructInterface[5];
		for (int i = 0; i < 5; i++){
			baseFrame[i] = new BlockBaseFrame(i);
			GameRegistry.registerBlock(baseFrame[i], "baseFrame_" + i);
			constructStorage[i] = new ConstructStorage(i);
			GameRegistry.registerBlock(constructStorage[i], ItemBlockTestMod.class, "storage_" + i);
			constructClockPulser[i] = new ConstructClockPulser(i);
			GameRegistry.registerBlock(constructClockPulser[i], ItemBlockTestMod.class, "clockPulser_" + i);
			constructRepeater[i] = new ConstructRepeater(i);
			GameRegistry.registerBlock(constructRepeater[i], ItemBlockTestMod.class, "repeater_" + i);
			constructEventCatcher[i] = new ConstructEventCatcher(i);
			GameRegistry.registerBlock(constructEventCatcher[i], ItemBlockTestMod.class, "eventCatcher_" + i);
			constructContainer[i] = new ConstructContainer(i);
			GameRegistry.registerBlock(constructContainer[i], ItemBlockTestMod.class, "container_" + i);
			constructFurnace[i] = new ConstructFurnace(i);
			GameRegistry.registerBlock(constructFurnace[i], ItemBlockTestMod.class, "furnace_" + i);
			constructVirtualGrower[i] = new ConstructVirtualGlower(i);
			GameRegistry.registerBlock(constructVirtualGrower[i], ItemBlockTestMod.class, "virtualGrower_" + i);
			constructEnergyProvider[i] = new ConstructEnergyProvider(i);
			GameRegistry.registerBlock(constructEnergyProvider[i], ItemBlockTestMod.class, "energyProvider_" + i);
			constructCrusher[i] = new ConstructCrusher(i);
			GameRegistry.registerBlock(constructCrusher[i], ItemBlockTestMod.class, "crusher_" + i);
			constructDispatcher[i] = new ConstructDispatcher(i);
			GameRegistry.registerBlock(constructDispatcher[i], ItemBlockTestMod.class, "dispatcher_" + i);
			constructInterface[i] = new ConstructInterface(i);
			GameRegistry.registerBlock(constructInterface[i], ItemBlockTestMod.class, "interface_" + i);
		}

		blockFrame = new BlockFrame();
		GameRegistry.registerBlock(blockFrame, "blockFrame");
		blockFrameLine = new BlockFrameLine();
		GameRegistry.registerBlock(blockFrameLine, "blockFrameLine");
		multiBlockCore = new MultiBlockCore();
		GameRegistry.registerBlock(multiBlockCore, ItemMultiBlock.class, "multiBlockCore");
		multiBlockCasing = new MultiBlockCasing();
		GameRegistry.registerBlock(multiBlockCasing, "multiBlockCasing");
		disassemblyTable = new DisassemblyTable();
		GameRegistry.registerBlock(disassemblyTable, "disassemblyTable");
	}

	public void registerItem() {
		wrench = new ItemWrench();
		GameRegistry.registerItem(wrench, "wrench");
		connector = new ItemConnector();
		GameRegistry.registerItem(connector, "connector");
		handyEmitter = new ItemHandySignalEmitter();
		GameRegistry.registerItem(handyEmitter, "handyEmitter");
		filter = new ItemFilter();
		GameRegistry.registerItem(filter, "filter");
		filtering = new Item[4];
		filtering[0] = new ItemFilteringGlyph(0.25f).setCreativeTab(testCreativeTab).setUnlocalizedName(AUTHER + ".filtering_c");
		GameRegistry.registerItem(filtering[0], "filtering_c");
		filtering[1] = new ItemFilteringGlyph(0.58f).setCreativeTab(testCreativeTab).setUnlocalizedName(AUTHER + ".filtering_g");
		GameRegistry.registerItem(filtering[1], "filtering_g");
		filtering[2] = new ItemFilteringGlyph(0.78f).setCreativeTab(testCreativeTab).setUnlocalizedName(AUTHER + ".filtering_e");
		GameRegistry.registerItem(filtering[2], "filtering_e");
		filtering[3] = new ItemFilteringGlyph(1f).setCreativeTab(testCreativeTab).setUnlocalizedName(AUTHER + ".filtering_f");
		GameRegistry.registerItem(filtering[3], "filtering_f");
		craftingFilter = new ItemCraftingFilter();
		GameRegistry.registerItem(craftingFilter, "craftingFilter");
		itemMultiBlock = new ItemMultiBlock(multiBlockCore);
		toilet = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName(AUTHER + ".toilet");
		GameRegistry.registerItem(toilet, "toilet");
		doubleDustIron = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName(AUTHER + ".dustDoubleIron");
		GameRegistry.registerItem(doubleDustIron, "dustDoubleIron");
		doubleDustGold = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName(AUTHER + ".dustDoubleGold");
		GameRegistry.registerItem(doubleDustGold, "dustDoubleGold");
		greenPowder = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName(AUTHER + ".greenPowder");
		GameRegistry.registerItem(greenPowder, "greenPowder");
		greenMatter = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName(AUTHER + ".greenMatter");
		GameRegistry.registerItem(greenMatter, "greenMatter");
		organicMatter = new ItemFood(4, true) {
			@Override
			public void onFoodEaten(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
				super.onFoodEaten(itemStack, world, entityPlayer);
				ForgeHooks.onPlayerTossEvent(entityPlayer, new ItemStack(toilet, 1), true);
			}
		}.setCreativeTab(testCreativeTab).setUnlocalizedName("organicMatter");
		GameRegistry.registerItem(organicMatter, "organicMatter");
		burntOrganicMatter = new Item().setCreativeTab(testCreativeTab).setUnlocalizedName(AUTHER + ".burntOrganicMatter");
		GameRegistry.registerItem(burntOrganicMatter, "burntOrganicMatter");
	}

	public void registerModel() {

	}

	public void registerTileEntity() {
		GameRegistry.registerTileEntity(BlockPipeTileEntity.class, "blockPipeTileEntity");
		GameRegistry.registerTileEntity(BlockFrameTileEntity.class, "blockFrameTileEntity");
		GameRegistry.registerTileEntity(MultiBlockCoreTileEntity.class, "multiBlockCoreTileEntity");
		GameRegistry.registerTileEntity(MultiBlockCasingTileEntity.class, "multiBlockCasingTileEntity");
		GameRegistry.registerTileEntity(DisassemblyTableTileEntity.class, "disassemblyTableTileEntity");

		//construct parts
		GameRegistry.registerTileEntity(ConstructFurnaceTileEntity.class, "constructFurnaceTileEntity");
		ConstructPartRegistry.registerConstructPart(constructFurnace, new FurnacePart());
		GameRegistry.registerTileEntity(ConstructStorageTileEntity.class, "constructStorageTileEntity");
		ConstructPartRegistry.registerConstructPart(constructStorage, new StoragePart());
		GameRegistry.registerTileEntity(ConstructInterfaceTileEntity.class, "constructInterfaceTileEntity");
		GameRegistry.registerTileEntity(ConstructClockPulserTileEntity.class, "constructClockPulserTileEntity");
		ConstructPartRegistry.registerConstructPart(constructClockPulser, new ClockPulserPart());
		GameRegistry.registerTileEntity(ConstructRepeaterTileEntity.class, "constructRepeaterTileEntity");
		ConstructPartRegistry.registerConstructPart(constructRepeater, new RepeaterPart());
		GameRegistry.registerTileEntity(ConstructContainerTileEntity.class, "constructContainerTileEntity");
		ConstructPartRegistry.registerConstructPart(constructContainer, new ContainerPart());
		GameRegistry.registerTileEntity(ConstructCrusherTileEntity.class, "constructCrusherTileEntity");
		ConstructPartRegistry.registerConstructPart(constructCrusher, new CrusherPart());
		GameRegistry.registerTileEntity(ConstructEnergyProviderTileEntity.class, "constructEnergyProviderTileEntity");
		ConstructPartRegistry.registerConstructPart(constructEnergyProvider, new EnergyProviderPart());
		GameRegistry.registerTileEntity(ConstructVirtualGrowerTileEntity.class, "constructVirtualGlowerTileEntity");
		ConstructPartRegistry.registerConstructPart(constructVirtualGrower, new VirtualGlowerPart());
		GameRegistry.registerTileEntity(ConstructEventCatcherTileEntity.class, "constructEventCatcherTileEntity");
		ConstructPartRegistry.registerConstructPart(constructEventCatcher, new EventCatcherPart());
		GameRegistry.registerTileEntity(ConstructDispatcherTileEntity.class, "constructDispatcherTileEntity");
		ConstructPartRegistry.registerConstructPart(constructDispatcher, new DispatcherPart());
	}

	public void registerRecipe() {
		//crafting
		GameRegistry.addRecipe(new ItemStack(pipe, 8, 0), new Object[] { "XRX", 'X', Blocks.oak_fence, 'R', Blocks.redstone_block });
		GameRegistry.addRecipe(new ItemStack(blockFrame, 1, 0), new Object[] { " X ", "XRX", 'X', Items.coal, 'R', Blocks.stone });
		GameRegistry.addRecipe(new ItemStack(blockFrameLine, 8, 0), new Object[] { " X ", "XRX", 'X', Items.coal, 'R', Blocks.redstone_block });
		GameRegistry.addRecipe(new ItemStack(multiBlockCasing, 8, 0), new Object[] { " X ", "XRX", 'X', Blocks.glass, 'R', Blocks.glass_pane });

		GameRegistry.addRecipe(new ItemStack(baseFrame[0], 1), new Object[] { "XXX", "X X", "XXX", 'X', Blocks.oak_fence });
		for (int i = 0; i < 5; i++){
			GameRegistry.addRecipe(new ItemStack(constructFurnace[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.furnace, 'S', Blocks.stone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructStorage[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.chest, 'S', Blocks.oak_fence, 'C', Blocks.planks });
			GameRegistry.addRecipe(new ItemStack(constructInterface[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.obsidian, 'S', Blocks.wool, 'C', Items.string });
			GameRegistry.addRecipe(new ItemStack(constructClockPulser[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.redstone_block, 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructRepeater[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.quartz_block, 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructContainer[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.glass, 'S', Blocks.glass_pane, 'C', Blocks.glass_pane });
			GameRegistry.addRecipe(new ItemStack(constructCrusher[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.cactus, 'S', Items.stone_pickaxe, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructEnergyProvider[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Items.ender_pearl, 'S', Items.enchanted_book, 'C', Blocks.nether_brick });
			GameRegistry.addRecipe(new ItemStack(constructVirtualGrower[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.grass, 'S', Blocks.leaves, 'C', Items.flower_pot });
			GameRegistry.addRecipe(new ItemStack(constructEventCatcher[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Items.comparator, 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructDispatcher[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.lapis_block, 'S', Items.redstone, 'C', Blocks.oak_fence });
		}
		Item[] material = { Items.iron_ingot, Items.gold_ingot, Items.diamond, Items.emerald };
		for (int i = 0; i < 4; i++){
			GameRegistry.addShapelessRecipe(new ItemStack(baseFrame[i + 1], 1), new Object[] { baseFrame[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructFurnace[i + 1], 1), new Object[] { constructFurnace[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructStorage[i + 1], 1), new Object[] { constructStorage[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructInterface[i + 1], 1), new Object[] { constructInterface[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructClockPulser[i + 1], 1), new Object[] { constructClockPulser[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructRepeater[i + 1], 1), new Object[] { constructRepeater[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructContainer[i + 1], 1), new Object[] { constructContainer[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructCrusher[i + 1], 1), new Object[] { constructCrusher[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructEnergyProvider[i + 1], 1), new Object[] { constructEnergyProvider[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructVirtualGrower[i + 1], 1), new Object[] { constructVirtualGrower[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructEventCatcher[i + 1], 1), new Object[] { constructEventCatcher[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructDispatcher[i + 1], 1), new Object[] { constructDispatcher[i], material[i] });
		}
		GameRegistry.addRecipe(new ItemStack(wrench, 1), new Object[] { "SBS", " B ", " B ", 'B', Items.reeds, 'S', Items.iron_ingot });
		GameRegistry.addRecipe(new ItemStack(connector, 1), new Object[] { " S ", "D  ", 'S', Blocks.sapling, 'D', Items.stick });
		GameRegistry.addRecipe(new ItemStack(handyEmitter, 1), new Object[] { " S ", "D  ", 'S', Items.redstone, 'D', Blocks.sapling });
		GameRegistry.addRecipe(new ItemStack(craftingFilter, 2, 0), new Object[] { "XRX", 'X', Items.stick, 'R', Blocks.crafting_table });
		GameRegistry.addShapelessRecipe(new ItemStack(filter, 1), new Object[] { Blocks.trapdoor });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[0], 1), new Object[] { filter, Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[0], 1), new Object[] { filtering[3], Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[1], 1), new Object[] { filtering[0], Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[2], 1), new Object[] { filtering[1], Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[3], 1), new Object[] { filtering[2], Blocks.cactus });
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(greenMatter, 1), new Object[] { Blocks.cactus, greenPowder, "dyeGreen" }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(organicMatter, 1), new Object[] { Blocks.cactus, greenPowder, "dyeGreen", Items.apple, Items.melon, Items.reeds, Items.carrot, Items.potato, Items.wheat }));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(organicMatter, 1), new Object[] { greenMatter, Items.apple, Items.melon, Items.reeds, Items.carrot, Items.potato, Items.wheat }));

		List<ItemStack> irondusts = OreDictionary.getOres("dustIron");
		if(irondusts != null && irondusts.size() > 0){
			ItemStack dust = irondusts.get(0).copy();
			if(dust != null){
				dust.stackSize = 2;
				GameRegistry.addShapelessRecipe(dust, new ItemStack(doubleDustIron, 1));
			}
		}
		List<ItemStack> golddusts = OreDictionary.getOres("dustIron");
		if(golddusts != null && golddusts.size() > 0){
			ItemStack dust = golddusts.get(0).copy();
			if(dust != null){
				dust.stackSize = 2;
				GameRegistry.addShapelessRecipe(dust, new ItemStack(doubleDustGold, 1));
			}
		}
		//smelting
		GameRegistry.addSmelting(doubleDustIron, new ItemStack(Items.iron_ingot, 2), 0.3F);
		GameRegistry.addSmelting(doubleDustGold, new ItemStack(Items.gold_ingot, 2), 0.3F);
		GameRegistry.addSmelting(organicMatter, new ItemStack(burntOrganicMatter, 1), 0.3F);

		//crusher
		CrusherRecipeRegister.instance.registerRecipe(OreDictionary.getOreID("oreIron"), new ItemStack(TestCore.doubleDustIron, 1));
		CrusherRecipeRegister.instance.registerRecipe(OreDictionary.getOreID("oreGold"), new ItemStack(TestCore.doubleDustGold, 1));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cobblestone, 1), new ItemStack(Blocks.sand, 1));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.stone, 1), new ItemStack(Blocks.gravel, 1));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Items.bone, 1), new ItemStack(Items.dye, 6, 15));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Items.blaze_rod, 1), new ItemStack(Items.blaze_powder, 4));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cactus, 1), new ItemStack(greenPowder, 1));

		//virtual grower
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cactus, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.apple, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.melon, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.reeds, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.carrot, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.potato, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.wheat, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.hay_block, 1), 3000, 1000);
		VirtualGrowerRecipeRegister.instance.registerRecipe(OreDictionary.getOreID("logWood"), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(OreDictionary.getOreID("treeSapling"), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(OreDictionary.getOreID("treeLeaves"), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.deadbush, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.vine, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.tallgrass, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.speckled_melon, 1), 5000, 5000);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.golden_apple, 1), 5000, 5000);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.nether_star, 1), 40000, 10000);

		//energy provider
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cactus, 1), 1000, 100);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(greenMatter, 1), 30000, 300);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(organicMatter, 1), 1000000, 500);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(toilet, 1), 1500000, 500);
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(burntOrganicMatter, 1), 2000000, 1000);
	}

	public void registerRenderer() {}

	public void registerPacket() {
		packetDispatcher.registerMessage(SimpleTilePacketHandler.class, SimpleTilePacket.class, SIMPLETILE_PACKET_ID, Side.SERVER);
		packetDispatcher.registerMessage(SimpleTileReplyPacketHandler.class, SimpleTilePacket.class, SIMPLETILE_REPLY_PACKET_ID, Side.CLIENT);
	}

	public void LoadNEI() {}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private Map<PacketType, List<BlockPos>> positionListMap = new HashMap();

	/**return true if newly marked*/
	public boolean markForTileUpdate(BlockPos position, PacketType type) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			if(positionListMap.get(type) != null){
				List<BlockPos> positionList = positionListMap.get(type);
				for (BlockPos tmp : positionList){
					if(tmp != null && tmp.equals(position)){
						//System.out.println("already marked update");
						return false;
					}
				}
				positionList.add(position);
			}else{
				List<BlockPos> positionList = new ArrayList();
				positionList.add(position);
				positionListMap.put(type, positionList);
			}
			//System.out.println("update marked");
			return true;
		}else{
			return false;
		}
	}

	void sendAllUpdatePacket() {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			for (PacketType type : PacketType.values()){
				List<BlockPos> positionList = positionListMap.get(type);
				if(positionList != null){
					for (BlockPos position : positionList){
						TileEntity tile = MinecraftServer.getServer().getEntityWorld().getTileEntity(position);
						if(tile instanceof ISimpleTilePacketUser){
							SimpleTilePacket packet = ((ISimpleTilePacketUser) tile).getPacket(type);
							if(packet != null){
								packetDispatcher.sendToAll(packet);
								//System.out.println("send packet");
							}
						}
					}
					positionList.clear();
				}
			}
		}
	}

	public void sendPacket(SimpleTilePacket packet) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			packetDispatcher.sendToAll(packet);
		}else{
			packetDispatcher.sendToServer(packet);
		}
	}

	protected void spawnParticle(World world, int id, Object... objects) {}

}
