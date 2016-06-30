package com.okina.multiblock.construct.block;

import static com.okina.main.TestCore.*;

import com.okina.multiblock.construct.tileentity.ConstructVirtualGrowerTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ConstructVirtualGlower extends ConstructFunctionalBase {

	//	private IIcon top;
	//	private IIcon side;

	public ConstructVirtualGlower(int grade) {
		super(grade);
		setUnlocalizedName(AUTHER + ".virtualGrower");
	}

	//	@SideOnly(Side.CLIENT)
	//	@Override
	//	public IIcon getIcon(int side, int meta) {
	//		return side == 1 ? this.top : (side == 0 ? this.side : this.blockIcon);
	//	}

	//	@Override
	//	public void registerBlockIcons(IIconRegister register) {
	//		this.blockIcon = Blocks.log.getBlockTextureFromSide(2);
	//		this.side = Blocks.log.getBlockTextureFromSide(0);
	//		this.top = register.registerIcon(TestCore.MODID + ":grass_top");
	//	}

	@Override
	public int getShiftLines() {
		return 4;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ConstructVirtualGrowerTileEntity(grade);
	}

}
