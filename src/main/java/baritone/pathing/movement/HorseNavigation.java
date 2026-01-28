/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.pathing.movement;

import baritone.api.BaritoneAPI;
import baritone.api.IBaritone;
import baritone.api.utils.BetterBlockPos;
import baritone.api.utils.IPlayerContext;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;

/**
 * Navigation logic for horse travel.
 * Handles pathfinding calculations and movement when assumeHorse setting is enabled.
 */
public class HorseNavigation {

    private final IBaritone baritone;
    private final IPlayerContext ctx;

    /**
     * Base speed multiplier for horse travel (horses are faster than walking)
     */
    private static final double HORSE_SPEED_MULTIPLIER = 2.5;

    /**
     * Cost reduction factor when using horses (lower cost = faster pathfinding)
     */
    private static final double HORSE_COST_REDUCTION = 0.4;

    public HorseNavigation(IBaritone baritone) {
        this.baritone = baritone;
        this.ctx = baritone.getPlayerContext();
    }

    /**
     * Check if horse navigation is enabled and player is riding a horse
     */
    public boolean isHorseNavigationEnabled() {
        return BaritoneAPI.getSettings().assumeHorse.value && ctx.player().getVehicle() instanceof Horse;
    }

    /**
     * Check if player can use horse navigation (has horse nearby or is riding one)
     */
    public boolean canUseHorseNavigation() {
        if (!BaritoneAPI.getSettings().assumeHorse.value) {
            return false;
        }
        // Check if player is currently riding a horse
        if (ctx.player().getVehicle() instanceof Horse) {
            return true;
        }
        // Could also check for nearby horses, but for now just check if riding
        return false;
    }

    /**
     * Calculate movement cost adjustment for horse travel
     * Horses move faster, so the cost should be reduced
     */
    public double getHorseCostMultiplier() {
        if (!canUseHorseNavigation()) {
            return 1.0;
        }
        return HORSE_COST_REDUCTION;
    }

    /**
     * Get the effective speed multiplier when using a horse
     */
    public double getHorseSpeedMultiplier() {
        if (!canUseHorseNavigation()) {
            return 1.0;
        }
        return HORSE_SPEED_MULTIPLIER;
    }

    /**
     * Check if a path segment can benefit from horse navigation
     * Horses are better for longer, flatter paths
     */
    public boolean isPathSuitableForHorse(BetterBlockPos start, BetterBlockPos end) {
        if (!canUseHorseNavigation()) {
            return false;
        }
        // Horses are better for horizontal movement
        int horizontalDistance = Math.abs(start.x - end.x) + Math.abs(start.z - end.z);
        int verticalDistance = Math.abs(start.y - end.y);
        // Prefer paths with more horizontal movement than vertical
        return horizontalDistance > verticalDistance * 2;
    }

    /**
     * Calculate adjusted movement cost for horse navigation
     */
    public double adjustMovementCostForHorse(double baseCost, BetterBlockPos start, BetterBlockPos end) {
        if (!isPathSuitableForHorse(start, end)) {
            return baseCost;
        }
        return baseCost * getHorseCostMultiplier();
    }
}
