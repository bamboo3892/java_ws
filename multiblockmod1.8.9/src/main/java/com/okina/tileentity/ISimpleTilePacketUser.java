package com.okina.tileentity;

import com.okina.network.SimpleTilePacket;
import com.okina.network.SimpleTilePacket.PacketType;

import net.minecraft.util.BlockPos;

public interface ISimpleTilePacketUser {

	SimpleTilePacket getPacket(PacketType type);
	void processCommand(PacketType type, Object value);
	BlockPos getPosition();

}
