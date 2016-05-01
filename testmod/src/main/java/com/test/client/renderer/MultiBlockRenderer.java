package com.test.client.renderer;

import com.test.main.TestCore;
import com.test.multiblock.MultiBlockCasing;
import com.test.multiblock.MultiBlockCasingTileEntity;
import com.test.multiblock.MultiBlockCore;
import com.test.multiblock.MultiBlockCoreTileEntity;
import com.test.utils.RenderingHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class MultiBlockRenderer implements ISimpleBlockRenderingHandler {

	public MultiBlockRenderer() {}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		if(block instanceof MultiBlockCasing){
			RenderingHelper.renderInvCuboid(renderer, block, 1.0001F / 3F / 16F, 1.0001F / 3F / 16F, 1.0001F / 3F / 16F, (16F - 1.0001F / 3F) / 16F, (16F - 1.0001F / 3F) / 16F, (16F - 1.0001F / 3F) / 16F);
		}else if(block instanceof MultiBlockCore){
			RenderingHelper.renderInvCubeFrame(renderer, block, 0, 0, 0, 1, 1, 1, 3F / 16F);

		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if(block instanceof MultiBlockCasing){
			renderer.clearOverrideBlockTexture();
			renderer.setRenderBounds(1.0001F / 16F, 1.0001F / 16F, 1.0001F / 16F, 14.9999F / 16F, 14.9999F / 16F, 14.9999F / 16F);
			renderer.renderStandardBlock(block, x, y, z);
			if(world.getTileEntity(x, y, z) instanceof MultiBlockCasingTileEntity){
				MultiBlockCasingTileEntity tile = (MultiBlockCasingTileEntity) world.getTileEntity(x, y, z);
				if(tile.flagIO != null){
					int[] flagIO = tile.flagIO;
					if(flagIO[0] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[0] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 2F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
					if(flagIO[1] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[1] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(5F / 16F, 14F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
					if(flagIO[2] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[2] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 2F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
					if(flagIO[3] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[3] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(5F / 16F, 5F / 16F, 14F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
					if(flagIO[4] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[4] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(0F / 16F, 5F / 16F, 5F / 16F, 2F / 16F, 11F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
					if(flagIO[5] == 0){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 3));
						renderer.setRenderBounds(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}else if(flagIO[5] == 1){
						renderer.setOverrideBlockTexture(Blocks.wool.getIcon(0, 1));
						renderer.setRenderBounds(14F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
						renderer.renderStandardBlock(block, x, y, z);
					}
				}
			}
		}else if(world.getTileEntity(x, y, z) instanceof MultiBlockCoreTileEntity){
			//			MultiBlockCoreTileEntity tile = (MultiBlockCoreTileEntity) world.getTileEntity(x, y, z);
			//			if(tile.connected){
			//				RenderingHelper.renderCubeFrame(x, y, z, block, -1, -1, -1, 3, 3, 3, 3F / 16F, renderer);
			//			}else{
			//				RenderingHelper.renderCubeFrame(x, y, z, block, 0, 0, 0, 1, 1, 1, 3F / 16F, renderer);
			//			}
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
		return TestCore.MULTIBLOCK_RENDER_ID;
	}

}
