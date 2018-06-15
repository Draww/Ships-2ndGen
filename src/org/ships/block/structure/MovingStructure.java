package org.ships.block.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.ships.block.MovingBlock;
import org.ships.block.blockhandler.BlockHandler;
import org.ships.block.blockhandler.BlockPriority;

public class MovingStructure implements ShipsStructure {

	List<MovingBlock> blocks = new ArrayList<>();
	
	public MovingStructure(Collection<MovingBlock> blocks) {
		this.blocks.addAll(blocks);
	}
	
	public Set<MovingBlock> getPriorityMovingBlocks() {
		return blocks.stream().filter(h -> h.getHandle().getPriority().equals(BlockPriority.ATTACHABLE)).collect(Collectors.toSet());
	}
	
	public Set<MovingBlock> getSpecialMovingBlocks(){
		return blocks.stream().filter(h -> h.getHandle().getPriority().equals(BlockPriority.SPECIAL)).collect(Collectors.toSet());
	}
	
	public Set<MovingBlock> getStandardMovingBlocks(){
		return blocks.stream().filter(h -> h.getHandle().getPriority().equals(BlockPriority.DEFAULT)).filter(e -> (!e.getBlock().getType().equals(Material.AIR))).collect(Collectors.toSet()); 
	}
	
	public Set<MovingBlock> getMovingBlocks(){
		return new HashSet<>(blocks);
	}

	@Override
	public Set<BlockHandler<? extends BlockState>> getAllBlocks() {
		List<BlockHandler<? extends BlockState>> list = new ArrayList<>();
		blocks.stream().forEach(b -> list.add(b.getHandle()));
		return new HashSet<>(list);
	}
}
