package com.test.multiblock.construct.block;

import com.test.multiblock.construct.tileentity.ConstructCrusherTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ConstructCrusher extends ConstructFunctionalBase {

	public ConstructCrusher(int grade) {
		super(grade);
		setBlockName("crusher");
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemstack) {
		int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);
		world.setBlockMetadataWithNotify(x, y, z, meta, 2);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		return side != meta ? this.blockIcon : this.front;
	}

	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = Blocks.cactus.getIcon(2, 0);
		this.front = Blocks.cactus.getIcon(0, 0);
		this.top = Blocks.cactus.getIcon(2, 0);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructCrusherTileEntity(grade);
	}

}
