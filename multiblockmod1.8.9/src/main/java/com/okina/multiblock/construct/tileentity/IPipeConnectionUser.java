package com.okina.multiblock.construct.tileentity;

public interface IPipeConnectionUser {

	/**
	 * called by pipe when the pipe removed
	 * be sure to do check next tick, as the breaking pipe still be exist.
	 */
	public void needUpdateEntry();

}
