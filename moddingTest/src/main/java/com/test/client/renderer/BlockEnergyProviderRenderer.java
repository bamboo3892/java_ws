package com.test.client.renderer;

import com.test.main.TestCore;
import com.test.multiblock.BlockBaseFrame;
import com.test.multiblock.construct.block.ConstructEnergyProvider;
import com.test.multiblock.construct.block.ConstructFunctionalBase;
import com.test.multiblock.construct.tileentity.ConstructEnergyProviderTileEntity;
import com.test.utils.RenderingHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class BlockEnergyProviderRenderer implements ISimpleBlockRenderingHandler {

	public BlockEnergyProviderRenderer() {

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		RenderingHelper.renderInvCuboid(renderer, block, 2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F, 4);
		int grade = ((ConstructFunctionalBase) block).grade;
		renderer.setOverrideBlockTexture(BlockBaseFrame.icons[grade]);
		RenderingHelper.renderInvCubeFrame(renderer, block, 0F / 16F, 0F / 16F, 0F / 16F, 16F / 16F, 16F / 16F, 16F / 16F, 1F / 16F);
		renderer.setOverrideBlockTexture(ConstructFunctionalBase.iconPane);
		RenderingHelper.renderInvCuboid(renderer, block, 1.0001F / 16F, 1.0001F / 16F, 1.0001F / 16F, 14.9999F / 16F, 14.9999F / 16F, 14.9999F / 16F);
		renderer.clearOverrideBlockTexture();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderer.setRenderBounds(2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F);
		renderer.renderStandardBlock(block, x, y, z);

		if(world.getTileEntity(x, y, z) instanceof ConstructEnergyProviderTileEntity){
			ConstructEnergyProviderTileEntity tile = (ConstructEnergyProviderTileEntity) world.getTileEntity(x, y, z);

			int grade = ((ConstructFunctionalBase) block).grade;
			boolean[] connection = tile.isNeighberBaseBlock;
			renderer.setOverrideBlockTexture(BlockBaseFrame.icons[grade]);
			RenderingHelper.renderConnectedCubeFrame(connection, x, y, z, block, 1F / 16F, renderer);

			//energy show pane
			int energy = tile.getEvergyLevel();
			renderer.setOverrideBlockTexture(ConstructEnergyProvider.energyIcon[energy]);
			renderer.setRenderBounds(1.0001F / 16F, 1.0001F / 16F, 1.0001F / 16F, 14.9999F / 16F, 14.9999F / 16F, 14.9999F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
		}

		renderer.clearOverrideBlockTexture();
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return TestCore.ENERGYPROVIDER_RENDER_ID;
	}

}
