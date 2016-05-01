package com.test.block;

import com.test.main.TestCore;
import com.test.tileentity.TileTestBlockTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileTestBlock extends BlockContainer {

	public TileTestBlock() {
		super(Material.iron);
		setBlockName("tileTestBlock");//lang file name
		setCreativeTab(TestCore.testCreativeTab);
		;
		;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float dx, float dy, float dz) {
		if(!world.isRemote){
			player.openGui(TestCore.instance, TestCore.TILETESTBLOCK_GUI_ID, world, x, y, z);
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon(TestCore.MODID + ":construct_pane");
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileTestBlockTileEntity();
	}

}
