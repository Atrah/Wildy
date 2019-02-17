package src;

import javax.swing.border.*;
import java.awt.*;
import javax.swing.*;

public class GUI {
	
	public JTextField bankText;
	public JTextField muleText;
	
	private boolean isStarted;
	private final JDialog mainDialog;
	
	public GUI() {
		mainDialog = new JDialog();
		mainDialog.setTitle("Atrah Wildy looter with mule");
		mainDialog.setModal(true);
        mainDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainDialog.getContentPane().add(mainPanel);
        
        JPanel valuePanel = new JPanel();
        valuePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JLabel label = new JLabel("Mule name:");
        muleText = new JTextField("", 10);
        valuePanel.add(label);
        valuePanel.add(muleText);
        
        mainPanel.add(valuePanel);
        
       JButton startBtn = new JButton("Start");
       startBtn.addActionListener(e -> {
    	   isStarted = true;
    	   close();
    	   return;
       });
       
       mainPanel.add(startBtn);
       mainDialog.pack();
	}
	
	public String getVMuleValue() {
		return muleText.getText().trim();
	}
	
	public boolean isStarted() {
		return isStarted;
	}

	public void open() {
		mainDialog.setVisible(true);
	}

	public void close() {
		mainDialog.setVisible(false);
		mainDialog.dispose();
	}
}
