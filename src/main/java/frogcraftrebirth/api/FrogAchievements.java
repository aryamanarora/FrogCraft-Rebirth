/**
 * This file is a part of FrogCraftRebirth, 
 * created by 3TUSK at 3:28:45 PM, Mar 13, 2016, EST
 * FrogCraftRebirth, is open-source under MIT license,
 * check https://github.com/FrogCraft-Rebirth/
 * FrogCraft-Rebirth/LICENSE_FrogCraft_Rebirth for 
 * more information.
 */
package frogcraftrebirth.api;

import java.lang.reflect.Field;

import javax.annotation.Nullable;

import net.minecraft.stats.Achievement;

public enum FrogAchievements {

	EVT, RAILGUN, POTASSIUM, GAS_PUMP, LIQUEFIER, HSU, UHSU, ADV_CHEM_REACTOR, JINKELA, CONDENSE_TOWER_CORE, CONDENSE_TOWER, NITRIC_ACID;

	@Nullable
	public Achievement get() {
		Field achievement;
		try {
			achievement = Class.forName("frogcraftrebirth.common.registry.RegFrogAchievements").getDeclaredField(this.name());
			achievement.setAccessible(true);
			return (Achievement) achievement.get(null);
		} catch (Exception e) {
			return null;
		}

	}

}
