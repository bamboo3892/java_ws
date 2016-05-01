package com.test.block;

import com.test.main.TestCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TestBlock extends Block{

	@SideOnly(Side.CLIENT)
	IIcon frontIcon;
	@SideOnly(Side.CLIENT)
	IIcon sideIcon;
	@SideOnly(Side.CLIENT)
	IIcon topIcon;

	public TestBlock(){
		super(Material.iron);
		setBlockName("testBlock");
		setBlockTextureName("stone");
		setCreativeTab(TestCore.testCreativeTab);
		;
		;
		;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float dx, float dy, float dz){
		player.addChatMessage(new ChatComponentText("No Connection Found"));
		world.spawnParticle("reddust", x,  y , z, 0.0D, 0.0D, 0.0D);

		/*
		if (!world.isRemote){
			player.openGui(TestCore.instance, TestCore.TESTBLOCK_GUI_ID, world, x, y, z);
		}
		return truer
		*/
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack itemStack){
		int playerDir = MathHelper.floor_double((double)(entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		ForgeDirection[] blockDir = {ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST};
		world.setBlockMetadataWithNotify(x, y, z, blockDir[playerDir].ordinal(), 2);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		this.frontIcon = iconRegister.registerIcon("furnace_front_off");
		this.sideIcon  = iconRegister.registerIcon("furnace_side");
		this.topIcon   = iconRegister.registerIcon("furnace_top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if (side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal()){
			return this.topIcon;
		}
		return side == meta ? this.frontIcon : this.sideIcon;
	}

	//inventory
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta){
		return 0xEE1111;
	}

	//world
	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess iBlockAccess, int x, int y, int z){
		return 0x1111EE;
	}

}
