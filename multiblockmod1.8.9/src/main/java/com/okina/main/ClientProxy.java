package com.okina.main;

import static com.okina.main.TestCore.*;

import com.okina.client.model.SampleModelLoader;
import com.okina.client.particle.ParticleBezierCurve;
import com.okina.client.particle.ParticleBezierDots;
import com.okina.client.particle.ParticleCruck;
import com.okina.client.particle.ParticleDot;
import com.okina.client.particle.ParticleEnergyProvide;
import com.okina.client.particle.ParticleGrower;
import com.okina.client.renderer.TileConstructBaseRenderer;
import com.okina.client.renderer.TileFrameLaserRenderer;
import com.okina.client.renderer.TileMultiBlockRenderer;
import com.okina.multiblock.BlockFrameTileEntity;
import com.okina.multiblock.BlockPipe;
import com.okina.multiblock.MultiBlockCoreTileEntity;
import com.okina.multiblock.construct.block.ConstructCrusher;
import com.okina.multiblock.construct.block.ConstructFurnace;
import com.okina.multiblock.construct.tileentity.ConstructBaseTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(pipe), 0, new ModelResourceLocation(pipe.getRegistryName(), null));
		ModelLoader.setCustomStateMapper(pipe, (new StateMap.Builder()).ignore(BlockPipe.COLOR).build());
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockFrame), 0, new ModelResourceLocation(blockFrame.getRegistryName(), null));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockFrameLine), 0, new ModelResourceLocation(blockFrameLine.getRegistryName(), null));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(multiBlockCore), 0, new ModelResourceLocation(multiBlockCore.getRegistryName(), null));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(multiBlockCasing), 0, new ModelResourceLocation(multiBlockCasing.getRegistryName(), null));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(disassemblyTable), 0, new ModelResourceLocation(disassemblyTable.getRegistryName(), null));
		for (int i = 0; i < 5; i++){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(baseFrame[i]), 0, new ModelResourceLocation(baseFrame[i].getRegistryName(), null));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(constructStorage[i]), 0, new ModelResourceLocation(constructStorage[i].getRegistryName(), null));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(constructClockPulser[i]), 0, new ModelResourceLocation(constructClockPulser[i].getRegistryName(), null));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(constructRepeater[i]), 0, new ModelResourceLocation(constructRepeater[i].getRegistryName(), null));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(constructEventCatcher[i]), 0, new ModelResourceLocation(constructEventCatcher[i].getRegistryName(), null));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(constructContainer[i]), 0, new ModelResourceLocation(constructContainer[i].getRegistryName(), null));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(constructFurnace[i]), 0, new ModelResourceLocation(constructFurnace[i].getRegistryName(), null));
			ModelLoader.setCustomStateMapper(constructFurnace[i], (new StateMap.Builder()).ignore(ConstructFurnace.DIRECTION).build());
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(constructVirtualGrower[i]), 0, new ModelResourceLocation(constructVirtualGrower[i].getRegistryName(), null));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(constructEnergyProvider[i]), 0, new ModelResourceLocation(constructEnergyProvider[i].getRegistryName(), null));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(constructCrusher[i]), 0, new ModelResourceLocation(constructCrusher[i].getRegistryName(), null));
			ModelLoader.setCustomStateMapper(constructCrusher[i], (new StateMap.Builder()).ignore(ConstructCrusher.DIRECTION).build());
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(constructDispatcher[i]), 0, new ModelResourceLocation(constructDispatcher[i].getRegistryName(), null));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(constructInterface[i]), 0, new ModelResourceLocation(constructInterface[i].getRegistryName(), null));
		}

		ModelLoader.setCustomModelResourceLocation(wrench, 0, new ModelResourceLocation(wrench.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(connector, 0, new ModelResourceLocation(connector.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(handyEmitter, 0, new ModelResourceLocation(handyEmitter.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(filter, 0, new ModelResourceLocation(filter.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(filtering[0], 0, new ModelResourceLocation(filtering[0].getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(filtering[1], 0, new ModelResourceLocation(filtering[1].getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(filtering[2], 0, new ModelResourceLocation(filtering[2].getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(filtering[3], 0, new ModelResourceLocation(filtering[3].getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(craftingFilter, 0, new ModelResourceLocation(craftingFilter.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(toilet, 0, new ModelResourceLocation(toilet.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(doubleDustIron, 0, new ModelResourceLocation(doubleDustIron.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(doubleDustGold, 0, new ModelResourceLocation(doubleDustGold.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(greenPowder, 0, new ModelResourceLocation(greenPowder.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(greenMatter, 0, new ModelResourceLocation(greenMatter.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(organicMatter, 0, new ModelResourceLocation(organicMatter.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(burntOrganicMatter, 0, new ModelResourceLocation(burntOrganicMatter.getRegistryName(), "inventory"));

		ModelLoaderRegistry.registerLoader(new SampleModelLoader());
	}

	@Override
	public void registerRenderer() {
		ClientRegistry.bindTileEntitySpecialRenderer(BlockFrameTileEntity.class, new TileFrameLaserRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ConstructBaseTileEntity.class, new TileConstructBaseRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(MultiBlockCoreTileEntity.class, new TileMultiBlockRenderer());
	}

	@Override
	public void LoadNEI() {
		if(Loader.isModLoaded("NotEnoughItems")){
			//			LoadNEI.loadNEI();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
