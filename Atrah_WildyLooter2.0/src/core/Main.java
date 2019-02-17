package core;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.randoms.WelcomeScreen;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import script.TutorialIsland;
import src.Looter;

@ScriptManifest(name = "Atrah Wildy looter", author = "Atrahasis", info = "wildy looter", version = 2.0, logo = "")
public class Main extends Script{
	
	private final Area TUTO_ISLAND = new Area (3045, 3138, 3157, 3057);
	private boolean needATask = true;
	
	@Override
	public void onStart() throws InterruptedException {
		if(getClient().isLoggedIn()) {
			getScript();
		}
	}
	
	@Override
	public int onLoop() throws InterruptedException {
		if (!getClient().isLoggedIn() || getWidgets().isVisible(WelcomeScreen.INTERFACE)) {
            return random(1200, 1800);
        } else if (getClient().isLoggedIn() && needATask == true) {
        	getScript();
        }
		return random(200, 300);
	}
	
	private void getScript() throws InterruptedException {
		if(tutoNeeded()) {
			needATask = false;
			startTuto();
		}
	    if(!tutoNeeded()) {
	    	needATask = false;
	    	startLooter();
	    }
	}
	
	private void startTuto() throws InterruptedException {
		TutorialIsland tuto = new TutorialIsland();
		tuto.exchangeContext(getBot());
		tuto.onStart();
	}
	
	private void startLooter() throws InterruptedException {
		Looter looter = new Looter();
		looter.exchangeContext(getBot());
		looter.onStart();
	}
	
	private boolean tutoNeeded() {
		return TUTO_ISLAND.contains(myPosition());
	}
}
