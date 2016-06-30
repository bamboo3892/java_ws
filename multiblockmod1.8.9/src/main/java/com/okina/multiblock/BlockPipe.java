package com.okina.multiblock;

import static com.okina.main.TestCore.*;

import java.util.List;

import com.okina.block.properties.PropertyConnection;
import com.okina.main.TestCore;
import com.okina.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.okina.utils.UtilMethods;

import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * metadata
 * 0 : pipe
 */
public class BlockPipe extends BlockContainer {

	public static final PropertyInteger COLOR = PropertyInteger.create("color", 0, 3);
	public static final PropertyConnection CONNECTION = new PropertyConnection("connection");

	public BlockPipe() {
		super(Material.rock);
		setCreativeTab(TestCore.testCreativeTab);
		setUnlocalizedName(AUTHER + ".pipe");
		setLightOpacity(0);
		setHardness(1.5F);
		setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if(world.getTileEntity(pos.add(facing.getOpposite().getDirectionVec())) instanceof BlockPipeTileEntity){
			return getStateFromMeta(world.getBlockState(pos.add(facing.getOpposite().getDirectionVec())).getValue(COLOR));
		}
		return getStateFromMeta(0);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IToolWrench){
			if(world.getTileEntity(pos) instanceof BlockPipeTileEntity){
				int meta = world.getBlockState(pos).getValue(COLOR);
				world.setBlockState(pos, meta == 3 ? world.getBlockState(pos).withProperty(COLOR, 0) : world.getBlockState(pos).withProperty(COLOR, meta + 1));
				((BlockPipeTileEntity) world.getTileEntity(pos)).checkConnection();
				for (EnumFacing dir : EnumFacing.VALUES){
					if(world.getTileEntity(pos.add(side.getDirectionVec())) instanceof BlockPipeTileEntity){
						((BlockPipeTileEntity) world.getTileEntity(pos.add(side.getDirectionVec()))).checkConnection();
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		super.onNeighborChange(world, pos, neighbor);
		if(!(world.getTileEntity(pos) instanceof BlockPipeTileEntity)) return;
		BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(pos);
		tile.checkConnection();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if(world.getTileEntity(pos) instanceof BlockPipeTileEntity){
			BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(pos);
			tile.onTileRemoved();
		}
		super.breakBlock(world, pos, state);
	}

	@Override
	public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB aabb, List<AxisAlignedBB> list, Entity entity) {
		setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
		super.addCollisionBoxesToList(world, pos, state, aabb, list, entity);
		if(!(world.getTileEntity(pos) instanceof BlockPipeTileEntity)) return;
		BlockPipeTileEntity tile = (BlockPipeTileEntity) world.getTileEntity(pos);
		if(tile.connection == null) return;
		if(tile.connection[0]){
			setBlockBounds(5F / 16F, 0F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, pos, state, aabb, list, entity);
		}
		if(tile.connection[1]){
			setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 16F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, pos, state, aabb, list, entity);
		}
		if(tile.connection[2]){
			setBlockBounds(5F / 16F, 5F / 16F, 0F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, pos, state, aabb, list, entity);
		}
		if(tile.connection[3]){
			setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 16F / 16F);
			super.addCollisionBoxesToList(world, pos, state, aabb, list, entity);
		}
		if(tile.connection[4]){
			setBlockBounds(0F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, pos, state, aabb, list, entity);
		}
		if(tile.connection[5]){
			setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 16F / 16F, 11F / 16F, 11F / 16F);
			super.addCollisionBoxesToList(world, pos, state, aabb, list, entity);
		}
		setBlockBounds(5F / 16F, 5F / 16F, 5F / 16F, 11F / 16F, 11F / 16F, 11F / 16F);
		setBlockBoundsForItemRender();
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isFullCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 3;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	protected BlockState createBlockState() {
		IProperty[] listedProperties = new IProperty[] { COLOR };
		IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] { CONNECTION };
		return new ExtendedBlockState(this, listedProperties, unlistedProperties);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if(state instanceof IExtendedBlockState){
			boolean[] b = new boolean[6];
			for (EnumFacing dir : EnumFacing.VALUES){
				if(world.getBlockState(pos.add(dir.getDirectionVec())).getBlock() instanceof BlockPipe){
					b[dir.getIndex()] = (state.getValue(COLOR) == world.getBlockState(pos.add(dir.getDirectionVec())).getValue(COLOR));
				}else if(world.getTileEntity(pos.add(dir.getDirectionVec())) instanceof ConstructBaseTileEntity){
					b[dir.getIndex()] = ((ConstructBaseTileEntity) world.getTileEntity(pos.add(dir.getDirectionVec()))).flagIO[dir.getOpposite().getIndex()] != 2;
				}
			}
			return ((IExtendedBlockState) state).withProperty(CONNECTION, UtilMethods.convertConnectionInfo(b));
		}
		return state;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(COLOR, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(COLOR);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new BlockPipeTileEntity();
	}

}
