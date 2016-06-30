package com.okina.item.itemBlock;

import java.util.List;

import com.okina.client.IToolTipUser;
import com.okina.main.TestCore;
import com.okina.multiblock.MultiBlockCoreTileEntity;
import com.okina.utils.RectangularSolid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLLog;

public class ItemMultiBlock extends ItemBlockWithMeta implements IToolTipUser {

	public ItemMultiBlock(Block block) {
		super(block);
		setCreativeTab(TestCore.testCreativeTab);
		setMaxStackSize(1);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if(!world.setBlockState(pos, newState, 3)) return false;
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == block){
			//			setTileEntityNBT(world, player, pos, stack);
			//			this.block.onBlockPlacedBy(world, pos, state, player, stack);
			NBTTagCompound tag = stack.getTagCompound();
			if(tag == null){
				FMLLog.severe("Tag deleted itemstack", new Object[0]);
				tag = new NBTTagCompound();
			}
			MultiBlockCoreTileEntity tile = MultiBlockCoreTileEntity.createTileFromNBT(tag);
			world.setTileEntity(pos, tile);
			block.onBlockPlacedBy(world, pos, state, player, stack);
		}
		return true;
	}

	//	@Override
	//	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
	//		if(!world.setBlock(x, y, z, getDamage(stack), metadata, 3)){
	//			return false;
	//		}
	//		if(world.getBlock(x, y, z) == field_150939_a){
	//			NBTTagCompound tag = stack.getTagCompound();
	//			if(tag == null){
	//				FMLLog.severe("Tag deleted itemstack", new Object[0]);
	//				tag = new NBTTagCompound();
	//			}
	//			MultiBlockCoreTileEntity tile = MultiBlockCoreTileEntity.createTileFromNBT(tag);
	//			world.setTileEntity(x, y, z, tile);
	//			field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
	//			field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
	//		}
	//		return true;
	//	}

	@Override
	public EnumRarity getRarity(ItemStack itemStack) {
		return EnumRarity.RARE;
	}

	@Override
	public void addToolTip(List<String> toolTip, ItemStack itemStack, EntityPlayer player, boolean shiftPressed, boolean advancedToolTip) {
		if(shiftPressed){
			NBTTagCompound tag = itemStack.getTagCompound();
			if(tag != null){
				int xSize = tag.getInteger("xSize");
				int ySize = tag.getInteger("ySize");
				int zSize = tag.getInteger("zSize");
				toolTip.add(xSize + " x " + ySize + " x " + zSize);
				RectangularSolid solid = new RectangularSolid(xSize, ySize, zSize);
				NBTTagList blockTagList = tag.getTagList("blockList", Constants.NBT.TAG_COMPOUND);
				if(blockTagList != null){
					for (int tagCounter = 0; tagCounter < blockTagList.tagCount(); ++tagCounter){
						NBTTagCompound blockTagCompound = blockTagList.getCompoundTagAt(tagCounter);
						String name = blockTagCompound.getString("name");
						int index = blockTagCompound.getInteger("index");
						BlockPos p = solid.toCoord(index);
						toolTip.add(name + ", " + p);
					}
				}
				NBTTagList interfaceTagList = tag.getTagList("interface", Constants.NBT.TAG_COMPOUND);
				if(interfaceTagList != null){
					for (int tagCounter = 0; tagCounter < interfaceTagList.tagCount(); ++tagCounter){
						NBTTagCompound interfaceTag = interfaceTagList.getCompoundTagAt(tagCounter);
						int side = interfaceTag.getInteger("side");
						int x = interfaceTag.getInteger("x");
						int y = interfaceTag.getInteger("y");
						int z = interfaceTag.getInteger("z");
						toolTip.add("Interface : " + EnumFacing.getFront(side) + " To " + x + ", " + y + ", " + z);
					}
				}
			}
		}
	}
	@Override
	public int getNeutralLines() {
		return 0;
	}
	@Override
	public int getShiftLines() {
		return 0;
	}

}
