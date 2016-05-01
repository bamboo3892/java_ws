package com.test.main;

import static com.test.main.TestCore.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.item.ItemConnector;
import com.test.item.ItemCraftingFilter;
import com.test.item.ItemFilter;
import com.test.item.ItemFilteringGlyph;
import com.test.item.ItemHandySignalEmitter;
import com.test.item.ItemWrench;
import com.test.item.TestItem;
import com.test.item.itemBlock.ItemBlockTestMod;
import com.test.item.itemBlock.ItemMultiBlock;
import com.test.multiblock.BlockBaseFrame;
import com.test.multiblock.BlockFrame;
import com.test.multiblock.BlockFrameLine;
import com.test.multiblock.BlockFrameTileEntity;
import com.test.multiblock.BlockPipe;
import com.test.multiblock.BlockPipeTileEntity;
import com.test.multiblock.DisassemblyTable;
import com.test.multiblock.DisassemblyTableTileEntity;
import com.test.multiblock.MultiBlockCasing;
import com.test.multiblock.MultiBlockCasingTileEntity;
import com.test.multiblock.MultiBlockCore;
import com.test.multiblock.MultiBlockCoreTileEntity;
import com.test.multiblock.construct.block.ConstructClockPulser;
import com.test.multiblock.construct.block.ConstructContainer;
import com.test.multiblock.construct.block.ConstructCrusher;
import com.test.multiblock.construct.block.ConstructDispatcher;
import com.test.multiblock.construct.block.ConstructEnergyProvider;
import com.test.multiblock.construct.block.ConstructEventCatcher;
import com.test.multiblock.construct.block.ConstructFurnace;
import com.test.multiblock.construct.block.ConstructInterface;
import com.test.multiblock.construct.block.ConstructRepeater;
import com.test.multiblock.construct.block.ConstructStorage;
import com.test.multiblock.construct.block.ConstructVirtualGlower;
import com.test.multiblock.construct.parts.ClockPulserPart;
import com.test.multiblock.construct.parts.ContainerPart;
import com.test.multiblock.construct.parts.CrusherPart;
import com.test.multiblock.construct.parts.DispatcherPart;
import com.test.multiblock.construct.parts.EnergyProviderPart;
import com.test.multiblock.construct.parts.EventCatcherPart;
import com.test.multiblock.construct.parts.FurnacePart;
import com.test.multiblock.construct.parts.RepeaterPart;
import com.test.multiblock.construct.parts.StoragePart;
import com.test.multiblock.construct.parts.VirtualGlowerPart;
import com.test.multiblock.construct.tileentity.ConstructClockPulserTileEntity;
import com.test.multiblock.construct.tileentity.ConstructContainerTileEntity;
import com.test.multiblock.construct.tileentity.ConstructCrusherTileEntity;
import com.test.multiblock.construct.tileentity.ConstructDispatcherTileEntity;
import com.test.multiblock.construct.tileentity.ConstructEnergyProviderTileEntity;
import com.test.multiblock.construct.tileentity.ConstructEventCatcherTileEntity;
import com.test.multiblock.construct.tileentity.ConstructFurnaceTileEntity;
import com.test.multiblock.construct.tileentity.ConstructInterfaceTileEntity;
import com.test.multiblock.construct.tileentity.ConstructRepeaterTileEntity;
import com.test.multiblock.construct.tileentity.ConstructStorageTileEntity;
import com.test.multiblock.construct.tileentity.ConstructVirtualGrowerTileEntity;
import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.network.SimpleTilePacket.SimpleTilePacketHandler;
import com.test.network.SimpleTilePacket.SimpleTileReplyPacketHandler;
import com.test.register.ConstructPartRegistry;
import com.test.register.CrusherRecipeRegister;
import com.test.register.EnergyProdeceRecipeRegister;
import com.test.register.VirtualGrowerRecipeRegister;
import com.test.tileentity.ISimpleTilePacketUser;
import com.test.tileentity.TileTestBlockTileEntity;
import com.test.utils.Position;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class CommonProxy {

	public void loadConfiguration(File file) {
		Configuration config = new Configuration(file);
		try{
			config.load();
			isHoge = config.get("GENERAL", "isHOGE", false).getBoolean(false);
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
		constructFurnace = new ConstructFurnace[5];
		constructStorage = new ConstructStorage[5];
		constructInterface = new ConstructInterface[5];
		constructClockPulser = new ConstructClockPulser[5];
		constructRepeater = new ConstructRepeater[5];
		constructContainer = new ConstructContainer[5];
		constructCrusher = new ConstructCrusher[5];
		constructEnergyProvider = new ConstructEnergyProvider[5];
		constructVirtualGrower = new ConstructVirtualGlower[5];
		constructEventCatcher = new ConstructEventCatcher[5];
		constructDispatcher = new ConstructDispatcher[5];

		for (int i = 0; i < 5; i++){
			baseFrame[i] = new BlockBaseFrame(i);
			GameRegistry.registerBlock(baseFrame[i], "baseFrame." + i);
			constructFurnace[i] = new ConstructFurnace(i);
			GameRegistry.registerBlock(constructFurnace[i], ItemBlockTestMod.class, "furnace." + i);
			constructStorage[i] = new ConstructStorage(i);
			GameRegistry.registerBlock(constructStorage[i], ItemBlockTestMod.class, "storage." + i);
			constructInterface[i] = new ConstructInterface(i);
			GameRegistry.registerBlock(constructInterface[i], ItemBlockTestMod.class, "interface." + i);
			constructClockPulser[i] = new ConstructClockPulser(i);
			GameRegistry.registerBlock(constructClockPulser[i], ItemBlockTestMod.class, "clockPulser." + i);
			constructRepeater[i] = new ConstructRepeater(i);
			GameRegistry.registerBlock(constructRepeater[i], ItemBlockTestMod.class, "repeater." + i);
			constructContainer[i] = new ConstructContainer(i);
			GameRegistry.registerBlock(constructContainer[i], ItemBlockTestMod.class, "container." + i);
			constructCrusher[i] = new ConstructCrusher(i);
			GameRegistry.registerBlock(constructCrusher[i], ItemBlockTestMod.class, "crusher." + i);
			constructEnergyProvider[i] = new ConstructEnergyProvider(i);
			GameRegistry.registerBlock(constructEnergyProvider[i], ItemBlockTestMod.class, "energyProvider." + i);
			constructVirtualGrower[i] = new ConstructVirtualGlower(i);
			GameRegistry.registerBlock(constructVirtualGrower[i], ItemBlockTestMod.class, "virtualGlower." + i);
			constructEventCatcher[i] = new ConstructEventCatcher(i);
			GameRegistry.registerBlock(constructEventCatcher[i], ItemBlockTestMod.class, "eventCatcher." + i);
			constructDispatcher[i] = new ConstructDispatcher(i);
			GameRegistry.registerBlock(constructDispatcher[i], ItemBlockTestMod.class, "dispatcher." + i);
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
		testItem = new TestItem();
		GameRegistry.registerItem(testItem, "testItem");
		wrench = new ItemWrench();
		GameRegistry.registerItem(wrench, "wrench");
		connector = new ItemConnector();
		GameRegistry.registerItem(connector, "connector");
		filter = new ItemFilter();
		GameRegistry.registerItem(filter, "filter");
		handyEmitter = new ItemHandySignalEmitter();
		GameRegistry.registerItem(handyEmitter, "handyEmitter");
		craftingFilter = new ItemCraftingFilter();
		GameRegistry.registerItem(craftingFilter, "craftingFilter");
		filtering = new Item[4];
		filtering[0] = new ItemFilteringGlyph(0.25f).setCreativeTab(testCreativeTab).setUnlocalizedName("filtering_c").setTextureName(MODID + ":filtering_c");
		GameRegistry.registerItem(filtering[0], "filtering_c");
		filtering[1] = new ItemFilteringGlyph(0.58f).setCreativeTab(testCreativeTab).setUnlocalizedName("filtering_g").setTextureName(MODID + ":filtering_g");
		GameRegistry.registerItem(filtering[1], "filtering_g");
		filtering[2] = new ItemFilteringGlyph(0.78f).setCreativeTab(testCreativeTab).setUnlocalizedName("filtering_e").setTextureName(MODID + ":filtering_e");
		GameRegistry.registerItem(filtering[2], "filtering_e");
		filtering[3] = new ItemFilteringGlyph(1f).setCreativeTab(testCreativeTab).setUnlocalizedName("filtering_f").setTextureName(MODID + ":filtering_f");
		GameRegistry.registerItem(filtering[3], "filtering_f");
		itemMultiBlock = new ItemMultiBlock(multiBlockCore);
	}

	public void registerTileEntity() {
		GameRegistry.registerTileEntity(TileTestBlockTileEntity.class, "tileTestBlockTileEntity");
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
		GameRegistry.addRecipe(new ItemStack(pipe, 8, 0), new Object[] { "XRX", 'X', Blocks.fence, 'R', Blocks.redstone_block });
		GameRegistry.addRecipe(new ItemStack(blockFrame, 1, 0), new Object[] { " X ", "XRX", 'X', Items.coal, 'R', Blocks.stone });
		GameRegistry.addRecipe(new ItemStack(blockFrameLine, 8, 0), new Object[] { " X ", "XRX", 'X', Items.coal, 'R', Blocks.redstone_block });
		GameRegistry.addRecipe(new ItemStack(multiBlockCasing, 8, 0), new Object[] { " X ", "XRX", 'X', Blocks.glass, 'R', Blocks.glass_pane });

		GameRegistry.addRecipe(new ItemStack(baseFrame[0], 1), new Object[] { "XXX", "X X", "XXX", 'X', Blocks.fence });
		for (int i = 0; i < 5; i++){
			GameRegistry.addRecipe(new ItemStack(constructFurnace[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.furnace, 'S', Blocks.stone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructStorage[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.chest, 'S', Blocks.fence, 'C', Blocks.planks });
			GameRegistry.addRecipe(new ItemStack(constructInterface[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.obsidian, 'S', Blocks.wool, 'C', Items.string });
			GameRegistry.addRecipe(new ItemStack(constructClockPulser[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.redstone_block, 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructRepeater[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.quartz_block, 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructContainer[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.glass, 'S', Blocks.glass_pane, 'C', Blocks.glass_pane });
			GameRegistry.addRecipe(new ItemStack(constructCrusher[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.cactus, 'S', Items.stone_pickaxe, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructEnergyProvider[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Items.ender_pearl, 'S', Items.enchanted_book, 'C', Blocks.nether_brick });
			GameRegistry.addRecipe(new ItemStack(constructVirtualGrower[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.grass, 'S', Blocks.leaves, 'C', Items.flower_pot });
			GameRegistry.addRecipe(new ItemStack(constructEventCatcher[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Items.comparator, 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructDispatcher[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.lapis_block, 'S', Items.redstone, 'C', Blocks.fence });
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
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[0], 1), new Object[] { filtering[3], Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[1], 1), new Object[] { filtering[0], Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[2], 1), new Object[] { filtering[1], Blocks.cactus });
		GameRegistry.addShapelessRecipe(new ItemStack(filtering[3], 1), new Object[] { filtering[2], Blocks.cactus });

		//smelting

		//crusher
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cobblestone, 1), new ItemStack(Blocks.sand, 1));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.stone, 1), new ItemStack(Blocks.gravel, 1));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Items.bone, 1), new ItemStack(Items.dye, 6, 15));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Items.blaze_rod, 1), new ItemStack(Items.blaze_powder, 4));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cactus, 1), (new ItemStack(filtering[0], 1)));

		//virtual grower
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cactus, 1), 400, 100);
		VirtualGrowerRecipeRegister.instance.registerRecipe(new ItemStack(Items.nether_star, 1), 40000, 10000);

		//energy provider
		EnergyProdeceRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cactus, 1), 10000, 100);
	}

	public void registerRenderer() {}

	public void registerPacket() {
		packetDispatcher.registerMessage(SimpleTilePacketHandler.class, SimpleTilePacket.class, SIMPLETILE_PACKET_ID, Side.SERVER);
		packetDispatcher.registerMessage(SimpleTileReplyPacketHandler.class, SimpleTilePacket.class, SIMPLETILE_REPLY_PACKET_ID, Side.CLIENT);
	}

	public void LoadNEI() {}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private Map<PacketType, List<Position>> positionListMap = new HashMap();

	/**return true if newly marked*/
	public boolean markForTileUpdate(Position position, PacketType type) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER){
			if(positionListMap.get(type) != null){
				List<Position> positionList = positionListMap.get(type);
				for (Position tmp : positionList){
					if(tmp != null && tmp.equals(position)){
						//System.out.println("already marked update");
						return false;
					}
				}
				positionList.add(position);
			}else{
				List<Position> positionList = new ArrayList();
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
				List<Position> positionList = positionListMap.get(type);
				if(positionList != null){
					for (Position position : positionList){
						TileEntity tile = MinecraftServer.getServer().getEntityWorld().getTileEntity(position.x, position.y, position.z);
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
		packetDispatcher.sendToAll(packet);
	}

	protected void spawnParticle(World world, int id, Object... objects) {}

}
