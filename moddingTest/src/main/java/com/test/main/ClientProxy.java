package com.test.main;

import com.test.client.particle.ParticleTest1;
import com.test.client.particle.ParticleTest2;
import com.test.client.renderer.BlockEnergyProviderRenderer;
import com.test.client.renderer.BlockFrameLineRenderer;
import com.test.client.renderer.BlockFrameRenderer;
import com.test.client.renderer.BlockPipeRenderer;
import com.test.client.renderer.ConstructBaseRenderer;
import com.test.client.renderer.ConstructContainerRenderer;
import com.test.client.renderer.MultiBlockRenderer;
import com.test.client.renderer.TestBlockRenderer;
import com.test.client.renderer.TileContainerRenderer;
import com.test.client.renderer.TileCrusherRenderer;
import com.test.client.renderer.TileEnergyProviderRenderer;
import com.test.client.renderer.TileFrameLaserRenderer;
import com.test.multiblock.BlockFrameTileEntity;
import com.test.multiblock.construct.tileentity.ConstructContainerTileEntity;
import com.test.multiblock.construct.tileentity.ConstructCrusherTileEntity;
import com.test.multiblock.construct.tileentity.ConstructEnergyProviderTileEntity;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderer() {
		RenderingRegistry.registerBlockHandler(new TestBlockRenderer());
		RenderingRegistry.registerBlockHandler(new BlockPipeRenderer());
		RenderingRegistry.registerBlockHandler(new ConstructBaseRenderer());
		RenderingRegistry.registerBlockHandler(new ConstructContainerRenderer());
		RenderingRegistry.registerBlockHandler(new BlockEnergyProviderRenderer());
		RenderingRegistry.registerBlockHandler(new BlockFrameRenderer());
		RenderingRegistry.registerBlockHandler(new BlockFrameLineRenderer());
		RenderingRegistry.registerBlockHandler(new MultiBlockRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BlockFrameTileEntity.class, new TileFrameLaserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ConstructContainerTileEntity.class, new TileContainerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ConstructCrusherTileEntity.class, new TileCrusherRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ConstructEnergyProviderTileEntity.class, new TileEnergyProviderRenderer());
		//MinecraftForgeClient.registerItemRenderer(itemMultiBlock, new ItemMultiBlockRenderer());
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void spawnParticle(World world, int id, double posX, double posY, double posZ, double vecX, double vecY, double vecZ) {
		switch(id){
		case 0 :
			Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTest1(world, posX, posY, posZ, vecX, vecY, vecZ));
			break;
		case 1 :
			Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleTest2(world, posX, posY, posZ, vecX, vecY, vecZ));
			break;
		case 2 :
			ParticleTest2 particle = new ParticleTest2(world, posX, posY, posZ, vecX, vecY, vecZ);
			particle.set1();
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			break;
		}
	}

}