package com.test.client.renderer;

import com.test.main.TestCore;
import com.test.utils.RenderingHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class MultiBlockRenderer implements ISimpleBlockRenderingHandler {

	public MultiBlockRenderer() {
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		RenderingHelper.renderCubeFrame(x, y, z, block, 0, 0, 0, 1, 1, 1, 3F/16F, renderer);
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return TestCore.MULTIBLOCK_RENDER_ID;
	}

}
