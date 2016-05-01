package com.test.multiblock;

import com.test.main.TestCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockDesignTable extends BlockContainer {

	private IIcon top;
	private IIcon front;

	public BlockDesignTable() {
		super(Material.iron);
		this.setCreativeTab(TestCore.testCreativeTab);
		this.setLightOpacity(0);
		this.setHardness(1.5F);
		this.setBlockName("designTable");
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase livingBase, ItemStack itemStack) {
		int l = MathHelper.floor_double((double) (livingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		if(l == 0){
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		}else if(l == 1){
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);
		}else if(l == 2){
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);
		}else if(l == 3){
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote){
			player.openGui(TestCore.instance, TestCore.DESIGN_TABLE_GUI, world, x, y, z);
		}
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		;
		;
		super.breakBlock(world, x, y, z, block, meta);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? this.top : (side == 0 ? this.top : (side != meta ? this.blockIcon : this.front));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon(TestCore.MODID + ":design_table_side");
		this.top = register.registerIcon(TestCore.MODID + ":design_table_top");
		this.front = register.registerIcon(TestCore.MODID + ":design_table_front");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new BlockDesignTableTileEntity();
	}

}
