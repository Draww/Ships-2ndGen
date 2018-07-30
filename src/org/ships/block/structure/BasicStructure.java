package org.ships.block.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.block.BlockState;
import org.ships.block.blockhandler.BlockHandler;

public class BasicStructure implements ShipsStructure {
	List<BlockHandler<? extends BlockState>> handlers = new ArrayList<BlockHandler<? extends BlockState>>();

	public BasicStructure() {
	}

	public BasicStructure(Collection<BlockHandler<? extends BlockState>> collection) {
		this.handlers.addAll(collection);
	}

	@Override
	public Set<BlockHandler<? extends BlockState>> getAllBlocks() {
		return new HashSet<BlockHandler<? extends BlockState>>(this.handlers);
	}
}