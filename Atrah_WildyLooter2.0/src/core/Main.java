package core;


import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import sections.TutorialIsland;
import src.Looter;

@ScriptManifest(name = "Atrah Wildy looter", author = "Atrahasis", info = "wildy looter", version = 0.1, logo = "")
public class Main extends Script{
	
	public static Script SCRIPT;
	
	private Area TUTO_ISLAND = new Area(3054, 3133, 3155, 3055);
	private Looter looterScript;
	private TutorialIsland tutoScript;

	@Override
	public void onStart() throws InterruptedException {
		
		looterScript = new Looter();
		tutoScript = new TutorialIsland();
	}
	
	@Override
	public int onLoop() throws InterruptedException {
		if(SCRIPT == null) {
			if(TUTO_ISLAND.contains(myPosition())) {
				log("Starting tuto script");
				SCRIPT = tutoScript;
			} else {
				log("Starting looter script");
				SCRIPT = looterScript;
			}
			log("Starting script" + SCRIPT.getName());
			SCRIPT.exchangeContext(bot);
			SCRIPT.onStart();
		}
		
		return SCRIPT.onLoop();
	}

}
