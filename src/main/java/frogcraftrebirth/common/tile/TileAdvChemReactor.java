package frogcraftrebirth.common.tile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import frogcraftrebirth.api.FrogAPI;
import frogcraftrebirth.api.OreStack;
import frogcraftrebirth.api.recipes.IAdvChemRecRecipe;
import frogcraftrebirth.client.gui.GuiAdvChemReactor;
import frogcraftrebirth.client.gui.GuiTileFrog;
import frogcraftrebirth.common.gui.ContainerAdvChemReactor;
import frogcraftrebirth.common.gui.ContainerTileFrog;
import frogcraftrebirth.common.lib.capability.ItemHandlerInputWrapper;
import frogcraftrebirth.common.lib.capability.ItemHandlerOutputWrapper;
import frogcraftrebirth.common.lib.tile.TileEnergySink;
import frogcraftrebirth.common.lib.tile.TileFrog;
import frogcraftrebirth.common.lib.util.ItemUtil;
import ic2.api.item.IC2Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class TileAdvChemReactor extends TileEnergySink implements IHasGui, IHasWork, ITickable {
	
	//0 for module, 1-5 for input, 6-10 for output, 11 for cell input and 12 for cell output
	public final IItemHandler module = new ItemStackHandler();
	public final IItemHandler input = new ItemStackHandler(5);
	public final IItemHandler output = new ItemStackHandler(5);
	public final IItemHandler cellInput = new ItemStackHandler();
	public final IItemHandler cellOutput = new ItemStackHandler();
	
	public int process, processMax;
	private boolean working;
	private boolean requireRefresh;
	private IAdvChemRecRecipe recipe;
	
	public TileAdvChemReactor() {
		super(2, 50000);
	}
	
	@Override
	public boolean isWorking() {
		return working;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.working = tag.getBoolean("working");
		this.process = tag.getInteger("process");
		this.processMax = tag.getInteger("processMax");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("working", this.working);
		tag.setInteger("process", this.process);
		tag.setInteger("processMax", this.processMax);
		return super.writeToNBT(tag);
	}
	
	@Override
	public void readPacketData(DataInputStream input) throws IOException {
		super.readPacketData(input);
		this.process = input.readInt();
		this.processMax = input.readInt();
		this.working = input.readBoolean();
	}
	
	@Override
	public void writePacketData(DataOutputStream output) throws IOException {
		super.writePacketData(output);
		output.writeInt(process);
		output.writeInt(processMax);
		output.writeBoolean(working);
	}
	
	@Override
	public void update() {
		if (getWorld().isRemote) {
			if (requireRefresh) {
				getWorld().markBlockRangeForRenderUpdate(getPos(), getPos());
				requireRefresh = false;
			}
			return;
		}
		
		if (!working || recipe == null) {
			ItemStack[] inputs = new ItemStack[] {input.getStackInSlot(0), input.getStackInSlot(1), input.getStackInSlot(2), input.getStackInSlot(3), input.getStackInSlot(4)};
			recipe = FrogAPI.managerACR.getRecipe(inputs);
			
			if (checkRecipe(recipe)) {
				this.consumeIngredient(recipe.getInputs());
				this.process = 0;
				this.processMax = recipe.getTime();
				this.working = true;
				this.requireRefresh = true;
			} else {
				this.working = false;
				this.sendTileUpdatePacket(this);
				this.markDirty();
				this.requireRefresh = true;
				return;
			}
		}
		
		if (recipe != null && charge >= recipe.getEnergyRate()) {
			this.charge -= recipe.getEnergyRate();
			++process;
		}
		
		if (recipe != null && process == processMax) {
			this.produce();
			this.process = 0;
			this.processMax = 0;
			this.recipe = null;
		}
		
		this.sendTileUpdatePacket(this);
		this.markDirty();
	}
	
	private final List<ItemStack> checkCache = new ArrayList<ItemStack>();
	private boolean checkRecipe(IAdvChemRecRecipe recipe) {
		if (recipe == null)
			return false;
		if (cellInput.getStackInSlot(0) != null) {
			if (!IC2Items.getItem("fluid_cell").isItemEqual(cellInput.getStackInSlot(0)))
				return false;
			if (cellInput.getStackInSlot(0).stackSize < recipe.getRequiredCellAmount())
				return false;
		} else {
			if (recipe.getRequiredCellAmount() > 0)
				return false;
		}
		if (recipe.getCatalyst() != null && !recipe.getCatalyst().isItemEqual(module.getStackInSlot(0)))
			return false;
		checkCache.clear();
		recipe.getOutputs().forEach(outputStack -> checkCache.add(ItemHandlerHelper.insertItemStacked(output, outputStack.copy(), true)));
		checkCache.removeIf(stack -> stack == null);
		return checkCache.size() == 0;
	}
	
	private void consumeIngredient(Collection<OreStack> toBeConsumed) {
		toBeConsumed.forEach(ore -> {
			for (int i = 0; i < 5; i++) {
				if (ore.consumable(input.getStackInSlot(i))) {
					input.extractItem(i, ore.getAmount(), false);
					return;
				}
			}
		});
		if (recipe.getRequiredCellAmount() > 0) {
			cellInput.extractItem(0, recipe.getRequiredCellAmount(), false);
		}
	}
	
	private final List<ItemStack> dropCache = new ArrayList<>();
	
	private void produce() {
		recipe.getOutputs().forEach(itemStack -> dropCache.add(ItemHandlerHelper.insertItemStacked(output, itemStack.copy(), false)));
		dropCache.stream().filter(stack -> stack != null).forEach(stack -> ItemUtil.dropItemStackAsEntityInsanely(getWorld(), getPos(), stack));
		dropCache.clear();
		
		if (recipe.getProducedCellAmount() > 0) {
			if (cellOutput.getStackInSlot(0) != null) {
				ItemStack cell = cellOutput.getStackInSlot(0).copy();
				cell.stackSize = recipe.getProducedCellAmount();
				ItemStack remain = cellOutput.insertItem(0, cell, false);
				if (remain != null)
					ItemUtil.dropItemStackAsEntityInsanely(getWorld(), getPos(), remain);
			} else {
				ItemStack stack = IC2Items.getItem("fluid_cell");
				stack.stackSize = recipe.getProducedCellAmount();
				cellOutput.insertItem(0, stack, false);
			}
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? true : super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			switch (facing) {
			case UP:
				return (T)new ItemHandlerInputWrapper(input);
			case DOWN:
				return (T)new ItemHandlerOutputWrapper(output);
			case NORTH:
			case EAST:
				return (T)new ItemHandlerInputWrapper(cellInput);
			case SOUTH:
			case WEST:
				return (T)new ItemHandlerOutputWrapper(cellOutput);
			default:
				break;
			}
		}	
		return super.getCapability(capability, facing);
	}

	@Override
	public ContainerTileFrog<? extends TileFrog> getGuiContainer(World world, EntityPlayer player) {
		return new ContainerAdvChemReactor(player.inventory, this);
	}

	@Override
	public GuiTileFrog<? extends TileFrog, ? extends ContainerTileFrog<? extends TileFrog>> getGui(World world, EntityPlayer player) {
		return new GuiAdvChemReactor(player.inventory, this);
	}

}
