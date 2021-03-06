package frogcraftrebirth.common.gui;

import frogcraftrebirth.common.tile.TileHSU;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerHybridEStorage extends ContainerTileFrog<TileHSU> {

	public ContainerHybridEStorage(InventoryPlayer playerInv, TileHSU tile) {
		super(playerInv, tile);
		this.addSlotToContainer(new SlotCharger(tile.inv, 0, 113, 24));
		this.addSlotToContainer(new SlotDischarger(tile.inv, 1, 113, 42));
		this.registerPlayerInventory(playerInv);
	}

}
