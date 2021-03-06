package frogcraftrebirth.common.lib;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import frogcraftrebirth.api.IFrogNetworkObject;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
/**
 * Create a instance of this class will provide a efficient way to manage fluid.
 * @author 3TUSK
 */
public class FrogFluidTank implements IFluidTank, IFluidHandler, IFrogNetworkObject {

	private final int capacity;
	private FluidStack fluidInv;
	/** The name used for NBT tag. It is existed to prevent tag conflict.*/
	private final String tankName;
	
	public FrogFluidTank (int capacity) {
		this.capacity = capacity;
		this.tankName = "tank";
	}
	
	public FrogFluidTank (int capacity, String tagName) {
		this.capacity = capacity;
		this.tankName = tagName;
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		this.fluidInv = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag(tankName));
	}
	
	public void writeToNBT(NBTTagCompound tag) {
		NBTTagCompound newTag = new NBTTagCompound();
		if (fluidInv != null) 
			tag.setTag(tankName, fluidInv.writeToNBT(newTag));
	}
	
	@Override
	public FluidStack getFluid() {
		return fluidInv != null ? fluidInv.copy() : null;
	}

	@Override
	public int getFluidAmount() {
		return fluidInv != null ? fluidInv.amount : 0;
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public FluidTankInfo getInfo() {
		return new FluidTankInfo(this);
	}
	
	@Override
	public IFluidTankProperties[] getTankProperties() {
		return FluidTankProperties.convert(new FluidTankInfo[] { getInfo() });
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (resource == null)
			return 0;
		
		if (this.fluidInv == null) {
			if (doFill)
				this.fluidInv = resource;
			
			return resource.amount;
		}
		
		if (this.fluidInv.isFluidEqual(resource)) {
			int newAmount = this.fluidInv.amount + resource.amount;
			if (doFill) {
				if (newAmount > capacity)
					fluidInv.amount = capacity;
				else
					fluidInv.amount = newAmount;
			}		
			return resource.amount - newAmount + capacity;
		} else 	
			return 0;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {	
		if (this.fluidInv == null) 
			return null;
		
		if (this.fluidInv.amount <= maxDrain) {
			FluidStack drained = this.fluidInv.copy();
			if (doDrain)
				this.fluidInv = null;
			return drained;
		} else {
			if (doDrain) {
				this.fluidInv.amount -= maxDrain;
				if (fluidInv.amount <= 0)
					fluidInv = null;
			}
			return new FluidStack(this.fluidInv.getFluid(), maxDrain);
		}
	}
	
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (resource.isFluidEqual(fluidInv)) {
			return drain(resource.amount, doDrain);
		} else {
			return null;
		}
	}
	
	public void writePacketData(DataOutputStream output) throws IOException {
		output.writeUTF(fluidInv != null ? FluidRegistry.getFluidName(fluidInv) : "EmptyTank");
		output.writeInt(getFluidAmount());
	}
	
	@SideOnly(Side.CLIENT)
	public void readPacketData(DataInputStream input) throws IOException {
		String fluidID = input.readUTF();
		int fluidAmount = input.readInt();
		
		if (fluidID.equals("EmptyTank")) {
			this.forceDrainTank();
			return;
		}
		
		Fluid fluid = FluidRegistry.getFluid(fluidID);
		if (fluid != null)
			this.forceFillTank(new FluidStack(fluid, fluidAmount));
	}
	
	public boolean isFull() {
		return fluidInv == null ? false : fluidInv.amount >= this.capacity;
	}
	
	/**
	 * Only use it on client side for forcing update.
	 */
	@SideOnly(Side.CLIENT)
	public void forceFillTank(FluidStack stack) {
		if (stack != null)
			this.fluidInv = stack.copy();
	}
	
	/**
	 * Only use it on client side for forcing update.
	 */
	@SideOnly(Side.CLIENT)
	public void forceDrainTank() {
		this.fluidInv = null;
	}

}
