package com.okina.network;

import com.okina.main.TestCore;
import com.okina.tileentity.ISimpleTilePacketUser;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SimpleTilePacket implements IMessage {

	public int x;
	public int y;
	public int z;
	public PacketType type;
	public Object value;

	public SimpleTilePacket() {}

	public SimpleTilePacket(ISimpleTilePacketUser tile, PacketType type, Object value) {
		this(tile.getPosition(), type, value);
	}

	public SimpleTilePacket(BlockPos p, PacketType type, Object value) {
		x = p.getX();
		y = p.getY();
		z = p.getZ();
		this.type = type;
		this.value = value;
		if(!type.valueClass.isAssignableFrom(value.getClass())) throw new IllegalArgumentException();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		try{
			x = buf.readInt();
			y = buf.readInt();
			z = buf.readInt();
			type = PacketType.getFromId(buf.readByte());
			if(type.valueClass == Integer.class){
				value = buf.readInt();
			}else if(type.valueClass == String.class){
				value = ByteBufUtils.readUTF8String(buf);
			}else if(type.valueClass == NBTTagCompound.class){
				value = ByteBufUtils.readTag(buf);
			}
		}catch (Exception e){
			System.err.println("Illegal packet received : " + this);
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		try{
			buf.writeInt(x);
			buf.writeInt(y);
			buf.writeInt(z);
			buf.writeByte(type.id);
			if(type.valueClass == Integer.class){
				buf.writeInt((Integer) value);
			}else if(type.valueClass == String.class){
				ByteBufUtils.writeUTF8String(buf, (String) value);
			}else if(type.valueClass == NBTTagCompound.class){
				ByteBufUtils.writeTag(buf, (NBTTagCompound) value);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return String.format("Simple Tile Packet : x, y, z = %s, %s, %s : type = %s : value = %s", x, y, z, type, value);
	}

	/**server only*/
	public static class SimpleTilePacketHandler implements IMessageHandler<SimpleTilePacket, IMessage> {
		@Override
		public IMessage onMessage(SimpleTilePacket msg, MessageContext ctx) {
			//			System.out.println(String.format("Received %s from %s", msg, ctx.getServerHandler().playerEntity.getDisplayName()));
			if(ctx.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(msg.x, msg.y, msg.z)) instanceof ISimpleTilePacketUser){
				ISimpleTilePacketUser tile = (ISimpleTilePacketUser) ctx.getServerHandler().playerEntity.worldObj.getTileEntity(new BlockPos(msg.x, msg.y, msg.z));
				tile.processCommand(msg.type, msg.value);
				TestCore.packetDispatcher.sendToDimension(msg, ctx.getServerHandler().playerEntity.dimension);
			}
			return null;
		}
	}

	/**client only*/
	public static class SimpleTileReplyPacketHandler implements IMessageHandler<SimpleTilePacket, IMessage> {
		@Override
		public IMessage onMessage(final SimpleTilePacket msg, final MessageContext ctx) {
			//			System.out.println(String.format("Received %s from %s", msg, ctx.side.SERVER));

			//			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
			//			mainThread.addScheduledTask(new Runnable() {
			//				@Override
			//				public void run() {
			//					System.out.println(String.format("Received %s from %s", msg.type, ctx.getServerHandler().playerEntity.getDisplayName()));
			//				}
			//			});

			if(Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(msg.x, msg.y, msg.z)) instanceof ISimpleTilePacketUser){
				ISimpleTilePacketUser tile = (ISimpleTilePacketUser) Minecraft.getMinecraft().theWorld.getTileEntity(new BlockPos(msg.x, msg.y, msg.z));
				tile.processCommand(msg.type, msg.value);
			}
			return null;
		}
	}

	public enum PacketType {

		UNKNOWN(0, null),

		SLIDER_INPUT(1, Integer.class),

		EFFECT(2, Integer.class),

		EFFECT2(3, NBTTagCompound.class),

		ENERGY(4, Integer.class),

		FILTER(5, String.class),

		FLAG_IO(6, String.class),

		NBT_CONTENT(7, NBTTagCompound.class),

		NBT_CONNECTION(8, NBTTagCompound.class),

		NBT_MULTIBLOCK(9, NBTTagCompound.class),

		FILTER2(10, NBTTagCompound.class),

		RENDER(11, Integer.class),

		OTHER(12, String.class);

		public final int id;
		public final Class valueClass;

		private PacketType(int id, Class valueClass) {
			this.id = id;
			this.valueClass = valueClass;
		}

		public static PacketType getFromId(int id) {
			for (PacketType type : PacketType.values()){
				if(type.id == id) return type;
			}
			return UNKNOWN;
		}

	}
	/*for 1.8
	@Override
	public IMessage onMessage(PacketClockPulser message, MessageContext ctx) {
	    IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
	    mainThread.addScheduledTask(new Runnable() {
	        @Override
	        public void run() {
	            System.out.println(String.format("Received %s from %s", message.text, ctx.getServerHandler().playerEntity.getDisplayName()));
	        }
	    });
	    return null; // no response in this case
	}
	*/

}
