/**
 * This file is a part of FrogCraftRebirth, 
 * created by 3TUSK at 1:41:04 PM, Jul 25, 2016, 
 * FrogCraftRebirth, is open-source under MIT license,
 * check https://github.com/FrogCraft-Rebirth/
 * FrogCraft-Rebirth/LICENSE_FrogCraft_Rebirth for 
 * more information.
 */
package frogcraftrebirth.api.mps;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import frogcraftrebirth.api.FrogAPI;
import net.minecraft.item.ItemStack;

public enum MPSUpgradeManager {
	
	INSTANCE;
	
	private Set<ItemStack> validSolarUpgrades = new LinkedHashSet<ItemStack>();
	
	private Map<ItemStack, Integer> validStorageUpgrades = new LinkedHashMap<ItemStack, Integer>();
	
	private Map<ItemStack, Integer> validVoltageUpgrades = new LinkedHashMap<ItemStack, Integer>();
	
	public boolean isSolarUpgradeValid(ItemStack stack) { 
		if (stack == null || stack.stackSize == 0)
			return false;
		for (ItemStack aStack : validSolarUpgrades) {
			if (aStack.isItemEqual(stack))
				return true;
		}
		return false;
	}
	
	public int getEnergyStoreIncreasementFrom(ItemStack stack) {
		if (stack == null || stack.stackSize == 0)
			return 0;
		for (Map.Entry<ItemStack, Integer> entry : validStorageUpgrades.entrySet()) {
			if (entry.getKey().isItemEqual(stack)) {
				int increasement = stack.stackSize / entry.getKey().stackSize * entry.getValue();
				return increasement;
			}
		}
		return 0;
	}
	
	public int getVoltageIncreasementFrom(ItemStack stack) {
		if (stack == null || stack.stackSize == 0)
			return 0;
		for (Map.Entry<ItemStack, Integer> entry : validVoltageUpgrades.entrySet()) {
			if (entry.getKey().isItemEqual(stack)) {
				int increasement = stack.stackSize / entry.getKey().stackSize * entry.getValue();
				return increasement;
			}
		}
		return 0;
	}
	
	/**
	 * @param stack The upgrade item being registered
	 * @return true if succeed, false if fail
	 */
	public boolean registerSolarUpgrade(ItemStack stack) {
		try {
			return validSolarUpgrades.add(stack);
		} catch (Exception e) {
			FrogAPI.FROG_LOG.error("Failed to register " + stack.toString() + " as valid MPS solar upgrade");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * @param stack The upgrade item being registered
	 * @param incrQuantity The quantity increased of energy storage, measured in EU
	 * @return true if succeed, false if fail
	 */
	public boolean registerStorageUpgrade(ItemStack stack, int incrQuantity) {
		try {
			validStorageUpgrades.put(stack, incrQuantity);
			return true;
		} catch (Exception e) {
			FrogAPI.FROG_LOG.error("Failed to register " + stack.toString() + " as valid MPS storage upgrade");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * @param stack The upgrade item being registered
	 * @param incrQuantity The quantity increased of voltage level. 1 is LV, 2 is MV, 3 is HV, and so on.
	 * @return true if succeed, false if fail
	 * @See {@link ic2.api.energy.tile.IEnergySource#getSourceTier()}
	 */
	public boolean registerVoltageUpgrades(ItemStack stack, int incrQuantity) {
		try {
			validVoltageUpgrades.put(stack, incrQuantity);
			return true;
		} catch (Exception e) {
			FrogAPI.FROG_LOG.error("Failed to register " + stack.toString() + " as valid MPS voltage upgrade");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Clear all existed registry info.
	 * @return true if all registration info is cleared.
	 */
	public boolean resetRegistry() {
		validSolarUpgrades.clear();
		validStorageUpgrades.clear();
		validVoltageUpgrades.clear();
		return true;
	}
}
