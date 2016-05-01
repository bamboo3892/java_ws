package com.test.main;

import java.util.ArrayList;
import java.util.List;

import com.test.creativetab.TestCreativeTab;
import com.test.nei.NewRecipeRegister;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = TestCore.MODID, name = TestCore.NAME, version = TestCore.VERSION)
public class TestCore {

	public static final String MODID = "testmod";
	public static final String NAME = "Test Mod";
	public static final String VERSION = "0.0.0";

	@Mod.Instance(MODID)
	public static TestCore instance;
	@SidedProxy(clientSide = "com.test.main.ClientProxy", serverSide = "com.test.main.CommonProxy")
	public static CommonProxy proxy;

	//configuration
	public static boolean isHoge;
	public static int particleLevel;

	//block instance
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

	//item instance
	public static Item testItem;
	public static Item sightStealer;
	public static Item wrench;
	public static Item connector;
	public static Item filter;
	public static Item handyEmitter;
	public static Item craftingFilter;
	public static Item itemMultiBlock;
	/**crusher, grower, energy, furnace*/
	public static Item[] filtering;

	//ore
	public static List<Integer> oreIds = new ArrayList<Integer>();
	public static List<Integer> dustIds = new ArrayList<Integer>();
	public static List<Integer> doubleDustIds = new ArrayList<Integer>();
	public static List<Integer> ingotIds = new ArrayList<Integer>();
	public static List<Item> dustDouble = new ArrayList<Item>();

	public static final String[] oreNames = { "Iron", "Gold", "Copper", "Tin", "Silver", "Lead" };
	public static int[] blockIds = new int[oreNames.length];

	//creative tab
	public static final CreativeTabs testCreativeTab = new TestCreativeTab("testCreativeTab");

	//GUI ID
	public static final int ITEM_GUI_ID = 0;
	public static final int BLOCK_GUI_ID_0 = 1;
	public static final int BLOCK_GUI_ID_1 = 2;
	public static final int BLOCK_GUI_ID_2 = 3;
	public static final int BLOCK_GUI_ID_3 = 4;
	public static final int BLOCK_GUI_ID_4 = 5;
	public static final int BLOCK_GUI_ID_5 = 6;

	//render ID
	public static int TESTBLOCK_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int BLOCKPIPE_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int CONSTRUCTBASE_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int CONTAINER_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int ENERGYPROVIDER_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int BLOCKFRAME_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int BLOCKFRAMELINE_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();
	public static int MULTIBLOCK_RENDER_ID = RenderingRegistry.getNextAvailableRenderId();

	//packet
	public static SimpleNetworkWrapper packetDispatcher;
	public static final int SIMPLETILE_PACKET_ID = 0;
	public static final int SIMPLETILE_REPLY_PACKET_ID = 1;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.loadConfiguration(event.getSuggestedConfigurationFile());
		proxy.registerBlock();
		proxy.registerItem();
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
		proxy.registerRecipe();
		IntegrationHandler.registerOre();
		IntegrationHandler.registerRecipe();

		/*独自レシピの登録メソッドを呼び出しています。
		  表示させるだけのダミーレシピです。
		  実際に使用する場合は、ご自身で追加した独自クラフトのレシピのリストを作って下さい。*/

		(new NewRecipeRegister()).setRecipeList();

		/*NEIへの登録は、プロキシクラスを利用することでクライアントサイドのみで行います。*/

		proxy.LoadNEI();
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