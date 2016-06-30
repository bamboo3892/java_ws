package com.okina.block.properties;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertyConnection implements IUnlistedProperty<Integer> {

	private static final ImmutableSet<Integer> allowedValues;
	static{
		Set<Integer> set = Sets.<Integer> newHashSet();
		for (int i = 0; i <= 63; ++i){
			set.add(Integer.valueOf(i));
		}
		allowedValues = ImmutableSet.copyOf(set);
	}

	private String name;
	private Integer connection = 0;

	public PropertyConnection(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isValid(Integer value) {
		return allowedValues.contains(value);
	}

	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}

	@Override
	public String valueToString(Integer value) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 6; i++){
			builder.append(EnumFacing.getFront(i) + ":" + (((value >> i) & 1) == 1) + " ");
		}
		return builder.toString();
	}

}
