package frogcraftrebirth.client;

import frogcraftrebirth.api.FrogAPI;
import frogcraftrebirth.common.FrogProxy;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class FrogProxyClient extends FrogProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		OBJLoader.INSTANCE.addDomain(FrogAPI.MODID);
		FrogTextures.initFrogItemsTexture();
		FrogTextures.initFrogBlocksTexture();
		FrogAPI.FROG_LOG.info("Client side pre-initialization finished");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FrogAPI.FROG_LOG.info("Client side initialization finished");
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		FrogAPI.FROG_LOG.info("Client side post-initialization finished");
	}

}
