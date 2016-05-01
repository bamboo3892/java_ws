package com.test.tileentity;

import com.test.network.SimpleTilePacket;
import com.test.network.SimpleTilePacket.PacketType;
import com.test.utils.Position;

public interface ISimpleTilePacketUser {

	SimpleTilePacket getPacket(PacketType type);
	void processCommand(PacketType type, Object value);
	Position getPosition();

}
