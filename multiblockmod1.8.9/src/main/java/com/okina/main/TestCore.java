package com.okina.main;

import com.okina.creativetab.TestCreativeTab;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = TestCore.MODID, name = TestCore.NAME, version = TestCore.VERSION, dependencies = "after:ThermalExpansion")
public class TestCore {

	public static final String MODID = "MultiBlockMod";
	public static final String NAME = "Multi Block Mod";
	public static final String VERSION = "0.1";

	public static final String AUTHER = "okina";

	@Mod.Instance(MODID)
	public static TestCore instance;
	@SidedProxy(clientSide = "com.okina.main.ClientProxy", serverSide = "com.okina.main.CommonProxy")
	public static CommonProxy proxy;

	// configuration
	public static boolean isHoge;
	public static int particleLevel;

	// block instance
	public static Block pipe;

	public static Block[] baseFrame;

	public static Block[] constructFurnace;
	public static Block[] constructStorage;
	public static Block[] constructInterface;
	public static Block[] constructClockPulser;
	public static Block[] constructRepeater;
	public static Block[] constructContainer;
	public static Block[] constructCrusher;
	public static Block[] constructEnergyProvider;
	public static Block[] constructVirtualGrower;
	public static Block[] constructEventCatcher;
	public static Block[] constructDispatcher;
	public static Block blockFrame;
	public static Block blockFrameLine;
	public static Block multiBlockCore;
	public static Block multiBlockCasing;
	public static Block disassemblyTable;

	// item instance
	public static Item wrench;
	public static Item connector;
	public static Item filter;
	public static Item handyEmitter;
	public static Item craftingFilter;
	public static Item itemMultiBlock;
	/** crusher, grower, energy, furnace */
	public static Item[] filtering;
	public static Item toilet;
	public static Item doubleDustIron;
	public static Item doubleDustGold;
	public static Item greenPowder;
	public static Item greenMatter;
	public static Item organicMatter;
	public static Item burntOrganicMatter;

	// ore
	// public static List<Integer> oreIds = new ArrayList<Integer>();
	// public static List<Integer> dustIds = new ArrayList<Integer>();
	// public static List<Integer> doubleDustIds = new ArrayList<Integer>();
	// public static List<Integer> ingotIds = new ArrayList<Integer>();
	// public static List<Item> dustDouble = new ArrayList<Item>();
	// public static final String[] oreNames = { "Iron", "Gold", "Copper",
	// "Tin", "Silver", "Lead" };
	// public static int[] blockIds = new int[oreNames.length];

	// creative tab
	public static final CreativeTabs testCreativeTab = new TestCreativeTab("testCreativeTab");

	// GUI ID
	public static final int ITEM_GUI_ID = 0;
	public static final int BLOCK_GUI_ID_0 = 1;
	public static final int BLOCK_GUI_ID_1 = 2;
	public static final int BLOCK_GUI_ID_2 = 3;
	public static final int BLOCK_GUI_ID_3 = 4;
	public static final int BLOCK_GUI_ID_4 = 5;
	public static final int BLOCK_GUI_ID_5 = 6;

	// packet
	public static SimpleNetworkWrapper packetDispatcher;
	public static final int SIMPLETILE_PACKET_ID = 0;
	public static final int SIMPLETILE_REPLY_PACKET_ID = 1;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.loadConfiguration(event.getSuggestedConfigurationFile());
		proxy.registerBlock();
		proxy.registerItem();
		proxy.registerModel();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerTileEntity();
		proxy.registerRenderer();
		packetDispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
		proxy.registerPacket();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		FMLCommonHandler.instance().bus().register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		//		proxy.registerRecipe();
		//		IntegrationHandler.registerOre();
		//		IntegrationHandler.registerRecipe();
		//		proxy.LoadNEI();
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final int PARTICLE_GROWER = 1;
	public static final int PARTICLE_ENERGY = 2;
	public static final int PARTICLE_BEZIER = 3;
	public static final int PARTICLE_BEZIER_DOT = 4;
	public static final int PARTICLE_DOT = 5;
	public static final int PARTICLE_CRUCK = 6;

	public static void spawnParticle(World world, int id, Object... objects) {
		if(particleLevel == 0) return;
		if(world.getTotalWorldTime() % (4 - particleLevel) == 0){
			proxy.spawnParticle(world, id, objects);
		}
	}

}
