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
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Navigation logic for boat travel.
 * Handles pathfinding calculations and movement when assumeBoat setting is enabled.
 */
public class BoatNavigation {

    private final IBaritone baritone;
    private final IPlayerContext ctx;

    /**
     * Base speed multiplier for boat travel (boats are faster on water)
     */
    private static final double BOAT_SPEED_MULTIPLIER = 3.0;

    /**
     * Cost reduction factor when using boats on water (lower cost = faster pathfinding)
     */
    private static final double BOAT_COST_REDUCTION = 0.3;

    /**
     * Cost penalty for boat travel on land (boats are slower on land)
     */
    private static final double BOAT_LAND_PENALTY = 2.0;

    public BoatNavigation(IBaritone baritone) {
        this.baritone = baritone;
        this.ctx = baritone.getPlayerContext();
    }

    /**
     * Check if boat navigation is enabled and player is in a boat
     */
    public boolean isBoatNavigationEnabled() {
        return BaritoneAPI.getSettings().assumeBoat.value && ctx.player().getVehicle() instanceof Boat;
    }

    /**
     * Check if player can use boat navigation (has boat nearby or is in one)
     */
    public boolean canUseBoatNavigation() {
        if (!BaritoneAPI.getSettings().assumeBoat.value) {
            return false;
        }
        // Check if player is currently in a boat
        if (ctx.player().getVehicle() instanceof Boat) {
            return true;
        }
        // Could also check for nearby boats, but for now just check if in boat
        return false;
    }

    /**
     * Check if a position is suitable for boat travel (on water)
     */
    public boolean isPositionOnWater(BetterBlockPos pos) {
        FluidState fluidState = ctx.world().getFluidState(pos);
        return fluidState.getType() == Fluids.WATER || fluidState.getType() == Fluids.FLOWING_WATER;
    }

    /**
     * Calculate movement cost adjustment for boat travel
     * Boats are faster on water, slower on land
     */
    public double getBoatCostMultiplier(BetterBlockPos start, BetterBlockPos end) {
        if (!canUseBoatNavigation()) {
            return 1.0;
        }
        boolean startOnWater = isPositionOnWater(start);
        boolean endOnWater = isPositionOnWater(end);
        
        // If both positions are on water, boats are very efficient
        if (startOnWater && endOnWater) {
            return BOAT_COST_REDUCTION;
        }
        // If on land, boats are slower
        if (!startOnWater && !endOnWater) {
            return BOAT_LAND_PENALTY;
        }
        // Mixed water/land - moderate cost
        return 1.0;
    }

    /**
     * Get the effective speed multiplier when using a boat
     */
    public double getBoatSpeedMultiplier(BetterBlockPos start, BetterBlockPos end) {
        if (!canUseBoatNavigation()) {
            return 1.0;
        }
        boolean startOnWater = isPositionOnWater(start);
        boolean endOnWater = isPositionOnWater(end);
        
        if (startOnWater && endOnWater) {
            return BOAT_SPEED_MULTIPLIER;
        }
        // On land, boats are slower than walking
        if (!startOnWater && !endOnWater) {
            return 0.5;
        }
        return 1.0;
    }

    /**
     * Check if a path segment can benefit from boat navigation
     * Boats are best for water-based paths
     */
    public boolean isPathSuitableForBoat(BetterBlockPos start, BetterBlockPos end) {
        if (!canUseBoatNavigation()) {
            return false;
        }
        // Boats are best when traveling on or near water
        return isPositionOnWater(start) || isPositionOnWater(end);
    }

    /**
     * Calculate adjusted movement cost for boat navigation
     */
    public double adjustMovementCostForBoat(double baseCost, BetterBlockPos start, BetterBlockPos end) {
        if (!isPathSuitableForBoat(start, end)) {
            return baseCost;
        }
        return baseCost * getBoatCostMultiplier(start, end);
    }
}
