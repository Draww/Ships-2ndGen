package org.ships.event.custom;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import MoseShipsBukkit.StillShip.Vessel.Ship;

public class ShipCreateEvent extends Event implements Cancellable {

	Player PLAYER;
	Sign SIGN;
	Ship VESSEL;
	boolean CANCELLED;
	static final HandlerList HANDLERS = new HandlerList();

	public ShipCreateEvent(Player player, Sign sign, Ship vessel) {
		PLAYER = player;
		SIGN = sign;
		VESSEL = vessel;
	}

	public Player getPlayer() {
		return PLAYER;
	}

	public Sign getSign() {
		return SIGN;
	}

	public Ship getVessel() {
		return VESSEL;
	}

	@Override
	public boolean isCancelled() {
		return CANCELLED;
	}

	@Override
	public void setCancelled(boolean arg0) {
		CANCELLED = arg0;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
