package com.test.multiblock;

import java.util.List;

import com.test.main.TestCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * metadata
 * 0 : pipe
 * 1 : pipe EX
 */
public class BlockPipe extends BlockContainer {

	public BlockPipe() {
		super(Material.rock);
		setCreativeTab(TestCore.testCreativeTab);
		setBlockName("pipe");
		this.setLightOpacity(0);
		this.setHardness(1.5F);
		setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
	}

	/**when game starting , this is called before TIleEntity.readFromNBT*/
	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
		super.onPostBlockPlaced(world, x, y, z, meta);
		if(!(world.getTileEntity(x, y, z) instanceof BlockPipeTileEntity)) return;
		BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(x, y, z);
		tile.checkConnection();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
		super.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
		if(!(world.getTileEntity(x, y, z) instanceof BlockPipeTileEntity)) return;
		BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(x, y, z);
		tile.checkConnection();
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if(world.getTileEntity(x, y, z) instanceof BlockPipeTileEntity){
			BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(x, y, z);
			tile.onTileRemoved();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List list, Entity entity) {
		this.setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
		super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		if(!(world.getTileEntity(x, y, z) instanceof BlockPipeTileEntity)) return;
		BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(x, y, z);
		if(tile.connection == null) return;
		if(tile.connection[0]){
			this.setBlockBounds(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if(tile.connection[1]){
			this.setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if(tile.connection[2]){
			this.setBlockBounds(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if(tile.connection[3]){
			this.setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if(tile.connection[4]){
			this.setBlockBounds(0F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		if(tile.connection[5]){
			this.setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, x, y, z, aabb, list, entity);
		}
		this.setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
		this.setBlockBoundsForItemRender();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int meta) {
		return true;
	}

	@Override
	public int getRenderType() {
		return TestCore.BLOCKPIPE_RENDER_ID;
	}

	@Override
	public IIcon getIcon(int side, int meta){
		this.blockIcon = meta == 0 ? Blocks.redstone_block.getIcon(0, 0) : Blocks.portal.getIcon(0, 0);
		return this.blockIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = Blocks.redstone_block.getIcon(0, 0);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new BlockPipeTileEntity();
	}

}
