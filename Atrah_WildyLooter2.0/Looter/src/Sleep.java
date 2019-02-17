package src;

import org.osbot.rs07.utility.ConditionalSleep;

public class Sleep extends ConditionalSleep{
	
	private final boolean condition;
	
	public Sleep(final boolean cond, final int timeout) {
		super(timeout);
		condition = cond;
	}
	
	public final boolean condition() throws InterruptedException {
		return condition;
	}
	
	public static boolean sleepUntil(final boolean cond, final int timeout) { 
		return new Sleep(cond, timeout).sleep();
	}
}
