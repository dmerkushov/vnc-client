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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author dmerkushov
 */
public class ThumbnailView extends JComponent {

	private final JComponent innerComponent;

	public ThumbnailView (JComponent innerComponent) {
		Objects.requireNonNull (innerComponent, "innerComponent");

		this.innerComponent = innerComponent;
	}

	@Override
	public void paint (Graphics g) {
		Dimension innerSize = innerComponent.getSize ();
		int innerW = innerSize.width;
		int innerH = innerSize.height;
		int w = getWidth ();
		int h = getHeight ();

		BufferedImage img = new BufferedImage (innerW, innerH, BufferedImage.TYPE_INT_ARGB);

		innerComponent.paint (img.getGraphics ());

		Graphics2D g2 = (Graphics2D) g;

		g2.drawImage (img, 0, 0, w - 1, h - 1, 0, 0, innerW - 1, innerH - 1, null);
	}

	////////////////////////////////////////////////////////////////////////////
	public static void main (String[] args) {
		JFrame frame = new JFrame ();

		JButton btn = new JButton ("Hi");
		btn.setSize (500, 500);

		ThumbnailView thm = new ThumbnailView (btn);

		frame.add (thm);
		frame.setSize (100, 200);

		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.setVisible (true);
	}

}
