package com.okina.item;

import static com.okina.main.TestCore.*;

import com.okina.main.TestCore;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemWrench extends Item implements IToolWrench {

	public ItemWrench() {
		//TODO
		//		this.setTextureName(TestCore.MODID + ":hummer");
		setUnlocalizedName(AUTHER + ".wrench");
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		Block block = world.getBlockState(pos).getBlock();
		if(block != null && !player.isSneaking() && block.rotateBlock(world, pos, side)){
			player.swingItem();
			world.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, block.stepSound.getBreakSound(), 1.0F, 0.8F);
			return !world.isRemote;
		}
		return false;
	}

	//	@Override
	//	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
	//		if(world.getTileEntity(x, y, z) instanceof ConstructBaseTileEntity){
	//			ConstructBaseTileEntity tile = (ConstructBaseTileEntity) world.getTileEntity(x, y, z);
	//			tile.onRightClickedByWrench(player, side, hitX, hitY, hitZ);
	//			//spawnParticle(world, x, y, z, side);
	//		}else if(world.getTileEntity(x, y, z) instanceof BlockFrameTileEntity){
	//			BlockFrameTileEntity frame = (BlockFrameTileEntity) world.getTileEntity(x, y, z);
	//			frame.tryConstruct();
	//		}
	//		return true;
	//	}

	@Override
	public boolean doesSneakBypassUse(World world, BlockPos pos, EntityPlayer player) {
		return true;
	}

	@Override
	public void wrenchUsed(EntityPlayer player, BlockPos pos) {

	}
	@Override
	public boolean canWrench(EntityPlayer player, Entity entity) {
		return true;
	}
	@Override
	public boolean canWrench(EntityPlayer player, BlockPos pos) {
		return true;
	}
	@Override
	public void wrenchUsed(EntityPlayer player, Entity entity) {

	}

}
