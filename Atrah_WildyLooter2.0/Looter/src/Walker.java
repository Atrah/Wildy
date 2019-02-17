package src;

import org.osbot.rs07.script.*;
import org.osbot.rs07.api.model.*;
import org.osbot.rs07.utility.*;
import org.osbot.rs07.event.*;
import org.osbot.rs07.api.map.*;
import org.osbot.rs07.api.map.constants.*;

public class Walker extends MethodProvider
{
    private final Position wildy;
    
    public Walker() {
        wildy = new Position(3087, 3536, 0);
    }
    
    public void moveToWild() {
        try {
            getWalking().walk(new Position(3087, 3508, 0));
            getWalking().walk(new Position(3086, 3520, 0));
            getObjects().closest("Wilderness Ditch").interact("Cross");
            Sleep.sleepUntil(getWidgets().getWidgetContainingText("Enter Wilderness") != null || isAnimating(), 3000);
            if (getWidgets().getWidgetContainingText("Enter Wilderness") != null && getWidgets().getWidgetContainingText("Enter Wilderness").isVisible()) {
                getWidgets().getWidgetContainingText("Enter Wilderness").interact(new String[0]);
            }
            Sleep.sleepUntil(!isAnimating(), 3000);
            getWalking().webWalk(wildy);
        }
        catch (Exception e) {
            log(e.toString());
        }
    }
    
    public void feint() {
        final WebWalkEvent webWalkEvent = new WebWalkEvent(new Position(3089, 3523, 0));
        webWalkEvent.setBreakCondition(new Condition() {
            public boolean evaluate() {
                return Walker.this.shouldIEat();
            }
        });
        execute((Event)webWalkEvent);
    }
    
    public boolean shouldIEat() {
        return myPlayer().getHealthPercent() <= 60;
    }
    
    public void moveToBank() {
        if (!getSettings().isRunning() && getSettings().getRunEnergy() > 10) {
            getSettings().setRunning(true);
            Sleep.sleepUntil(getSettings().isRunning(), 3000);
        }
        try {
            if (myPlayer().getPosition().getY() > 3520) {
                log("NOT HERE");
                final WebWalkEvent webWalkEvent = new WebWalkEvent(new Position(3088, 3523, 0));
                webWalkEvent.setBreakCondition(new Condition() {
                    public boolean evaluate() {
                        return false;
                    }
                });
                execute((Event)webWalkEvent);
                final RS2Object ditch = objects.closest("Wilderness Ditch");
                if (myPlayer().isAnimating()) {
                    ditch.interact("Cross");
                }
                Sleep.sleepUntil(!this.isAnimating(), 5000);
            }
            log("HERE");
            getWalking().walk(new Position(3086, 3506, 0));
            final WebWalkEvent webWalkEvent2 = new WebWalkEvent(Banks.EDGEVILLE);
            webWalkEvent2.setBreakCondition(new Condition() {
                public boolean evaluate() {
                    return false;
                }
            });
            execute((Event)webWalkEvent2);
        }
        catch (Exception e) {
            log("Exception while move to Bank");
        }
    }
    
    public boolean isAnimating() throws InterruptedException {
        for (int i = 0; i < 8; ++i) {
            if (myPlayer().isAnimating()) {
                return true;
            }
            sleep(400L);
        }
        return false;
    }
}
