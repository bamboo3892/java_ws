package com.okina.item;

import static com.okina.main.TestCore.*;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.okina.multiblock.construct.tileentity.ILinkConnectionUser;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemConnector extends Item {

	public ItemConnector() {
		//TODO
		//		this.setTextureName(TestCore.MODID + ":connector");
		setUnlocalizedName(AUTHER + ".connector");
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		NBTTagCompound tag = itemStack.getTagCompound();
		if(tag == null){
			tag = new NBTTagCompound();
		}
		boolean prev = tag.getBoolean("connected");
		if(prev){
			int prevX = tag.getInteger("x");
			int prevY = tag.getInteger("y");
			int prevZ = tag.getInteger("z");
			BlockPos newPos = new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
			int prevSide = tag.getInteger("side");
			if(world.getTileEntity(pos) instanceof ConstructBaseTileEntity){
				if(world.getTileEntity(newPos) instanceof ILinkConnectionUser){
					ConstructBaseTileEntity tile = (ConstructBaseTileEntity) world.getTileEntity(pos);
					ILinkConnectionUser prevTile = (ILinkConnectionUser) world.getTileEntity(newPos);
					if(prevTile.tryConnect(tile, side, EnumFacing.getFront(prevSide))){
						if(!world.isRemote) player.addChatMessage(new ChatComponentText("Connected"));
					}else{
						if(!world.isRemote) player.addChatMessage(new ChatComponentText("Illegal Block"));
					}
				}
			}
			tag.setBoolean("connected", false);
		}else{
			if(world.getTileEntity(pos) instanceof ILinkConnectionUser){
				if(((ILinkConnectionUser) world.getTileEntity(pos)).canStartAt(side)){
					tag.setInteger("x", pos.getX());
					tag.setInteger("y", pos.getY());
					tag.setInteger("z", pos.getZ());
					tag.setInteger("side", side.getIndex());
					tag.setBoolean("connected", true);
					itemStack.setTagCompound(tag);
					if(!world.isRemote) player.addChatMessage(new ChatComponentText("Start Point"));
				}
			}
		}
		return true;
	}

}
