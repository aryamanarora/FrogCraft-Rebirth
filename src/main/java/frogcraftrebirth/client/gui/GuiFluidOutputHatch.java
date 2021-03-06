package frogcraftrebirth.client.gui;

import frogcraftrebirth.common.gui.ContainerFluidOutputHatch;
import frogcraftrebirth.common.tile.TileFluidOutputHatch;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiFluidOutputHatch extends GuiTileFrog<TileFluidOutputHatch, ContainerFluidOutputHatch> {

	public GuiFluidOutputHatch(InventoryPlayer playerInv, TileFluidOutputHatch tile) {
		super(new ContainerFluidOutputHatch(playerInv, tile), tile, "GUI_LiquidOutput.png");
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.mc.getTextureManager().bindTexture(guiBackground);
		this.drawTexturedModalRect(143, 23, 176, 0, 16, 47);
		
		if (mouseX > 143 + guiLeft && mouseX < 159 + guiLeft && mouseY > 23 + guiTop && mouseY < 70 + guiTop) {
			this.renderFluidTankTooltip(tile.tank, mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
		this.renderFluidTank(tile.tank, this.guiLeft + 143, this.guiTop + 23, 16, 47);
	}

}
