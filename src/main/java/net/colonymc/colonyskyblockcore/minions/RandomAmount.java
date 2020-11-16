package net.colonymc.colonyskyblockcore.minions;

public class RandomAmount {
	
	final int max;
	final int chanceToDrop;
	
	public RandomAmount(int chanceToDropOne, int max) {
		this.max = max;
		this.chanceToDrop = chanceToDropOne;
	}
	
	public int getRandomAmount() {
		int amount = 0;
		while(amount == 0) {
			for(int i = 0; i < max; i++) {
				if(Math.random() * 100 < chanceToDrop) {
					 amount++;
				}
			}
		}
		return amount;
	}

}
