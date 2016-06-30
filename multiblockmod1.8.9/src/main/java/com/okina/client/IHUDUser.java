package com.okina.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;

/**implements this interface on TileEntity, Block or Item*/
public interface IHUDUser {

	/**max renderTicks is 1 hour*/
	public void renderHUD(Minecraft sr, double renderTicks, MovingObjectPosition mop);

	public boolean comparePastRenderObj(Object object, MovingObjectPosition past, MovingObjectPosition current);

}
