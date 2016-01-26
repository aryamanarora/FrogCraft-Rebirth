package frogcraftrebirth.common.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import frogcraftrebirth.common.network.PacketFrog02GuiDataUpdate;
import frogcraftrebirth.common.tile.TileFluidOutputHatch;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

public class ContainerFluidOutputHatch extends ContainerTileFrog<TileFluidOutputHatch> {
	
	int fluidAmount;

	public ContainerFluidOutputHatch(InventoryPlayer playerInv, TileFluidOutputHatch tile) {
		super(playerInv, tile); //DRAW SLOTS!!!!!
		
		this.registerPlayerInventory(playerInv);
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafting) {
		super.addCraftingToCrafters(crafting);
        crafting.sendProgressBarUpdate(this, 0, this.tile.getTankInfo()[0].capacity);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i=0;i<this.crafters.size();++i) {
			ICrafting crafter = (ICrafting)this.crafters.get(i);
			if (this.fluidAmount != this.tile.getTankInfo()[0].capacity)
				sendDataToClientSide(new PacketFrog02GuiDataUpdate(this.windowId, 0, this.tile.getTankInfo()[0].capacity), (EntityPlayerMP)crafter);
		}
		this.fluidAmount = this.tile.getTankInfo()[0].capacity;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int id, int value) {
		//if (id == 0) this.fluidAmount = value; TODO: synchronized FluidTank
	}
}