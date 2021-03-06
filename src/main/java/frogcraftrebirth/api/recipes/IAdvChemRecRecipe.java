/**
 * This file is a part of FrogCraftRebirth, 
 * created by U_Knowledge at 9:26:11 PM, Mar 12, 2016, EST
 * FrogCraftRebirth, is open-source under MIT license,
 * check https://github.com/FrogCraft-Rebirth/
 * FrogCraft-Rebirth/LICENSE_FrogCraft_Rebirth for 
 * more information.
 */
package frogcraftrebirth.api.recipes;

import java.util.Collection;

import frogcraftrebirth.api.OreStack;
import net.minecraft.item.ItemStack;

public interface IAdvChemRecRecipe {

	Collection<OreStack> getInputs();

	Collection<ItemStack> getOutputs();

	ItemStack getCatalyst();

	int getTime();

	int getEnergyRate();

	default int getRequiredCellAmount() {
		return 0;
	}

	default int getProducedCellAmount() {
		return 0;
	}

	default boolean equals(IAdvChemRecRecipe rec) {
		if (rec.getInputs().equals(getInputs()))
			return true;
		else
			return false;
	}

	default boolean matchInputs(ItemStack... stacks) {
		for (OreStack ore : this.getInputs()) {
			boolean match = false;
			for (ItemStack stack : stacks) {
				if (stack == null)
					continue;
				if (ore.consumable(stack)) {
					match = true;
					break;
				}
			}
			if (!match)
				return false;
		}

		return true;
	}

}
