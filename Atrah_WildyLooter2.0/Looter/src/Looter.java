package src;

import org.osbot.rs07.script.*;
import org.osbot.rs07.api.map.*;
import javax.swing.*;
import java.lang.reflect.*;
import org.osbot.rs07.api.map.constants.*;
import org.osbot.rs07.api.model.*;
import java.awt.*;

public class Looter extends Script {
	
	public int arrowsCollected = 0;
	public int lobstersCollected = 0;
	public int swordfishCollected = 0;
	public int anchoviesCollected = 0;
	public int arrowsPrice = 61;
	public int lobstersPrice = 170;
	public int swordfishPrice = 381;
	public int achoviesPrice = 900;
	public int inventoryLimit = 10000;
	public String muleName = "";
	public Position wildy = new Position(3087, 3536, 0);
	
	private long startTime;
	private long gpGained = 0;
	private GUI gui;
    private Walker walker;
    private boolean didTrade;
	private Area lumbridge = new Area(2891, 3482, 3441, 3170);
	private Position safeWildy = new Position(3086, 3523, 0);
	
	public Looter() {
		
	}

	@Override
	public void onStart() throws InterruptedException {
		gui = new GUI();
		walker = new Walker();
		startTime = System.currentTimeMillis();
		walker.exchangeContext(getBot());
		try {
			SwingUtilities.invokeAndWait(() -> gui.open());
		}
		catch (InterruptedException | InvocationTargetException ex){
			ex.printStackTrace();
			stop();
			return;
		}
		if(!gui.isStarted()) {
			stop();
			return;
		}
		muleName = gui.getVMuleValue();
	}

	@Override
	public final int onLoop() throws InterruptedException {
		try {
			if (lumbridge.contains(myPosition())) {
				log("Walking to wildy");
				getWalking().webWalk(Banks.EDGEVILLE);
			}
			else if (isUnderAttack()) {
				getWalking().walk(safeWildy);
				if(inventory.contains("Lobster") ||  inventory.contains("Lobster"))
					Eat();
			}
			else if (getMap().getWildernessLevel() > 5) {
				log("Getting lured but i'm a smart bot, walking to lower wilderness");
				getWalking().webWalk(wildy);
			}
			else if (getSettings().getRunEnergy() > 30 && !getSettings().isRunning()) {
				getSettings().setRunning(true);
				Sleep.sleepUntil(getSettings().isRunning(), 3000);
			}
			else if (freshInventory()) {
				bank();
				Sleep.sleepUntil(getInventory().isEmpty(), 60000);
			}
			else if (finishedLootingRun() || notDoneBanking()) {
                if (!trade.isCurrentlyTrading())
                	tradeMule();
            }
			else if (canCollect()) {
                lootItems();
            }
            else if (!trade.isCurrentlyTrading()){
                walker.moveToWild();
            }
        }
        catch (Exception e) {
            log(e.getMessage().toString());
        }
        return random(400);
	}
	
	private void bank() throws InterruptedException {
		log("trying to bank");
        if (!Banks.EDGEVILLE.contains(myPosition())) {
            log("Bank1");
            walker.moveToBank();
        }
        else if (!getBank().isOpen()) {
            log("Bank2");
            final boolean success = getBank().open();
            Sleep.sleepUntil(success, 3000);
        }
        if (getBank().isOpen()) {
            log("Bank3");
            AdsUpInventory();
            final long currtotal = CalculateInventoryPrice();
            gpGained += currtotal;
            final boolean success2 = getBank().depositAll();
            Sleep.sleepUntil(success2, 3000);
        }
	}
	
	private void tradeMule() throws InterruptedException {
		log("trying to trade mule");
		if (!Banks.EDGEVILLE.contains(myPosition())) {
            log("Bank1");
            walker.moveToBank();
        }
		Player player = null;
		if (!trade.isCurrentlyTrading()) {
			player = getPlayers().closest(muleName);
		} 
		if (player != null) {
			player.interact("Trade with");
			Sleep.sleepUntil(trade.isFirstInterfaceOpen(), 5000);
		}
		if (trade.isFirstInterfaceOpen()) {
			if(getInventory().contains("Adamant arrow"))
				Sleep.sleepUntil(trade.offerAll("Adamant arrow"), 3000);
			if(getInventory().contains("Swordfish"))
				Sleep.sleepUntil(trade.offerAll("Swordfish"), 3000);
			if(getInventory().contains("Anchovy pizza"))
				Sleep.sleepUntil(trade.offerAll("Anchovy pizza"), 3000);
			if(getInventory().contains("Lobster"))
				Sleep.sleepUntil(trade.offerAll("Lobster"), 3000);
			if(!trade.acceptTrade())
				Sleep.sleepUntil(trade.acceptTrade(), 3000);
				Sleep.sleepUntil(trade.acceptTrade(), 3000);
			Sleep.sleepUntil(trade.isSecondInterfaceOpen(), 5000);
			Sleep.sleepUntil(trade.acceptTrade(), 3000);
		}
		if (trade.isSecondInterfaceOpen()) {
			Sleep.sleepUntil(trade.acceptTrade(), 3000);
			Sleep.sleepUntil(trade.acceptTrade(), 3000);
			Sleep.sleepUntil(getInventory().isEmptyExcept("Lobster"), 60000);
			AdsUpInventory();
			didTrade = true;
		}
		if (trade.isCurrentlyTrading() && trade.isSecondInterfaceOpen()) {
			didTrade = true;
			
		}
		
		if (!inventory.isEmpty() && didTrade) bank();
		didTrade = false;
	}
	
	private void lootItem(final GroundItem item, final String itemName) {
		try {
			item.interact("Take");
		}
		catch (Exception e) {
			log("Exception thrown while looting" + itemName);
		}
	}
	
	private void lootItems() {
		final GroundItem anchovy = getGroundItems().closest("Anchovy pizza");
        final GroundItem swordfish = getGroundItems().closest("Swordfish");
        final GroundItem lobster = getGroundItems().closest("Lobster");
        final GroundItem adamantArrow = getGroundItems().closest("Adamant arrow");
        
        if (anchovy != null)
            lootItem(anchovy, "Swordfish");
        else if (swordfish != null)
            lootItem(swordfish, "Swordfish");
        else if (lobster != null)
            lootItem(lobster, "Lobster");
        else if (adamantArrow != null)
            lootItem(adamantArrow, "Adamant arrow");
	}
	
	private void Eat() {
        log("Eating");
        if (inventory.contains("Lobster"))
        	getInventory().getItem("Lobster").interact("Eat");
        else if (inventory.contains("Swordfish"))
        	getInventory().getItem("Swordfish").interact("Eat");
    }
	
	private boolean freshInventory() {
		return getInventory().contains("Bucket") || getInventory().contains("Pot") || getInventory().contains("Air rune");
	}
    
    private boolean notDoneBanking() {
        return !getInventory().isEmptyExcept("Lobster") || !(myPlayer().getPosition().getY() > 3520);
    }
    
    private boolean canCollect() {
        return getMap().getWildernessLevel() > 0;
    }
    
    private boolean isUnderAttack() {
        return myPlayer().isUnderAttack();
    }
    
    private boolean finishedLootingRun() {
        final long totalPrice = CalculateInventoryPrice();
        return totalPrice >= inventoryLimit || getInventory().isFull();
    }
    
    private void AdsUpInventory() {
    	arrowsCollected += getInventory().getAmount("Adamant arrow");
        lobstersCollected += getInventory().getAmount("Lobster");
        swordfishCollected += getInventory().getAmount("Swordfish");
        anchoviesCollected += getInventory().getAmount("Anchovy pizza");
    }

    private long CalculateInventoryPrice() {
        final long totalAddy = arrowsPrice * getInventory().getAmount(new String[] { "Adamant arrow" });
        final long totalLobster = lobstersPrice * getInventory().getAmount(new String[] { "Lobster" });
        final long totalSwordies = swordfishPrice * getInventory().getAmount(new String[] { "Swordfish" });
        final long totalAnchovy = achoviesPrice * getInventory().getAmount(new String[] { "Anchovy pizza" });
        final long totalPrice = totalAnchovy + totalSwordies + totalAddy + totalLobster;
        return totalPrice;
    }
    
    public void onPaint(final Graphics2D g) {
        final long timeElapsed = System.currentTimeMillis() - startTime;
        final long ProfitPerHour = Math.round(gpGained * 1.0 / timeElapsed * 1.0 * 3600000.0);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 230, 516, 118);
        g.setColor(new Color(244, 249, 184, 250));
        g.drawString("Runtime: " + formatTime(timeElapsed), 40, 250);
        g.drawString("Current Inventory Value: " + CalculateInventoryPrice(), 40, 270);
        g.drawString("Gp/H: " + ProfitPerHour, 40, 290);
        g.drawString("Adamant Arrows: " + arrowsCollected, 260, 250);
        g.drawString("Lobsters: " + lobstersCollected, 260, 270);
        g.drawString("Swordfish: " + swordfishCollected, 260, 290);
        g.drawString("Anchovy Pizzas: " + anchoviesCollected, 260, 310);
    }
    
    public final String formatTime(final long ms) {
        long s = ms / 1000L;
        long m = s / 60L;
        long h = m / 60L;
        final long d = h / 24L;
        s %= 60L;
        m %= 60L;
        h %= 24L;
        return (d > 0L) ? String.format("%02d:%02d:%02d:%02d", d, h, m, s) : ((h > 0L) ? String.format("%02d:%02d:%02d", h, m, s) : String.format("%02d:%02d", m, s));
    }

}

