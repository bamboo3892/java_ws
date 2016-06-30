package com.okina.multiblock;

import java.util.ArrayList;

import com.okina.main.TestCore;
import com.okina.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.okina.multiblock.construct.tileentity.ConstructInterfaceTileEntity;
import com.okina.utils.RectangularSolid;
import com.okina.utils.UtilMethods;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class BlockFrameTileEntity extends TileEntity implements ITickable {

	public int[] length = new int[6];

	public BlockFrameTileEntity() {

	}

	@Override
	public void update() {
		checkConnection();
	}

	public void checkConnection() {
		length = new int[6];
		for (EnumFacing side : EnumFacing.VALUES){
			if(length[side.getIndex()] == -1) continue;
			for (int n = 1; n < 16; n++){
				BlockPos newPos = pos.add(side.getFrontOffsetX() * n, side.getFrontOffsetY() * n, side.getFrontOffsetZ() * n);
				if(worldObj.getTileEntity(newPos) instanceof BlockFrameTileEntity){
					length[side.getIndex()] = n;
					length[side.getOpposite().getIndex()] = -1;
					break;
				}
			}
		}
	}

	public boolean tryConstruct(EntityPlayer player, EnumFacing side) {
		checkConnection();
		if(worldObj.isRemote) return true;
		//check
		ArrayList<BlockPos> interfaceList = new ArrayList<BlockPos>();
		RectangularSolid solid = new RectangularSolid();
		if(!checkCubicFrame(interfaceList, solid, player)) return false;
		player.addChatMessage(new ChatComponentText("Construct Success!"));
		ItemStack itemStack = new ItemStack(TestCore.multiBlockCore, 1);
		solid = new RectangularSolid(solid.getMinPoint().add(1, 1, 1), solid.getMaxPoint().add(-1, -1, -1));
		writeNBTToItemStack(itemStack, solid, interfaceList);
		if(worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX() + side.getFrontOffsetX(), pos.getY() + side.getFrontOffsetY(), pos.getZ() + side.getFrontOffsetZ(), itemStack))){
			if(!player.capabilities.isCreativeMode){
				for (int x = -1; x < solid.getXSize() + 1; x++){
					for (int y = -1; y < solid.getYSize() + 1; y++){
						for (int z = -1; z < solid.getZSize() + 1; z++){
							worldObj.setBlockToAir(solid.getMinPoint().add(x, y, z));
						}
					}
				}
			}
		}
		return true;
	}

	private boolean checkCubicFrame(ArrayList<BlockPos> interfaceList, RectangularSolid solid, EntityPlayer player) {
		ArrayList<BlockPos> list = new ArrayList<BlockPos>();
		list.add(new BlockPos(0, 0, 0));
		for (int n = 0; n < 6; n++){
			if(length[n] > 0){
				EnumFacing dir = EnumFacing.getFront(n);
				list.add(UtilMethods.multVeci(dir.getDirectionVec(), n));
				//				list.add(new BlockPos(dir.offsetX * length[n], dir.offsetY * length[n], dir.offsetZ * length[n]));
			}
		}
		if(list.size() != 4) return false;
		list.add(list.get(1).add(list.get(2)));
		list.add(list.get(2).add(list.get(3)));
		list.add(list.get(3).add(list.get(1)));
		list.add(list.get(3).add(list.get(4)));

		//frame core
		for (int i = 0; i < list.size(); i++){
			list.set(i, pos.add(list.get(i)));
			BlockPos p = list.get(i);
			if(!(worldObj.getTileEntity(p) instanceof BlockFrameTileEntity)){
				return false;
			}
		}
		solid.set(list);

		//		for (Position p : list){
		//			p = Position.sum(p, new Position(xCoord, yCoord, zCoord));
		//			if(!(worldObj.getTileEntity(p.x, p.y, p.z) instanceof BlockFrameTileEntity)){
		//				return false;
		//			}
		//		}

		//frame line
		int k[] = new int[3];//length
		for (int kkk = 0; kkk < 3; kkk++){
			k[kkk] = -1;
		}
		EnumFacing dir[] = new EnumFacing[3];//direction
		for (int l = 0; l < 6; l++){
			if(length[l] > 0){
				if(k[0] == -1){
					k[0] = length[l];
					dir[0] = EnumFacing.getFront(l);
				}else if(k[1] == -1){
					k[1] = length[l];
					dir[1] = EnumFacing.getFront(l);
				}else if(k[2] == -1){
					k[2] = length[l];
					dir[2] = EnumFacing.getFront(l);
				}else{
					System.err.println("something is wrong");
				}
			}
		}
		for (int i1 = 0; i1 < 3; i1++){
			int i2 = i1 == 2 ? 0 : i1 + 1;
			int i3 = i2 == 2 ? 0 : i2 + 1;
			for (int n1 = 0; n1 < 2; n1++){
				for (int n2 = 0; n2 < 2; n2++){
					for (int m = 1; m < k[i1]; m++){
						int x = m * dir[i1].getFrontOffsetX() + k[i2] * dir[i2].getFrontOffsetX() * n1 + k[i3] * dir[i3].getFrontOffsetX() * n2 + pos.getX();
						int y = m * dir[i1].getFrontOffsetY() + k[i2] * dir[i2].getFrontOffsetY() * n1 + k[i3] * dir[i3].getFrontOffsetY() * n2 + pos.getY();
						int z = m * dir[i1].getFrontOffsetZ() + k[i2] * dir[i2].getFrontOffsetZ() * n1 + k[i3] * dir[i3].getFrontOffsetZ() * n2 + pos.getZ();
						BlockPos linePos = pos.add(UtilMethods.multVeci(dir[i1].getDirectionVec(), m)).add(UtilMethods.multVeci(dir[i2].getDirectionVec(), k[i2] * n1)).add(UtilMethods.multVeci(dir[i3].getDirectionVec(), k[i3] * n2));
						if(!(worldObj.getBlockState(linePos).getBlock() == TestCore.blockFrameLine)) return false;
					}
				}
			}
		}

		//interface
		BlockPos p1 = null;
		BlockPos p2 = null;
		boolean count;
		for (int side = 0; side < 6; side++){
			switch (side) {
			case 0:
				p1 = list.get(0);
				p2 = list.get(4);
				break;
			case 1:
				p1 = list.get(0);
				p2 = list.get(5);
				break;
			case 2:
				p1 = list.get(0);
				p2 = list.get(6);
				break;
			case 3:
				p1 = list.get(4);
				p2 = list.get(5);
				break;
			case 4:
				p1 = list.get(4);
				p2 = list.get(6);
				break;
			case 5:
				p1 = list.get(5);
				p2 = list.get(6);
				break;
			}
			count = false;
			for (int i1 = (p1.getX() < p2.getX() ? p1.getX() : p2.getX()); i1 <= (p1.getX() < p2.getX() ? p2.getX() : p1.getX()); i1++){
				for (int i2 = (p1.getY() < p2.getY() ? p1.getY() : p2.getY()); i2 <= (p1.getY() < p2.getY() ? p2.getY() : p1.getY()); i2++){
					for (int i3 = (p1.getZ() < p2.getZ() ? p1.getZ() : p2.getZ()); i3 <= (p1.getZ() < p2.getZ() ? p2.getZ() : p1.getZ()); i3++){
						if(worldObj.getTileEntity(new BlockPos(i1, i2, i3)) instanceof ConstructInterfaceTileEntity){
							if(count){
								player.addChatMessage(new ChatComponentText("Too Many Interface"));
								return false;
							}else{
								interfaceList.add(new BlockPos(i1, i2, i3));
								count = true;
							}
						}else if(worldObj.getTileEntity(new BlockPos(i1, i2, i3)) instanceof BlockFrameTileEntity){
							if(!((i1 == p1.getX() || i1 == p2.getX()) && (i2 == p1.getY() || i2 == p2.getY()) && (i3 == p1.getZ() || i3 == p2.getZ()))){
								player.addChatMessage(new ChatComponentText("illegal frame"));
								return false;
							}
						}else if(worldObj.getTileEntity(new BlockPos(i1, i2, i3)) != null){
							player.addChatMessage(new ChatComponentText("frame pane can include only interface"));
							return false;
						}
					}
				}
			}
		}
		p1 = list.get(0);
		p2 = list.get(7);
		for (int i1 = (p1.getX() < p2.getX() ? p1.getX() : p2.getX()) + 1; i1 < (p1.getX() < p2.getX() ? p2.getX() : p1.getX()); i1++){
			for (int i2 = (p1.getY() < p2.getY() ? p1.getY() : p2.getY()) + 1; i2 < (p1.getY() < p2.getY() ? p2.getY() : p1.getY()); i2++){
				for (int i3 = (p1.getZ() < p2.getZ() ? p1.getZ() : p2.getZ()) + 1; i3 < (p1.getZ() < p2.getZ() ? p2.getZ() : p1.getZ()); i3++){
					if(worldObj.getTileEntity(new BlockPos(i1, i2, i3)) instanceof ConstructInterfaceTileEntity){
						player.addChatMessage(new ChatComponentText("Interface cannot be inside the frame"));
						return false;
					}else if(worldObj.getTileEntity(new BlockPos(i1, i2, i3)) instanceof BlockPipeTileEntity || worldObj.getTileEntity(new BlockPos(i1, i2, i3)) instanceof ConstructBaseTileEntity){

					}else if(worldObj.getTileEntity(new BlockPos(i1, i2, i3)) != null){
						player.addChatMessage(new ChatComponentText("Illegal Block contains"));
						return false;
					}
				}
			}
		}

		return true;
	}

	private void writeNBTToItemStack(ItemStack itemStack, RectangularSolid solid, ArrayList<BlockPos> interfaceList) {
		NBTTagCompound tags = new NBTTagCompound();
		tags.setInteger("xSize", solid.getXSize());
		tags.setInteger("ySize", solid.getYSize());
		tags.setInteger("zSize", solid.getZSize());
		tags.setBoolean("pause", true);

		NBTTagList blocksTagList = new NBTTagList();
		for (int index = 0; index < solid.getIndexSize(); index++){
			BlockPos p = solid.toCoord(index);
			if(worldObj.getTileEntity(p) instanceof ConstructBaseTileEntity){
				ConstructBaseTileEntity base = (ConstructBaseTileEntity) worldObj.getTileEntity(p);
				NBTTagCompound baseTag = new NBTTagCompound();
				baseTag.setInteger("index", index);
				baseTag.setString("name", base.getNameForNBT());
				base.writeDetailToNBTForItemStack(baseTag, solid);
				blocksTagList.appendTag(baseTag);
			}
		}
		tags.setTag("blockList", blocksTagList);

		NBTTagList interfaceTagList = new NBTTagList();
		BlockPos min = solid.getMinPoint();
		BlockPos max = solid.getMaxPoint();
		for (BlockPos p : interfaceList){
			if(worldObj.getTileEntity(p) instanceof ConstructInterfaceTileEntity){
				ConstructInterfaceTileEntity inter = (ConstructInterfaceTileEntity) worldObj.getTileEntity(p);
				NBTTagCompound interfaceTag = new NBTTagCompound();
				if(p.getY() < min.getY()){
					interfaceTag.setInteger("side", 0);
				}else if(p.getY() > max.getY()){
					interfaceTag.setInteger("side", 1);
				}else if(p.getZ() < min.getZ()){
					interfaceTag.setInteger("side", 2);
				}else if(p.getZ() > max.getZ()){
					interfaceTag.setInteger("side", 3);
				}else if(p.getX() < min.getX()){
					interfaceTag.setInteger("side", 4);
				}else if(p.getX() > max.getX()){
					interfaceTag.setInteger("side", 5);
				}
				if(inter.connection != null && inter.connection.getTile() != null){
					interfaceTag.setInteger("x", inter.connection.x - min.getX());
					interfaceTag.setInteger("y", inter.connection.y - min.getY());
					interfaceTag.setInteger("z", inter.connection.z - min.getZ());
				}else{
					interfaceTag.setInteger("x", -1);
					interfaceTag.setInteger("y", -1);
					interfaceTag.setInteger("z", -1);
				}
				interfaceTag.setInteger("grade", inter.grade);
				interfaceTagList.appendTag(interfaceTag);
			}
		}
		tags.setTag("interface", interfaceTagList);

		itemStack.setTagCompound(tags);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB bb = INFINITE_EXTENT_AABB;
		return bb;
	}

}
