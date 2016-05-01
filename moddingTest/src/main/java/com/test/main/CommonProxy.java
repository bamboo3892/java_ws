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
import com.test.item.ItemHandySignalEmitter;
import com.test.item.ItemWrench;
import com.test.item.itemBlock.ItemBlockWithMeta;
import com.test.item.itemBlock.ItemMultiBlock;
import com.test.multiblock.BlockBaseFrame;
import com.test.multiblock.BlockDesignTable;
import com.test.multiblock.BlockDesignTableTileEntity;
import com.test.multiblock.BlockFrame;
import com.test.multiblock.BlockFrameLine;
import com.test.multiblock.BlockFrameTileEntity;
import com.test.multiblock.BlockPipe;
import com.test.multiblock.BlockPipeTileEntity;
import com.test.multiblock.MultiBlock;
import com.test.multiblock.MultiBlockTileEntity;
import com.test.multiblock.construct.block.ConstructClockPulser;
import com.test.multiblock.construct.block.ConstructContainer;
import com.test.multiblock.construct.block.ConstructCrusher;
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
import com.test.multiblock.construct.parts.EnergyProviderPart;
import com.test.multiblock.construct.parts.EventCatcherPart;
import com.test.multiblock.construct.parts.FurnacePart;
import com.test.multiblock.construct.parts.InterfacePart;
import com.test.multiblock.construct.parts.RepeaterPart;
import com.test.multiblock.construct.parts.StoragePart;
import com.test.multiblock.construct.parts.VirtualGlowerPart;
import com.test.multiblock.construct.tileentity.ConstructClockPulserTileEntity;
import com.test.multiblock.construct.tileentity.ConstructContainerTileEntity;
import com.test.multiblock.construct.tileentity.ConstructCrusherTileEntity;
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
		}catch (Exception e){
			FMLLog.severe("Error Message");
		}finally{
			config.save();
		}
	}

	public void registerBlock() {
		/*
		testBlock = new TestBlock();
		GameRegistry.registerBlock(testBlock, "testBlock");
		tileTestBlock = new TileTestBlock();
		GameRegistry.registerBlock(tileTestBlock, "tileTestBlock");
		*/
		pipe = new BlockPipe();
		GameRegistry.registerBlock(pipe, ItemBlockWithMeta.class, "pipe");
		designTable = new BlockDesignTable();
		GameRegistry.registerBlock(designTable, "designTable");

		baseFrame = new BlockBaseFrame[5];
		constructFurnace = new ConstructFurnace[5];
		constructStorage = new ConstructStorage[5];
		constructInterface = new ConstructInterface[5];
		constructClockPulser = new ConstructClockPulser[5];
		constructRepeater = new ConstructRepeater[5];
		constructContainer = new ConstructContainer[5];
		constructCrusher = new ConstructCrusher[5];
		costructEnergyProvider = new ConstructEnergyProvider[5];
		constructVirtualGlower = new ConstructVirtualGlower[5];
		constructEventCatcher = new ConstructEventCatcher[5];

		for (int i = 0; i < 5; i++){
			baseFrame[i] = new BlockBaseFrame(i);
			GameRegistry.registerBlock(baseFrame[i], "baseFrame." + i);
			constructFurnace[i] = new ConstructFurnace(i);
			GameRegistry.registerBlock(constructFurnace[i], "furnace." + i);
			constructStorage[i] = new ConstructStorage(i);
			GameRegistry.registerBlock(constructStorage[i], "storage." + i);
			constructInterface[i] = new ConstructInterface(i);
			GameRegistry.registerBlock(constructInterface[i], "interface." + i);
			constructClockPulser[i] = new ConstructClockPulser(i);
			GameRegistry.registerBlock(constructClockPulser[i], "clockPulser." + i);
			constructRepeater[i] = new ConstructRepeater(i);
			GameRegistry.registerBlock(constructRepeater[i], "repeater." + i);
			constructContainer[i] = new ConstructContainer(i);
			GameRegistry.registerBlock(constructContainer[i], "container." + i);
			constructCrusher[i] = new ConstructCrusher(i);
			GameRegistry.registerBlock(constructCrusher[i], "crusher." + i);
			costructEnergyProvider[i] = new ConstructEnergyProvider(i);
			GameRegistry.registerBlock(costructEnergyProvider[i], "energyProvider." + i);
			constructVirtualGlower[i] = new ConstructVirtualGlower(i);
			GameRegistry.registerBlock(constructVirtualGlower[i], "virtualGlower." + i);
			constructEventCatcher[i] = new ConstructEventCatcher(i);
			GameRegistry.registerBlock(constructEventCatcher[i], "eventCatcher." + i);
		}

		blockFrame = new BlockFrame();
		GameRegistry.registerBlock(blockFrame, "blockFrame");
		blockFrameLine = new BlockFrameLine();
		GameRegistry.registerBlock(blockFrameLine, "blockFrameLine");
		multiBlock = new MultiBlock();
		GameRegistry.registerBlock(multiBlock, ItemMultiBlock.class, "multiBlock");
		//GameRegistry.registerCustomItemStack(string, itemStack);
	}

	public void registerItem() {
		/*
		testItem = new TestItem();
		GameRegistry.registerItem(testItem, "testItem");
		sightStealer = new SightStealer();
		GameRegistry.registerItem(sightStealer, "sightStealer");
		*/
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
		itemMultiBlock = new ItemMultiBlock(multiBlock);

	}

	public void registerTileEntity() {
		GameRegistry.registerTileEntity(TileTestBlockTileEntity.class, "tileTestBlockTileEntity");
		GameRegistry.registerTileEntity(BlockPipeTileEntity.class, "blockPipeTileEntity");
		GameRegistry.registerTileEntity(BlockDesignTableTileEntity.class, "designTableTileEntity");
		GameRegistry.registerTileEntity(BlockFrameTileEntity.class, "blockFrameTileEntity");
		GameRegistry.registerTileEntity(MultiBlockTileEntity.class, "multiBlockTileEntity");
		//construct parts
		GameRegistry.registerTileEntity(ConstructFurnaceTileEntity.class, "constructFurnaceTileEntity");
		ConstructPartRegistry.registerConstructPart(new FurnacePart());
		GameRegistry.registerTileEntity(ConstructStorageTileEntity.class, "constructStorageTileEntity");
		ConstructPartRegistry.registerConstructPart(new StoragePart());
		GameRegistry.registerTileEntity(ConstructInterfaceTileEntity.class, "constructInterfaceTileEntity");
		ConstructPartRegistry.registerConstructPart(new InterfacePart());
		GameRegistry.registerTileEntity(ConstructClockPulserTileEntity.class, "constructClockPulserTileEntity");
		ConstructPartRegistry.registerConstructPart(new ClockPulserPart());
		GameRegistry.registerTileEntity(ConstructRepeaterTileEntity.class, "constructRepeaterTileEntity");
		ConstructPartRegistry.registerConstructPart(new RepeaterPart());
		GameRegistry.registerTileEntity(ConstructContainerTileEntity.class, "constructContainerTileEntity");
		ConstructPartRegistry.registerConstructPart(new ContainerPart());
		GameRegistry.registerTileEntity(ConstructCrusherTileEntity.class, "constructCrusherTileEntity");
		ConstructPartRegistry.registerConstructPart(new CrusherPart());
		GameRegistry.registerTileEntity(ConstructEnergyProviderTileEntity.class, "constructEnergyProviderTileEntity");
		ConstructPartRegistry.registerConstructPart(new EnergyProviderPart());
		GameRegistry.registerTileEntity(ConstructVirtualGrowerTileEntity.class, "constructVirtualGlowerTileEntity");
		ConstructPartRegistry.registerConstructPart(new VirtualGlowerPart());
		GameRegistry.registerTileEntity(ConstructEventCatcherTileEntity.class, "constructEventCatcherTileEntity");
		ConstructPartRegistry.registerConstructPart(new EventCatcherPart());
	}

	public void registerRecipe() {
		//crafting
		GameRegistry.addRecipe(new ItemStack(pipe, 8, 0), new Object[] { "XRX", 'X', Blocks.fence, 'R', Blocks.redstone_block });
		GameRegistry.addRecipe(new ItemStack(designTable, 1, 0), new Object[] { "XRX", 'X', Blocks.fence, 'R', Blocks.crafting_table });

		GameRegistry.addRecipe(new ItemStack(baseFrame[0], 1), new Object[] { "XXX", "X X", "XXX", 'X', Blocks.fence });
		for (int i = 0; i < 5; i++){
			GameRegistry.addRecipe(new ItemStack(constructFurnace[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.furnace, 'S', Blocks.stone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructStorage[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.chest, 'S', Blocks.fence, 'C', Blocks.planks });
			GameRegistry.addRecipe(new ItemStack(constructInterface[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.obsidian, 'S', Blocks.wool, 'C', Items.string });
			GameRegistry.addRecipe(new ItemStack(constructClockPulser[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.redstone_block, 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructRepeater[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.quartz_block, 'S', Items.redstone, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(constructContainer[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.glass, 'S', Blocks.glass_pane, 'C', Blocks.glass_pane });
			GameRegistry.addRecipe(new ItemStack(constructCrusher[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.cactus, 'S', Items.stone_pickaxe, 'C', Blocks.cobblestone });
			GameRegistry.addRecipe(new ItemStack(costructEnergyProvider[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Items.ender_pearl, 'S', Items.enchanted_book, 'C', Blocks.nether_brick });
			GameRegistry.addRecipe(new ItemStack(constructVirtualGlower[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Blocks.grass, 'S', Blocks.leaves, 'C', Items.flower_pot });
			GameRegistry.addRecipe(new ItemStack(constructEventCatcher[i], 1), new Object[] { "SBS", "CFC", 'B', baseFrame[i], 'F', Items.comparator, 'S', Items.redstone, 'C', Blocks.cobblestone });
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
			GameRegistry.addShapelessRecipe(new ItemStack(costructEnergyProvider[i + 1], 1), new Object[] { costructEnergyProvider[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructVirtualGlower[i + 1], 1), new Object[] { constructVirtualGlower[i], material[i] });
			GameRegistry.addShapelessRecipe(new ItemStack(constructEventCatcher[i + 1], 1), new Object[] { constructEventCatcher[i], material[i] });
		}
		GameRegistry.addRecipe(new ItemStack(wrench, 1), new Object[] { "SBS", " B ", " B ", 'B', Items.reeds, 'S', Items.iron_ingot });
		GameRegistry.addRecipe(new ItemStack(connector, 1), new Object[] { " S ", "D  ", 'S', Blocks.sapling, 'D', Items.stick });
		GameRegistry.addRecipe(new ItemStack(handyEmitter, 1), new Object[] { " S ", "D  ", 'S', Items.redstone, 'D', Blocks.sapling });
		GameRegistry.addRecipe(new ItemStack(craftingFilter, 2, 0), new Object[] { "XRX", 'X', Items.stick, 'R', Blocks.crafting_table });
		GameRegistry.addShapelessRecipe(new ItemStack(filter, 1), new Object[] { Blocks.trapdoor });

		//smelting

		//crusher
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.cobblestone, 1), new ItemStack(Blocks.sand, 1));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Blocks.stone, 1), new ItemStack(Blocks.gravel, 1));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Items.bone, 1), new ItemStack(Items.dye, 6, 15));
		CrusherRecipeRegister.instance.registerRecipe(new ItemStack(Items.blaze_rod, 1), new ItemStack(Items.blaze_powder, 4));
	}

	public void registerRenderer() {

	}

	public void registerPacket() {
		packetDispatcher.registerMessage(SimpleTilePacketHandler.class, SimpleTilePacket.class, SIMPLETILE_PACKET_ID, Side.SERVER);
		packetDispatcher.registerMessage(SimpleTileReplyPacketHandler.class, SimpleTilePacket.class, SIMPLETILE_REPLY_PACKET_ID, Side.CLIENT);
	}

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

	public void sendPacket() {
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

	public void spawnParticle(World world, int id, double posX, double posY, double posZ, double vecX, double vecY, double vecZ) {}

}
