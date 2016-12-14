/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.dmerkushov.vnc.client.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import ru.dmerkushov.vnc.client.VncCommon;

/**
 *
 * @author dmerkushov
 */
public class ThumbnailView extends JComponent {

	public final static String UPDATE_TIMER_NAME = "updateTimer";

//	private long updateStartDelayMs = 2000L;
	private long updatePeriodMs = 1000L;
	private final JComponent innerComponent;

	private Timer updateTimer;
	private TimerTask updateTimerTask;

	public ThumbnailView (JComponent innerComponent) {
		Objects.requireNonNull (innerComponent, "innerComponent");

		this.innerComponent = innerComponent;

		restartUpdates ();
	}

	@Override
	public void paint (Graphics g) {
		Dimension innerSize = innerComponent.getPreferredSize ();
		int innerW = innerSize.width <= 0 ? 100 : innerSize.width;
		int innerH = innerSize.height <= 0 ? 100 : innerSize.height;
		int w = getWidth ();
		int h = getHeight ();

		BufferedImage img = new BufferedImage (innerW, innerH, BufferedImage.TYPE_INT_ARGB);

		innerComponent.paint (img.getGraphics ());

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
		return updatePeriodMs;
	}

	public final void setUpdatePeriodMs (long updatePeriodMs) {
		setUpdateParameters (0L,/*this.updateStartDelayMs,*/ updatePeriodMs);
	}

	public final void setUpdateParameters (long updateStartDelayMs, long updatePeriodMs) {
		if (/*this.updateStartDelayMs == updateStartDelayMs &&*/this.updatePeriodMs == updatePeriodMs) {
			restartUpdates ();
			return;
		}

//		this.updateStartDelayMs = updateStartDelayMs;
		this.updatePeriodMs = updatePeriodMs;
		restartUpdates ();
	}

	public final void restartUpdates () {
		stopUpdates ();
		updateTimer = new Timer (UPDATE_TIMER_NAME);
		updateTimerTask = new TimerTask () {

			@Override
			public void run () {
				ThumbnailView.this.repaint ();
			}
		};
		updateTimer.schedule (updateTimerTask, 0L /*updateStartDelayMs*/, updatePeriodMs);
	}

	public final void stopUpdates () {
		if (updateTimer != null) {
			updateTimer.cancel ();
		}
	}

	////////////////////////////////////////////////////////////////////////////
	public static void main (String[] args) {

		JFrame frame = new JFrame ();

		final JButton btn = new JButton ("Hi");
		btn.setSize (100, 100);

		final ThumbnailView thm = new ThumbnailView (btn);

		Thread btnUpdateThread = new Thread (new Runnable () {

			@Override
			public void run () {
				for (int i = 0; i < 10000; i++) {
					try {
						Thread.sleep (200L);
					} catch (InterruptedException ex) {
						VncCommon.getLogger ().log (Level.SEVERE, null, ex);
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
