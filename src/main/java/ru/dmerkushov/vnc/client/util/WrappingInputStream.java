/*
 * Copyright (C) 2019 dmerkushov
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
package ru.dmerkushov.vnc.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author dmerkushov
 */
public class WrappingInputStream extends InputStream {

	LinkedList<InputStream> streams = new LinkedList<> ();

	public WrappingInputStream (Collection<InputStream> in) {
		if (in != null && in.size () > 0) {
			streams.addAll (in);
		}
	}

	public WrappingInputStream (InputStream... in) {
		this (Arrays.asList (in));
	}

	public void appendInputStreams (Collection<InputStream> in) {
		if (in != null && in.size () > 0) {
			streams.addAll (in);
		}
	}

	public void appendInputStreams (InputStream... in) {
		appendInputStreams (Arrays.asList (in));
	}

	public InputStream currentStream () {
		return streams.get (0);
	}

	public int streamsCount () {
		return streams.size ();
	}

	@Override
	public int read () throws IOException {
		InputStream currStream;

		try {
			currStream = streams.get (0);
		} catch (IndexOutOfBoundsException ex) {
			return -1;
		}

		int r = -1;

		while (r == -1) {
			while (currStream == null) {
				streams.remove ();
				try {
					currStream = streams.get (0);
				} catch (IndexOutOfBoundsException ex) {
					return -1;
				}
			}

			r = currStream.read ();

			if (r == -1) {
				currStream.close ();
				streams.remove ();
				try {
					currStream = streams.get (0);
				} catch (IndexOutOfBoundsException ex) {
					return -1;
				}
			}
		}

		return r;
	}

	@Override
	public void close () throws IOException {
		while (true) {
			InputStream currStream;
			try {
				currStream = streams.get (0);
			} catch (IndexOutOfBoundsException ex) {
				return;
			}
			currStream.close ();
			streams.remove ();
		}
	}

}
