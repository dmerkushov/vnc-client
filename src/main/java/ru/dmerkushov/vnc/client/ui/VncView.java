/*
 * Copyright (C) 2017 dmerkushov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package ru.dmerkushov.vnc.client.ui;

import ru.dmerkushov.vnc.client.rfb.session.RfbClientSession;

/**
 *
 * @author dmerkushov
 */
public interface VncView {

	void setSession (RfbClientSession session);

	RfbClientSession getSession ();

	/**
	 * Returns an AWT component representing this view, if this is a AWT-based
	 * view, null otherwise
	 *
	 * @return
	 */
	java.awt.Component getAwtComponent ();

	/**
	 * Returns a Swing component representing this view, if this is a
	 * Swing-based view, null otherwise
	 *
	 * @return
	 */
	javax.swing.JComponent getSwingComponent ();

	/**
	 * Returns a JavaFX node representing this view, if this is a JavaFX-based
	 * view, null otherwise
	 *
	 * @return
	 */
	javafx.scene.Node getJavafxNode ();

	java.awt.Dimension getPreferredSize ();

	void paintNow (int x, int y, int width, int height);

	void setCursor (java.awt.Cursor cursor);

	void repaint ();

}
