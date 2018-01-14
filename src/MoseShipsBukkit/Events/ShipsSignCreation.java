package MoseShipsBukkit.Events;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class ShipsSignCreation extends Event implements Cancellable {

	boolean CANCELLED;
	JavaPlugin PLUGIN;
	Player PLAYER;
	String[] SIGNTEXT;
	String[] TEXT;
	String[] RETURN_TEXT;
	Sign SIGN;
	static HandlerList LIST = new HandlerList();

	public ShipsSignCreation(JavaPlugin plugin, String[] signText, Sign sign) {
		PLUGIN = plugin;
		SIGNTEXT = signText;
		SIGN = sign;
		RETURN_TEXT = signText;
	}

	public ShipsSignCreation(JavaPlugin plugin, String[] signText, Sign sign, Player player, String... playerText) {
		PLUGIN = plugin;
		SIGNTEXT = signText;
		SIGN = sign;

		PLAYER = player;
		TEXT = playerText;
		RETURN_TEXT = signText;
	}

	public JavaPlugin getCause() {
		return PLUGIN;
	}

	public Player getPlayer() {
		return PLAYER;
	}

	public String[] getSignTypeResult() {
		return SIGNTEXT;
	}

	public String[] getTypedText() {
		return TEXT;
	}

	public Sign getSign() {
		return SIGN;
	}

	public String[] getReturnText() {
		return RETURN_TEXT;
	}

	public ShipsSignCreation setReturnText(String... text) {
		RETURN_TEXT = text;
		return this;
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
		return LIST;
	}

	public static HandlerList getHandlerList() {
		return LIST;
	}

}
