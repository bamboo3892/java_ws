package com.test.client.renderer;

import com.test.main.TestCore;
import com.test.multiblock.BlockBaseFrame;
import com.test.multiblock.construct.block.ConstructFunctionalBase;
import com.test.multiblock.construct.tileentity.ConstructContainerTileEntity;
import com.test.utils.RenderingHelper;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class ConstructContainerRenderer implements ISimpleBlockRenderingHandler {

	public ConstructContainerRenderer() {

	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		RenderingHelper.renderInvCuboid(renderer, block, 2F / 16F, 2F / 16F, 2F / 16F, 14F / 16F, 14F / 16F, 14F / 16F, 4);
		int grade = ((ConstructFunctionalBase)block).grade;
		renderer.setOverrideBlockTexture(BlockBaseFrame.icons[grade]);
		RenderingHelper.renderInvCubeFrame(renderer, block, 0F / 16F, 0F / 16F, 0F / 16F, 16F / 16F, 16F / 16F, 16F / 16F, 1F / 16F);
		renderer.setOverrideBlockTexture(ConstructFunctionalBase.iconPane);
		RenderingHelper.renderInvCuboid(renderer, block,1.0001F / 16F, 1.0001F / 16F, 1.0001F / 16F, 14.9999F / 16F, 14.9999F / 16F, 14.9999F / 16F);
		renderer.clearOverrideBlockTexture();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderer.setOverrideBlockTexture(ConstructFunctionalBase.iconPane);
		renderer.setRenderBounds(1.0001F / 16F, 1.0001F / 16F, 1.0001F / 16F, 14.9999F / 16F, 14.9999F / 16F, 14.9999F / 16F);
		renderer.renderStandardBlock(block, x, y, z);

		if(world.getTileEntity(x, y, z) instanceof ConstructContainerTileEntity){
			ConstructContainerTileEntity tile = ((ConstructContainerTileEntity) world.getTileEntity(x, y, z));

			int grade = ((ConstructFunctionalBase)block).grade;
			boolean[] connection = tile.isNeighberBaseBlock;
			renderer.setOverrideBlockTexture(BlockBaseFrame.icons[grade]);
			RenderingHelper.renderConnectedCubeFrame(connection, x, y, z, block, 1F / 16F, renderer);

			//connection box
			if(tile.mode == 1){
				int dir = tile.connectDirection;
				if(dir != -1){
					renderer.setOverrideBlockTexture(Blocks.planks.getBlockTextureFromSide(0));
					if(dir == 0){
						RenderingHelper.renderCubeFrame(x, y, z, block, 1F / 16F, -15F / 16F, 1F / 16F, 14F / 16F, 46F / 16F, 14F / 16F, 1F / 16F, renderer);
					}else if(dir == 2){
						RenderingHelper.renderCubeFrame(x, y, z, block, 1F / 16F, 1F / 16F, -15F / 16F, 14F / 16F, 14F / 16F, 46F / 16F, 1F / 16F, renderer);
					}else if(dir == 4){
						RenderingHelper.renderCubeFrame(x, y, z, block, -15F / 16F, 1F / 16F, 1F / 16F, 46F / 16F, 14F / 16F, 14F / 16F, 1F / 16F, renderer);
					}
				}
			}else if(tile.mode == 2){
				renderer.setOverrideBlockTexture(Blocks.planks.getBlockTextureFromSide(0));
				RenderingHelper.renderCubeFrame(x, y, z, block, 1F / 16F, -15F / 16F, 1F / 16F, 14F / 16F, 30F / 16F, 14F / 16F, 1F / 16F, renderer);
			}

			//io
			int[] flagIO = tile.flagIO;
			if(flagIO != null){
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
		renderer.clearOverrideBlockTexture();
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return TestCore.CONTAINER_RENDER_ID;
	}

}
