package com.test.multiblock.construct.block;

import com.test.main.TestCore;
import com.test.multiblock.construct.tileentity.ConstructBaseTileEntity;
import com.test.multiblock.construct.tileentity.ConstructFurnaceTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ConstructFurnace extends ConstructFunctionalBase {

	protected IIcon top;
	protected IIcon front;

	public ConstructFurnace(int grade) {
		super(grade);
		setBlockName("furnace");
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
		if(!(world.getTileEntity(x, y, z) instanceof ConstructBaseTileEntity)) return;
		ConstructBaseTileEntity tile = (ConstructBaseTileEntity) world.getTileEntity(x, y, z);
		tile.updateIsNeighberBaseBlock();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon("furnace_side");
		this.front = register.registerIcon(TestCore.MODID + ":furnace_front_off");
		this.top = register.registerIcon("furnace_top");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return side == 1 ? this.top : (side == 0 ? this.top : (side != meta ? this.blockIcon : this.front));
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructFurnaceTileEntity(grade);
	}

}
