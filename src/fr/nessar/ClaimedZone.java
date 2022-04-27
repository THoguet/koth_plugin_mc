package fr.nessar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ClaimedZone {

	private List<String> playerList;

	private double xStart, zStart, xEnd, zEnd;

	private boolean active;

	private String name, world;

	public ClaimedZone(Location minimal, Location maximal, String name) {
		try {
			if (minimal.getX() > maximal.getX()) {
				this.xStart = maximal.getX();
				this.xEnd = minimal.getX();
			} else {
				this.xStart = minimal.getX();
				this.xEnd = maximal.getX();
			}
			if (minimal.getZ() > maximal.getZ()) {
				this.zStart = maximal.getZ();
				this.zEnd = minimal.getZ();
			} else {
				this.zStart = minimal.getZ();
				this.zEnd = maximal.getZ();
			}
			this.world = minimal.getWorld().getName();
			this.name = name;
			this.active = false;
			this.playerList = new ArrayList<>();
		} catch (Exception e) {
			System.out.println("null pointer new ClaimedZone");
		}
	}

	public List<String> getPlayerList() {
		return playerList;
	}

	public boolean isPlayerInZone(Player p) {
		for (int i = 0; i < playerList.size(); i++) {
			if (playerList.get(i).equals(p.getName())) {
				return true;
			}
		}
		return false;
	}

	public String getFirstPlayer() {
		if (playerList.size() == 0) {
			return null;
		}
		return playerList.get(0);
	}

	public boolean addPlayer(String pName) {
		if (pName != null) {
			playerList.add(pName);
			return true;
		}
		return false;
	}

	public boolean deletePlayer(String pName) {
		if (pName != null) {
			for (int i = 0; i < playerList.size(); i++) {
				if (playerList.get(i).equals(pName)) {
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

	public void setActive(boolean x) {
		this.active = x;
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
