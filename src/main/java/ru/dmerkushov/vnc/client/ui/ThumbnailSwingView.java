/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.ui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import static ru.dmerkushov.vnc.client.VncCommon.logger;

/**
 * @author dmerkushov
 */
public class ThumbnailSwingView extends JComponent {

	public final static String UPDATE_TIMER_NAME = "updateTimer";

	//	private long updateStartDelayMs = 2000L;
	private long updatePeriodMs = 1000L;
	private final JComponent innerComponent;

	private Timer updateTimer;
	private TimerTask updateTimerTask;

	public ThumbnailSwingView (JComponent innerComponent) {
		Objects.requireNonNull (innerComponent, "innerComponent");

		this.innerComponent = innerComponent;

		this.restartUpdates ();
	}

	@Override
	public void paint (Graphics g) {
		Dimension innerSize = this.innerComponent.getPreferredSize ();
		int innerW = innerSize.width <= 0 ? 100 : innerSize.width;
		int innerH = innerSize.height <= 0 ? 100 : innerSize.height;
		int w = this.getWidth ();
		int h = this.getHeight ();

		BufferedImage img = new BufferedImage (innerW, innerH, BufferedImage.TYPE_INT_ARGB);

		this.innerComponent.paint (img.getGraphics ());

		Graphics2D g2 = (Graphics2D) g;

		g2.drawImage (img, 0, 0, w - 1, h - 1, 0, 0, innerW - 1, innerH - 1, null);
	}

	//	public long getUpdateStartDelayMs () {
//		return updateStartDelayMs;
//	}
//
//	public void setUpdateStartDelayMs (long updateStartDelayMs) {
//		setUpdateParameters (updateStartDelayMs, this.updatePeriodMs);
//	}
	public final long getUpdatePeriodMs () {
		return this.updatePeriodMs;
	}

	public final void setUpdatePeriodMs (long updatePeriodMs) {
		this.setUpdateParameters (0L,/*this.updateStartDelayMs,*/ updatePeriodMs);
	}

	public final void setUpdateParameters (long updateStartDelayMs, long updatePeriodMs) {
		if (/*this.updateStartDelayMs == updateStartDelayMs &&*/this.updatePeriodMs == updatePeriodMs) {
			this.restartUpdates ();
			return;
		}

//		this.updateStartDelayMs = updateStartDelayMs;
		this.updatePeriodMs = updatePeriodMs;
		this.restartUpdates ();
	}

	public final void restartUpdates () {
		this.stopUpdates ();
		this.updateTimer = new Timer (ThumbnailSwingView.UPDATE_TIMER_NAME);
		this.updateTimerTask = new TimerTask () {

			@Override
			public void run () {
				ThumbnailSwingView.this.repaint ();
			}
		};
		this.updateTimer.schedule (this.updateTimerTask, 0L /*updateStartDelayMs*/, this.updatePeriodMs);
	}

	public final void stopUpdates () {
		if (this.updateTimer != null) {
			this.updateTimer.cancel ();
		}
	}

	////////////////////////////////////////////////////////////////////////////
	public static void main (String[] args) {

		JFrame frame = new JFrame ();

		final JButton btn = new JButton ("Hi");
		btn.setSize (100, 100);

		final ThumbnailSwingView thm = new ThumbnailSwingView (btn);

		Thread btnUpdateThread = new Thread (new Runnable () {

			@Override
			public void run () {
				for (int i = 0; i < 10000; i++) {
					try {
						Thread.sleep (200L);
					} catch (InterruptedException ex) {
						logger.log (Level.SEVERE, null, ex);
					}
					btn.setText (String.valueOf (i));
				}

			}

		});

		frame.add (thm);
		frame.setSize (100, 200);

		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		btnUpdateThread.start ();
		frame.setVisible (true);
	}

}
