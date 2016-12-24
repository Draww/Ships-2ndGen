package MoseShipsSponge.Ships.Movement.MovingBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.type.TileEntityInventory;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import MoseShipsSponge.Causes.ShipsCause;

import MoseShipsSponge.Configs.Files.BlockList;
import MoseShipsSponge.Ships.Movement.MovementType;
import MoseShipsSponge.Ships.Movement.Collide.CollideType;
import MoseShipsSponge.Ships.Movement.Movement.Rotate;

public class MovingBlock {

	Location<World> ORIGIN;
	Location<World> MOVING_TO;
	BlockSnapshot STATE;

	TileEntityInventory<TileEntityCarrier> INVENTORY;

	public MovingBlock(Location<World> original, Location<World> moving) {
		ORIGIN = original;
		MOVING_TO = moving;
		STATE = original.createSnapshot();
	}

	public MovingBlock(Location<World> original, int X, int Y, int Z) {
		ORIGIN = original;
		MOVING_TO = original.add(X, Y, Z);
		STATE = original.createSnapshot();
	}

	public MovingBlock(Location<World> original, Vector3i vector) {
		ORIGIN = original;
		MOVING_TO = original.add(vector.toDouble());
		STATE = original.createSnapshot();
	}

	public Location<World> getOrigin() {
		return ORIGIN;
	}

	public Location<World> getMovingTo() {
		return MOVING_TO;
	}
	
	public BlockType getType(){
		return STATE.getState().getType();
	}
	
	public BlockState getState(){
		return STATE.getState();
	}

	public MovementType getMovementType() {
		if ((ORIGIN.getBlockX() == MOVING_TO.getBlockX()) && (ORIGIN.getBlockZ() == MOVING_TO.getBlockZ())) {
			return MovementType.DIRECTIONAL;
		} else if ((ORIGIN.getBlockX() > MOVING_TO.getBlockX()) || (ORIGIN.getBlockZ() > MOVING_TO.getBlockZ())) {
			return MovementType.ROTATE_RIGHT;
		} else {
			return MovementType.ROTATE_LEFT;
		}
	}

	public MovingBlock clearOriginalBlock(BlockChangeFlag flag, Cause cause) {
		clearBlock(ORIGIN, BlockTypes.AIR, flag, cause);
		return this;
	}

	public MovingBlock clearMovingToBlock(BlockChangeFlag flag, Cause cause) {
		clearBlock(MOVING_TO, BlockTypes.AIR, flag, cause);
		return this;
	}

	public MovingBlock move(BlockChangeFlag flag) {
		MOVING_TO.restoreSnapshot(STATE, true, flag, ShipsCause.BLOCK_MOVING.buildCause());
		return this;
	}

	public MovingBlock move(BlockChangeFlag flag, Cause cause) {
		MOVING_TO.restoreSnapshot(STATE, true, flag, cause);
		return this;
	}

	public MovingBlock replaceOriginalBlock(BlockType type, BlockChangeFlag flag, Cause cause) {
		clearBlock(ORIGIN, type, flag, cause);
		return this;
	}

	public MovingBlock replaceMovingToBlock(BlockType type, BlockChangeFlag flag, Cause cause) {
		clearBlock(MOVING_TO, type, flag, cause);
		return this;
	}

	public Priority getPriority() {
		return Priority.getType(STATE.getState().getType());
	}

	public CollideType getCollision(List<Location<World>> ignore, BlockState... ignore2) {
		if (ignore.contains(MOVING_TO.getBlock())) {
			return CollideType.COLLIDE_WITH_SELF;
		} else if (Arrays.asList(ignore2).contains(MOVING_TO.getBlock())) {
			return CollideType.COLLIDE_WITH_IGNORED_TYPE;
		} else if (BlockList.BLOCK_LIST.contains(MOVING_TO.getBlock(),
				BlockList.ListType.MATERIALS)) {
			return CollideType.COLLIDE_WITH_MATERIAL;
		} else if (BlockList.BLOCK_LIST.contains(MOVING_TO.getBlock(),
				BlockList.ListType.RAM)) {
			return CollideType.RAM;
		} else {
			return CollideType.COLLIDE;
}
	}

	public MovingBlock rotate(Rotate rotate, Location<World> centre) {
		switch (rotate) {
			case LEFT:
				return rotateLeft(centre);
			case RIGHT:
				return rotateRight(centre);
		}
		return this;
	}

	public MovingBlock rotateLeft(Location<World> centre) {
		rotate(true);
		int shift = centre.getBlockX() - centre.getBlockZ();
		double symmetry = centre.getBlockX();

		double X = MOVING_TO.getX() - (MOVING_TO.getX() - symmetry) * 2.0D - shift;
		double Y = MOVING_TO.getY();
		double Z = MOVING_TO.getZ() + shift;
		MOVING_TO.setPosition(new Vector3d(X, Y, Z));
		return this;
	}

	public MovingBlock rotateRight(Location<World> centre) {
		rotate(false);
		int shift = centre.getBlockX() - centre.getBlockZ();
		double symmetry = centre.getBlockZ();

		double X = MOVING_TO.getX() - shift;
		double Y = MOVING_TO.getY();
		double Z = MOVING_TO.getZ() - (MOVING_TO.getZ() - symmetry) * 2.0 + shift;
		MOVING_TO.setPosition(new Vector3d(X, Y, Z));
		return this;
	}

	private void clearBlock(Location<World> loc, BlockType type, BlockChangeFlag flag, Cause cause) {
		Optional<TileEntity> opTile = loc.getTileEntity();
		if (opTile.isPresent()) {
			TileEntity entity = opTile.get();
			if (entity instanceof TileEntityCarrier) {
				TileEntityCarrier tile = (TileEntityCarrier) entity;
				TileEntityInventory<TileEntityCarrier> inv = tile.getInventory();
				inv.clear();
			}
		}
		loc.setBlockType(type, flag, cause);
	}

	private void rotate(boolean left) {
		Optional<Direction> opDir = STATE.get(Keys.DIRECTION);
		if (opDir.isPresent()) {
			Direction dir = opDir.get();
			switch (dir) {
				case EAST:
					if (left) {
						STATE.with(Keys.DIRECTION, Direction.NORTH);
						return;
					} else {
						STATE.with(Keys.DIRECTION, Direction.SOUTH);
						return;
					}
				case NORTH:
					if (left) {
						STATE.with(Keys.DIRECTION, Direction.WEST);
						return;
					} else {
						STATE.with(Keys.DIRECTION, Direction.EAST);
						return;
					}
				case SOUTH:
					if (left) {
						STATE.with(Keys.DIRECTION, Direction.EAST);
						return;
					} else {
						STATE.with(Keys.DIRECTION, Direction.WEST);
						return;
					}
				case WEST:
					if (left) {
						STATE.with(Keys.DIRECTION, Direction.SOUTH);
						return;
					} else {
						STATE.with(Keys.DIRECTION, Direction.NORTH);
						return;
					}
				default:
					return;
			}
		}
	}

	public static List<MovingBlock> setPriorityOrder(List<MovingBlock> blocks) {
		List<MovingBlock> normalList = blocks.stream().filter(b -> b.getPriority().equals(Priority.NORMAL)).collect(Collectors.toList());
		List<MovingBlock> airList = blocks.stream().filter(b -> b.getPriority().equals(Priority.AIR)).collect(Collectors.toList());
		List<MovingBlock> priList = blocks.stream().filter(b -> b.getPriority().equals(Priority.PRIORITY)).collect(Collectors.toList());
		List<MovingBlock> retList = new ArrayList<>();
		retList.addAll(normalList);
		retList.addAll(airList);
		retList.addAll(priList);
		return retList;
	}

	public enum Priority {
		NORMAL,
		PRIORITY,
		AIR;

		public static Priority getType(BlockType type) {
			if ((type.equals(BlockTypes.TORCH)) ||
					(type.equals(BlockTypes.FIRE)) ||
					(type.equals(BlockTypes.REDSTONE_WIRE)) ||
					(type.equals(BlockTypes.ACACIA_DOOR)) ||
					(type.equals(BlockTypes.BIRCH_DOOR)) ||
					(type.equals(BlockTypes.DARK_OAK_DOOR)) ||
					(type.equals(BlockTypes.IRON_DOOR)) ||
					(type.equals(BlockTypes.JUNGLE_DOOR)) ||
					(type.equals(BlockTypes.SPRUCE_DOOR)) ||
					(type.equals(BlockTypes.WOODEN_DOOR)) ||
					(type.equals(BlockTypes.LADDER)) ||
					(type.equals(BlockTypes.WALL_SIGN)) ||
					(type.equals(BlockTypes.LEVER)) ||
					(type.equals(BlockTypes.STONE_PRESSURE_PLATE)) ||
					(type.equals(BlockTypes.WOODEN_PRESSURE_PLATE)) ||
					(type.equals(BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE)) ||
					(type.equals(BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE)) ||
					(type.equals(BlockTypes.UNPOWERED_REPEATER)) ||
					(type.equals(BlockTypes.POWERED_REPEATER)) ||
					(type.equals(BlockTypes.UNPOWERED_COMPARATOR)) ||
					(type.equals(BlockTypes.POWERED_COMPARATOR)) ||
					(type.equals(BlockTypes.UNLIT_REDSTONE_TORCH)) ||
					(type.equals(BlockTypes.REDSTONE_TORCH)) ||
					(type.equals(BlockTypes.STONE_BUTTON)) ||
					(type.equals(BlockTypes.TRAPDOOR)) ||
					(type.equals(BlockTypes.TRIPWIRE_HOOK)) ||
					(type.equals(BlockTypes.TRIPWIRE)) ||
					(type.equals(BlockTypes.IRON_TRAPDOOR)) ||
					(type.equals(BlockTypes.WALL_BANNER))) {
				return PRIORITY;
			} else if (type.equals(BlockTypes.AIR)) {
				return AIR;
			} else {
				return NORMAL;
			}
		}
	}

}