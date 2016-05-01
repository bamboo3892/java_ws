package com.test.multiblock;

import java.util.ArrayList;

import com.test.main.TestCore;
import com.test.utils.Position;
import com.test.utils.RectangularSolid;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockFrameTileEntity extends TileEntity {

	public int[] length = new int[6];

	public BlockFrameTileEntity() {

	}

	@Override
	public void updateEntity() {
		checkConnection();
	}

	public void checkConnection() {
		length = new int[6];
		for (int side = 0; side < 6; side++){
			ForgeDirection dir = ForgeDirection.getOrientation(side);
			if(length[dir.ordinal()] == -1) continue;
			for (int n = 1; n < 16; n++){
				int newX = xCoord + dir.offsetX * n;
				int newY = yCoord + dir.offsetY * n;
				int newZ = zCoord + dir.offsetZ * n;
				if(worldObj.getTileEntity(newX, newY, newZ) instanceof BlockFrameTileEntity){
					length[dir.ordinal()] = n;
					length[dir.getOpposite().ordinal()] = -1;
					break;
				}
			}
		}
	}

	public boolean tryConstruct() {
		checkConnection();
		if(worldObj.isRemote) return true;
		//check
		ArrayList<Position> list = new ArrayList<Position>();
		if(!checkCubicFrame(list)) return false;
		System.out.println("ok");

		ItemStack itemStack = new ItemStack(TestCore.multiBlock, 1);
		RectangularSolid solid = new RectangularSolid(list);
		writeNBTToItemStack(itemStack, solid);
		if(worldObj.spawnEntityInWorld(new EntityItem(worldObj, xCoord, yCoord, zCoord, itemStack))){
			for (int index = 0; index < solid.getIndexSize(); index++){
				Position p = solid.toCoord(index);
				worldObj.setBlockToAir(p.x, p.y, p.z);
			}
		}
		System.out.println(itemStack.getTagCompound());
		return true;
	}

	private boolean checkCubicFrame(ArrayList<Position> list) {
		System.out.println("start checking");
		list.add(new Position(0, 0, 0));
		for (int n = 0; n < 6; n++){
			if(length[n] > 0){
				ForgeDirection dir = ForgeDirection.getOrientation(n);
				list.add(new Position(dir.offsetX * length[n], dir.offsetY * length[n], dir.offsetZ * length[n]));
			}
		}
		if(list.size() != 4) return false;
		list.add(Position.sum(list.get(1), list.get(2)));
		list.add(Position.sum(list.get(2), list.get(3)));
		list.add(Position.sum(list.get(3), list.get(1)));
		list.add(Position.sum(list.get(3), list.get(4)));
		Position tilePos = new Position(xCoord, yCoord, zCoord);
		for(int i=0;i<list.size();i++){
			list.set(i, Position.sum(list.get(i), tilePos));
			Position p = list.get(i);
			if(!(worldObj.getTileEntity(p.x, p.y, p.z) instanceof BlockFrameTileEntity)){
				return false;
			}
		}
		
//		for (Position p : list){
//			p = Position.sum(p, new Position(xCoord, yCoord, zCoord));
//			if(!(worldObj.getTileEntity(p.x, p.y, p.z) instanceof BlockFrameTileEntity)){
//				return false;
//			}
//		}

		int k[] = new int[3];//length
		for (int kkk = 0; kkk < 3; kkk++){
			k[kkk] = -1;
		}
		ForgeDirection dir[] = new ForgeDirection[3];//direction
		for (int l = 0; l < 6; l++){
			if(length[l] > 0){
				if(k[0] == -1){
					k[0] = length[l];
					dir[0] = ForgeDirection.getOrientation(l);
				}else if(k[1] == -1){
					k[1] = length[l];
					dir[1] = ForgeDirection.getOrientation(l);
				}else if(k[2] == -1){
					k[2] = length[l];
					dir[2] = ForgeDirection.getOrientation(l);
				}else{
					System.out.println("something is wrong");
				}
			}
		}
		for (int i1 = 0; i1 < 3; i1++){
			int i2 = i1 == 2 ? 0 : i1 + 1;
			int i3 = i2 == 2 ? 0 : i2 + 1;
			for (int n1 = 0; n1 < 2; n1++)
				for (int n2 = 0; n2 < 2; n2++){
					for (int m = 1; m < k[i1]; m++){
						int x = m * dir[i1].offsetX + k[i2] * dir[i2].offsetX * n1 + k[i3] * dir[i3].offsetX * n2 + xCoord;
						int y = m * dir[i1].offsetY + k[i2] * dir[i2].offsetY * n1 + k[i3] * dir[i3].offsetY * n2 + yCoord;
						int z = m * dir[i1].offsetZ + k[i2] * dir[i2].offsetZ * n1 + k[i3] * dir[i3].offsetZ * n2 + zCoord;
						if(!(worldObj.getBlock(x, y, z) == TestCore.blockFrameLine)) return false;
					}
				}
		}
		return true;
	}

	private void writeNBTToItemStack(ItemStack itemStack, RectangularSolid solid) {
		NBTTagCompound tags = new NBTTagCompound();
		//itemStack.writeToNBT(tags);
		//itemStack.stackTagCompound = tags;
		MultiBlockTileEntity.writeDetailToNBT(tags, worldObj, solid);
		itemStack.setTagCompound(tags);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB bb = INFINITE_EXTENT_AABB;
		return bb;
	}

}
