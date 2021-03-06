package com.test.multiblock;

import com.test.multiblock.construct.parts.ConstructPartBase;
import com.test.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.test.register.ConstructPartRegistry;
import com.test.utils.Position;
import com.test.utils.RectangularSolid;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class MultiBlockTileEntity extends TileEntity {

	private int xSize;//the number of blocks in x direction
	private int ySize;
	private int zSize;
	private ConstructPartBase[][][] parts;

	public MultiBlockTileEntity() {

	}

	public void writeToNBTForItemDrop(ItemStack itemStack) {
		NBTTagCompound itemTag = itemStack.getTagCompound() == null ? new NBTTagCompound() : itemStack.getTagCompound();
		this.writeToNBT(itemTag);
		itemStack.setTagCompound(itemTag);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		xSize = nbtTagCompound.getInteger("xSize");
		ySize = nbtTagCompound.getInteger("ySize");
		zSize = nbtTagCompound.getInteger("zSize");
		parts = new ConstructPartBase[xSize][ySize][zSize];
		NBTTagList blockTagList = nbtTagCompound.getTagList("blockList", Constants.NBT.TAG_COMPOUND);
		RectangularSolid solid = new RectangularSolid(xSize, ySize, zSize);
		for (int tagCounter = 0; tagCounter < blockTagList.tagCount(); ++tagCounter){
			NBTTagCompound blockTagCompound = (NBTTagCompound) blockTagList.getCompoundTagAt(tagCounter);
			int index = blockTagCompound.getInteger("index");
			Position p = solid.toCoord(index);
			parts[p.x][p.y][p.z] = ConstructPartRegistry.getNewPartFromNBT(blockTagCompound, solid);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setInteger("xSize", xSize);
		nbtTagCompound.setInteger("ySize", ySize);
		nbtTagCompound.setInteger("zSize", zSize);
		NBTTagList blocksTagList = new NBTTagList();
		RectangularSolid solid = new RectangularSolid(xSize, ySize, zSize);
		for (int index = 0; index < solid.getIndexSize(); index++){
			Position p = solid.toCoord(index);
			if(parts[p.x][p.y][p.z] != null){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("index", index);
				tag.setString("name", parts[p.x][p.y][p.z].getNameForNBT());
				parts[p.x][p.y][p.z].writeToNBTTagCompound(tag, solid);
				blocksTagList.appendTag(tag);
			}
		}
		nbtTagCompound.setTag("blockList", blocksTagList);
	}

	public static MultiBlockTileEntity createTileFromNBT(NBTTagCompound tagCompound) {
		MultiBlockTileEntity tile = new MultiBlockTileEntity();
		tile.readFromNBT(tagCompound);
		return tile;
	}

	/**solid is not relative*/
	public static void writeDetailToNBT(NBTTagCompound tagCompound, World world, RectangularSolid solid) {
		tagCompound.setInteger("xSize", solid.getXSize());
		tagCompound.setInteger("ySize", solid.getYSize());
		tagCompound.setInteger("zSize", solid.getZSize());

		NBTTagList blocksTagList = new NBTTagList();

		for (int index = 0; index < solid.getIndexSize(); index++){
			Position p = solid.toCoord(index);
			if(world.getTileEntity(p.x, p.y, p.z) instanceof ConstructBaseTileEntity){
				ConstructBaseTileEntity base = (ConstructBaseTileEntity) world.getTileEntity(p.x, p.y, p.z);
				NBTTagCompound tag = new NBTTagCompound();
				tag.setInteger("index", index);
				tag.setString("name", base.getNameForNBT());
				base.writeDetailToNBTForItemStack(tag, solid);
				blocksTagList.appendTag(tag);
			}
		}
		tagCompound.setTag("blockList", blocksTagList);
	}

	public void showDebug() {
		System.out.println("xSize = " + xSize + " ySize = " + ySize + " zSize = " + zSize);
		for (int i = 0; i < xSize; i++){
			for (int j = 0; j < ySize; j++){
				for (int k = 0; k < zSize; k++){
					if(parts[i][j][k] != null){
						System.out.println(parts[i][j][k].getNameForNBT() + " : x, y, z = " + i + ", " + j + ", " + k);
					}
				}
			}
		}
	}

}
