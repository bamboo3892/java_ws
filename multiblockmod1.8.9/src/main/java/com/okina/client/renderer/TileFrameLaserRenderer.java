package com.okina.client.renderer;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileFrameLaserRenderer extends TileEntitySpecialRenderer {

	public TileFrameLaserRenderer() {

	}

	@Override
	public void renderTileEntityAt(TileEntity pTile, double x, double y, double z, float partialTicks, int destroyStage) {
		//		if(pTile instanceof BlockFrameTileEntity){
		//			BlockFrameTileEntity tile = (BlockFrameTileEntity)pTile;
		//			for(ForgeDirection dir : ForgeDirection.values()){
		//				if(dir.ordinal() >= 6) break;
		//				if(tile.length[dir.ordinal()] > 0){
		//					RenderingHelper.renderLaser(tile, x, y, z, tile.length[dir.ordinal()], dir, 0, 255, 0, 120, partialTicks);
		//				}else if(tile.length[dir.ordinal()] == 0){
		//					RenderingHelper.renderLaser(tile, x, y, z, 15, dir,255, 0, 0, 50, partialTicks);
		//				}
		//			}
		//		}
	}

}
