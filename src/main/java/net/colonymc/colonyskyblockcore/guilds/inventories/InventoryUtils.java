package net.colonymc.colonyskyblockcore.guilds.inventories;

import java.util.concurrent.TimeUnit;

public class InventoryUtils {
	
	public static String getDurationString(long duration) {
		String durationString = null;
		if(duration == -1) {
			durationString = "0s";
			return durationString;
		}
		if(TimeUnit.MILLISECONDS.toDays(duration) > 0) {
			durationString = String.format("%dd, %dh, %dm, %ds",
					TimeUnit.MILLISECONDS.toDays(duration),
					TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration)),
					TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
					TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
					);
			
		}
		if(TimeUnit.MILLISECONDS.toDays(duration) == 0) {
			durationString = String.format("%dh, %dm, %ds", 
					TimeUnit.MILLISECONDS.toHours(duration),
					TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
					TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
					);
		}
		if(TimeUnit.MILLISECONDS.toHours(duration) == 0) {
			durationString = String.format("%dm, %ds", 
					TimeUnit.MILLISECONDS.toMinutes(duration),
					TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
					);
		}
		if(TimeUnit.MILLISECONDS.toMinutes(duration) == 0) {
			durationString = String.format("%ds", 
					TimeUnit.MILLISECONDS.toSeconds(duration)
					);
			
		}
		return durationString;
	}

}
