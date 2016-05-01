package com.test.main;

import static com.test.main.TestCore.*;

import com.test.client.particle.ParticleBezierCurve;
import com.test.client.particle.ParticleBezierDots;
import com.test.client.particle.ParticleCruck;
import com.test.client.particle.ParticleDot;
import com.test.client.particle.ParticleEnergyProvide;
import com.test.client.particle.ParticleGrower;
import com.test.client.renderer.BlockEnergyProviderRenderer;
import com.test.client.renderer.BlockFrameLineRenderer;
import com.test.client.renderer.BlockFrameRenderer;
import com.test.client.renderer.BlockPipeRenderer;
import com.test.client.renderer.ConstructBaseRenderer;
import com.test.client.renderer.ConstructContainerRenderer;
import com.test.client.renderer.MultiBlockRenderer;
import com.test.client.renderer.TileConstructBaseRenderer;
import com.test.client.renderer.TileFrameLaserRenderer;
import com.test.client.renderer.TileMultiBlockRenderer;
import com.test.multiblock.BlockFrameTileEntity;
import com.test.multiblock.MultiBlockCoreTileEntity;
import com.test.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.test.nei.GuiDummy;
import com.test.nei.SampleRecipeHandler;
import com.test.network.SimpleTilePacket;

import codechicken.nei.api.API;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Loader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderer() {
		RenderingRegistry.registerBlockHandler(new BlockPipeRenderer());
		RenderingRegistry.registerBlockHandler(new ConstructBaseRenderer());
		RenderingRegistry.registerBlockHandler(new ConstructContainerRenderer());
		RenderingRegistry.registerBlockHandler(new BlockEnergyProviderRenderer());
		RenderingRegistry.registerBlockHandler(new BlockFrameRenderer());
		RenderingRegistry.registerBlockHandler(new BlockFrameLineRenderer());
		RenderingRegistry.registerBlockHandler(new MultiBlockRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BlockFrameTileEntity.class, new TileFrameLaserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ConstructBaseTileEntity.class, new TileConstructBaseRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MultiBlockCoreTileEntity.class, new TileMultiBlockRenderer());
		//MinecraftForgeClient.registerItemRenderer(itemMultiBlock, new ItemMultiBlockRenderer());
	}

	public static SampleRecipeHandler catchRecipe;

	@Override
	public void LoadNEI() {
		if(Loader.isModLoaded("NotEnoughItems")){
			try{
				catchRecipe = new SampleRecipeHandler();
				API.registerRecipeHandler(catchRecipe);
				API.registerUsageHandler(catchRecipe);
				API.registerGuiOverlay(GuiDummy.class, catchRecipe.getOverlayIdentifier(), 0, 0);
				//LoadNEIConfig.load();
			}catch (Exception e){
				e.printStackTrace(System.err);
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void sendPacket(SimpleTilePacket packet){
		packetDispatcher.sendToServer(packet);
	}

	@Override
	protected void spawnParticle(World world, int id, Object... objects) {
		try{
			switch (id) {
			case TestCore.PARTICLE_GROWER:
				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleGrower(world, objects));
				break;
			case TestCore.PARTICLE_ENERGY:
				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleEnergyProvide(world, objects).set1());
				break;
			case TestCore.PARTICLE_BEZIER:
				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBezierCurve(world, objects));
				break;
			case TestCore.PARTICLE_BEZIER_DOT:
				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleBezierDots(world, objects));
				break;
			case TestCore.PARTICLE_DOT:
				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleDot(world, objects));
				break;
			case TestCore.PARTICLE_CRUCK:
				Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleCruck(world, objects));
				break;
			}
		}catch (Exception e){
			System.err.println("Illegal parameter");
			e.printStackTrace();
		}
	}

}