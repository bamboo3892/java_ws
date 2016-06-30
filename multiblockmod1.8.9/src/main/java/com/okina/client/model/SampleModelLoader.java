package com.okina.client.model;

import com.okina.main.TestCore;

import net.minecraft.block.Block;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SampleModelLoader implements ICustomModelLoader {

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		if(modelLocation.getResourceDomain().equals(TestCore.MODID.toLowerCase()) && (modelLocation.getResourcePath().startsWith("models/block"))){
			return true;
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) {
		if(modelLocation.getResourceDomain().equals(TestCore.MODID.toLowerCase()) && (modelLocation.getResourcePath().startsWith("models/block/"))){
			String name = modelLocation.getResourcePath().replace("models/block/", "");
			System.out.println(name);
			if(name.equals("pipe")){
				return new BlockPipeModel();
			}else if(name.equals("blockFrame")){
				return new BlockFrameModel();
			}else if(name.equals("blockFrameLine")){
				return new BlockFrameLineModel();
			}else if(name.equals("multiBlockCore")){
				return new SampleModel();
			}else if(name.equals("multiBlockCasing")){
				return new MultiBlockCasingModel();
			}else if(name.equals("disassemblyTable")){
				return new DisassemblyTableModel();
			}else{//grade stuff
				Block block = GameRegistry.findBlock(TestCore.MODID, name);
				if(block != null){
					for (int i = 0; i < 5; i++){
						if(block == TestCore.baseFrame[i]){
							return new BaseFrameModel(i);
						}else if(block == TestCore.constructStorage[i]){
							return new ConstructBaseModel(block, i, new ResourceLocation("blocks/planks_oak"));
						}else if(block == TestCore.constructClockPulser[i]){
							return new ConstructBaseModel(block, i, new ResourceLocation("blocks/redstone_block"));
						}else if(block == TestCore.constructRepeater[i]){
							return new ConstructBaseModel(block, i, new ResourceLocation("blocks/quartz_block_side"));
						}else if(block == TestCore.constructEventCatcher[i]){
							return new ConstructBaseModel(block, i, new ResourceLocation(TestCore.MODID, "blocks/event_catcher"));
						}else if(block == TestCore.constructContainer[i]){
							return new ConstructBaseModel(block, i, new ResourceLocation(TestCore.MODID, "blocks/null"));
						}else if(block == TestCore.constructFurnace[i]){
							return new ConstructBaseModel(block, i, new ResourceLocation(TestCore.MODID, "blocks/event_catcher"));
						}else if(block == TestCore.constructVirtualGrower[i]){
							return new ConstructBaseModel(block, i, new ResourceLocation(TestCore.MODID, "blocks/event_catcher"));
						}else if(block == TestCore.constructEnergyProvider[i]){
							return new ConstructBaseModel(block, i, new ResourceLocation("blocks/portal"));
						}else if(block == TestCore.constructCrusher[i]){
							return new ConstructBaseModel(block, i, new ResourceLocation(TestCore.MODID, "blocks/event_catcher"));
						}else if(block == TestCore.constructDispatcher[i]){
							return new ConstructBaseModel(block, i, new ResourceLocation("blocks/lapis_block"));
						}else if(block == TestCore.constructInterface[i]){
							return new ConstructBaseModel(block, i, new ResourceLocation("blocks/enchanting_table_top"));
						}
					}
				}
			}
			return new SampleModel();
		}
		return null;
	}

	/**
	外部リソースを利用する場合はresourceManagerをここで取得しておく必要がある。
	今回は、内部で完結するので必要ない。
	*/
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {}
}
