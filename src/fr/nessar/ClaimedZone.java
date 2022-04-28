package fr.nessar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ClaimedZone {

	private transient List<Player> playerList;

	private transient boolean active;

	private String name, world;

	private double xStart, zStart, xEnd, zEnd;

	public ClaimedZone(Location minimal, Location maximal, String name) throws Exception {
		if (!minimal.getWorld().equals(maximal.getWorld()))
			throw new Exception("The two locations not on the same world");
		this.xStart = minimal.getX();
		this.zStart = minimal.getZ();
		this.xEnd = maximal.getX();
		this.zEnd = maximal.getZ();
		this.world = minimal.getWorld().getName();
		this.name = name;
		this.active = false;
		this.playerList = new ArrayList<>();
	}

	public ClaimedZone() {
		this.active = false;
		this.playerList = new ArrayList<>();
	}

	public List<Player> getPlayerList() {
		return playerList;
	}

	public boolean isPlayerInZone(Player p) {
		for (int i = 0; i < playerList.size(); i++) {
			if (playerList.get(i).getDisplayName().equals(p.getDisplayName())) {
				return true;
			}
		}
		return false;
	}

	public Player getFirstPlayer() {
		if (playerList.size() == 0) {
			return null;
		}
		return playerList.get(0);
	}

	public String getFirstPlayerDisplayName() {
		if (playerList.size() == 0)
			return null;
		return playerList.get(0).getDisplayName();
	}

	public boolean addPlayer(Player p) {
		if (p != null) {
			playerList.add(p);
			return true;
		}
		return false;
	}

	public boolean deletePlayer(Player p) {
		if (p != null) {
			for (int i = 0; i < playerList.size(); i++) {
				if (playerList.get(i).getDisplayName().equals(p.getDisplayName())) {
					playerList.remove(i);
					return true;
				}
			}
		}
		return false;
	}

	public String getName() {
		return this.name;
	}

	public String getWorld() {
		return world;
	}

	public double getXStart() {
		return xStart;
	}

	public double getZStart() {
		return zStart;
	}

	public double getXEnd() {
		return xEnd;
	}

	public double getZEnd() {
		return zEnd;
	}

	public void start() {
		this.active = true;
	}

	public void stop() {
		this.active = false;
	}

	public boolean isInZone(Location loc) {
		return (loc.getWorld().getName().equals(this.world) && loc.getX() >= this.xStart && loc.getX() <= this.xEnd + 1
				&& loc.getZ() >= this.zStart
				&& loc.getZ() <= this.zEnd + 1);
	}

	public boolean isActive() {
		return this.active;
	}
}
