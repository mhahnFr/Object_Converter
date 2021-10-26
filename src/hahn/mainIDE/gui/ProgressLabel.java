package hahn.mainIDE.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.Timer;

public class ProgressLabel extends JLabel implements ActionListener {
	private static final long serialVersionUID = -5825813759547888324L;
	private Timer errorBlinker;
	private Icon wipIcon;
	private Icon errIcon;
	private Icon infoIcon;
	private boolean error;
	private boolean blink;
	private boolean clickable;
	
	public ProgressLabel(Icon wipIcon, Icon errIcon, Icon infoIcon) {
		this.wipIcon = wipIcon;
		this.errIcon = errIcon;
		this.infoIcon = infoIcon;
		errorBlinker = new Timer(1000, this);
	}
	
	public void showError(String message, boolean clickable) {
		this.clickable = clickable;
		if(clickable) {
			errorBlinker.setDelay(1000);
		} else {
			errorBlinker.setDelay(500);
		}
		if(!error) {
			startError();
		}
		setIcon(errIcon);
		setText(message);
	}
	
	public void showInformation(String message) {
		if(error) {
			stopError();
		}
		setIcon(infoIcon);
		setText(message);
	}
	
	public void showWorkInProgress(String work) {
		if(error) {
			stopError();
		}
		setIcon(wipIcon);
		setText(work);
	}
	
	private void stopError() {
		error = false;
		errorBlinker.stop();
		setForeground(Color.BLACK);
	}
	
	private void startError() {
		error = true;
		setForeground(Color.RED);
		errorBlinker.start();
	}
	
	public void actionPerformed(ActionEvent e) {
		blink = !blink;
		if(clickable) {
			switchIcon();
		} else {
			changeTextColor();
		}
	}
	
	private void switchIcon() {
		if(blink) {
			setIcon(errIcon);
		} else {
			setIcon(infoIcon);
		}
	}
	
	private void changeTextColor() {
		if(blink) {
			setForeground(Color.RED);
		} else {
			setForeground(Color.BLACK);
		}
	}
}