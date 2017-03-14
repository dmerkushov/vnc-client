/*
 * Copyright (C) 2016 Dmitriy Merkushov
 * Copyright (C) 2013 Brian P. Hinz
 * Copyright (C) 2002-2005 RealVNC Ltd.  All Rights Reserved.
 * Copyright (C) 2001 Markus G. Kuhn, University of Cambridge
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */
package ru.dmerkushov.vnc.client.ui.events;

import java.awt.event.KeyEvent;
import ru.dmerkushov.vnc.client.VncCommon;

/**
 *
 * @author dmerkushov
 */
public class Keysyms {

	public static final int VoidSymbol = 0xffffff;
	/*
	 * TTY Functions, cleverly chosen to map to ascii, for convenience of
	 * programming, but could have been arbitrary (at the cost of lookup
	 * tables in client code.
	 */
	public static final int BackSpace = 0xFF08;
	public static final int Tab = 0xFF09;
	public static final int Linefeed = 0xFF0A;
	public static final int Clear = 0xFF0B;
	public static final int Return = 0xFF0D;
	public static final int Pause = 0xFF13;
	public static final int Scroll_Lock = 0xFF14;
	public static final int Sys_Req = 0xFF15;
	public static final int Escape = 0xFF1B;
	public static final int Delete = 0xFFFF;
	/*
	 * International & multi-key character composition
	 */
	public static final int Multi_key = 0xFF20;
	/*
	 * Multi-key character compose
	 */
	public static final int Codeinput = 0xFF37;
	public static final int SingleCandidate = 0xFF3C;
	public static final int MultipleCandidate = 0xFF3D;
	public static final int PreviousCandidate = 0xFF3E;
	/*
	 * Japanese keyboard support
	 */
	public static final int Kanji = 0xFF21;
	/*
	 * Kanji, Kanji convert
	 */
	public static final int Muhenkan = 0xFF22;
	/*
	 * Cancel Conversion
	 */
	public static final int Henkan_Mode = 0xFF23;
	/*
	 * Start/Stop Conversion
	 */
	public static final int Henkan = 0xFF23;
	/*
	 * Alias for Henkan_Mode
	 */
	public static final int Romaji = 0xFF24;
	/*
	 * to Romaji
	 */
	public static final int Hiragana = 0xFF25;
	/*
	 * to Hiragana
	 */
	public static final int Katakana = 0xFF26;
	/*
	 * to Katakana
	 */
	public static final int Hiragana_Katakana = 0xFF27;
	/*
	 * Hiragana/Katakana toggle
	 */
	public static final int Zenkaku = 0xFF28;
	/*
	 * to Zenkaku
	 */
	public static final int Hankaku = 0xFF29;
	/*
	 * to Hankaku
	 */
	public static final int Zenkaku_Hankaku = 0xFF2A;
	/*
	 * Zenkaku/Hankaku toggle
	 */
	public static final int Touroku = 0xFF2B;
	/*
	 * Add to Dictionary
	 */
	public static final int Massyo = 0xFF2C;
	/*
	 * Delete from Dictionary
	 */
	public static final int Kana_Lock = 0xFF2D;
	/*
	 * Kana Lock
	 */
	public static final int Kana_Shift = 0xFF2E;
	/*
	 * Kana Shift
	 */
	public static final int Eisu_Shift = 0xFF2F;
	/*
	 * Alphanumeric Shift
	 */
	public static final int Eisu_toggle = 0xFF30;
	/*
	 * Alphanumeric toggle
	 */
	public static final int Kanji_Bangou = 0xFF37;
	/*
	 * Codeinput
	 */
	public static final int Zen_Koho = 0xFF3D;
	/*
	 * Multiple/All Candidate(s)
	 */
	public static final int Mae_Koho = 0xFF3E;
	/*
	 * Previous Candidate
	 */
	/*
	 * Cursor control & motion
	 */
	public static final int Home = 0xFF50;
	public static final int Left = 0xFF51;
	public static final int Up = 0xFF52;
	public static final int Right = 0xFF53;
	public static final int Down = 0xFF54;
	public static final int Prior = 0xFF55;
	public static final int Page_Up = 0xFF55;
	public static final int Next = 0xFF56;
	public static final int Page_Down = 0xFF56;
	public static final int End = 0xFF57;
	public static final int Begin = 0xFF58;
	/*
	 * Misc Functions
	 */
	public static final int Select = 0xFF60;
	public static final int Print = 0xFF61;
	public static final int Execute = 0xFF62;
	public static final int Insert = 0xFF63;
	public static final int Undo = 0xFF65;
	public static final int Redo = 0xFF66;
	public static final int Menu = 0xFF67;
	public static final int Find = 0xFF68;
	public static final int Cancel = 0xFF69;
	public static final int Help = 0xFF6A;
	public static final int Break = 0xFF6B;
	public static final int Mode_switch = 0xFF7E;
	public static final int script_switch = 0xFF7E;
	public static final int Num_Lock = 0xFF7F;
	/*
	 * Keypad Functions, keypad numbers cleverly chosen to map to ascii
	 */
	public static final int KP_Enter = 0xFF8D;
	public static final int KP_Home = 0xFF95;
	public static final int KP_Left = 0xFF96;
	public static final int KP_Up = 0xFF97;
	public static final int KP_Right = 0xFF98;
	public static final int KP_Down = 0xFF99;
	public static final int KP_Page_Up = 0xFF9A;
	public static final int KP_Page_Down = 0xFF9B;
	public static final int KP_End = 0xFF9C;
	public static final int KP_Begin = 0xFF9D;
	public static final int KP_Insert = 0xFF9E;
	public static final int KP_Delete = 0xFF9F;
	public static final int KP_Equal = 0xFFBD;
	public static final int KP_0 = 0xFFB0;
	public static final int KP_1 = 0xFFB1;
	public static final int KP_2 = 0xFFB2;
	public static final int KP_3 = 0xFFB3;
	public static final int KP_4 = 0xFFB4;
	public static final int KP_5 = 0xFFB5;
	public static final int KP_6 = 0xFFB6;
	public static final int KP_7 = 0xFFB7;
	public static final int KP_8 = 0xFFB8;
	public static final int KP_9 = 0xFFB9;
	public static final int KP_Decimal = 0xFFAE;
	public static final int KP_Add = 0xFFAB;
	public static final int KP_Subtract = 0xFFAD;
	public static final int KP_Multiply = 0xFFAA;
	public static final int KP_Divide = 0xFFAF;
	/*
	 * Auxilliary Functions; note the duplicate definitions for left and right
	 * function keys; Sun keyboards and a few other manufactures have such
	 * function key groups on the left and/or right sides of the keyboard.
	 * We've not found a keyboard with more than 35 function keys total.
	 */
	public static final int F1 = 0xFFBE;
	public static final int F2 = 0xFFBF;
	public static final int F3 = 0xFFC0;
	public static final int F4 = 0xFFC1;
	public static final int F5 = 0xFFC2;
	public static final int F6 = 0xFFC3;
	public static final int F7 = 0xFFC4;
	public static final int F8 = 0xFFC5;
	public static final int F9 = 0xFFC6;
	public static final int F10 = 0xFFC7;
	public static final int F11 = 0xFFC8;
	public static final int F12 = 0xFFC9;
	public static final int F13 = 0xFFCA;
	public static final int F14 = 0xFFCB;
	public static final int F15 = 0xFFCC;
	public static final int F16 = 0xFFCD;
	public static final int F17 = 0xFFCE;
	public static final int F18 = 0xFFCF;
	public static final int F19 = 0xFFD0;
	public static final int F20 = 0xFFD1;
	public static final int F21 = 0xFFD2;
	public static final int F22 = 0xFFD3;
	public static final int F23 = 0xFFD4;
	public static final int F24 = 0xFFD5;
	/*
	 * Modifiers
	 */
	public static final int Shift_L = 0xFFE1;
	public static final int Shift_R = 0xFFE2;
	public static final int Control_L = 0xFFE3;
	public static final int Control_R = 0xFFE4;
	public static final int Caps_Lock = 0xFFE5;
	public static final int Shift_Lock = 0xFFE6;
	public static final int Meta_L = 0xFFE7;
	public static final int Meta_R = 0xFFE8;
	public static final int Alt_L = 0xFFE9;
	public static final int Alt_R = 0xFFEA;
	public static final int Super_L = 0xFFEB;
	public static final int Super_R = 0xFFEC;
	public static final int Hyper_L = 0xFFED;
	public static final int Hyper_R = 0xFFEE;
	/*
	 * ISO 9995 Function and Modifier Keys
	 * Byte 3 = 0xFE
	 */
	public static final int ISO_Level3_Shift = 0xFE03;
	/*
	 * Dead Modifier Keys
	 */
	public static final int Dead_Grave = 0xfe50;
	/*
	 * COMBINING GRAVE ACCENT
	 */
	public static final int Dead_Acute = 0xfe51;
	/*
	 * COMBINING ACUTE ACCENT
	 */
	public static final int Dead_Circumflex = 0xfe52;
	/*
	 * COMBINING CIRCUMFLEX ACCENT
	 */
	public static final int Dead_Tilde = 0xfe53;
	/*
	 * COMBINING TILDE
	 */
	public static final int Dead_Macron = 0xfe54;
	/*
	 * COMBINING MACRON
	 */
	public static final int Dead_Breve = 0xfe55;
	/*
	 * COMBINING BREVE
	 */
	public static final int Dead_AboveDot = 0xfe56;
	/*
	 * COMBINING DOT ABOVE
	 */
	public static final int Dead_Diaeresis = 0xfe57;
	/*
	 * COMBINING DIAERESIS
	 */
	public static final int Dead_AboveRing = 0xfe58;
	/*
	 * COMBINING RING ABOVE
	 */
	public static final int Dead_DoubleAcute = 0xfe59;
	/*
	 * COMBINING DOUBLE ACUTE ACCENT
	 */
	public static final int Dead_Caron = 0xfe5a;
	/*
	 * COMBINING CARON
	 */
	public static final int Dead_Cedilla = 0xfe5b;
	/*
	 * COMBINING CEDILLA
	 */
	public static final int Dead_Ogonek = 0xfe5c;
	/*
	 * COMBINING OGONEK
	 */
	public static final int Dead_Iota = 0xfe5d;
	/*
	 * MODIFIER LETTER SMALL IOTA
	 */
	public static final int Dead_Voiced_Sound = 0xfe5e;
	/*
	 * COMBINING KATAKANA-HIRAGANA VOICED SOUND MARK
	 */
	public static final int Dead_SemiVoiced_Sound = 0xfe5f;
	/*
	 * COMBINING KATAKANA-HIRAGANA SEMI-VOICED SOUND MARK
	 */
	public static final int Dead_BelowDot = 0xfe60;

	/*
	 * COMBINING DOT BELOW
	 */
	private static class KeySymbol {

		public KeySymbol (int keycode_, int[] keysym_) {
			keycode = keycode_;
			keysym = new int[5];
			System.arraycopy (keysym_, 0, keysym, 0, 5);
		}
		int keycode;
		int[] keysym;
	}
	private static KeySymbol[] keySymbols = { /*
		 * KEYCODE LOCATION
		 */ /*
		 * UNKNOWN STANDARD LEFT RIGHT NUMPAD
		 */new KeySymbol (KeyEvent.VK_BACK_SPACE, new int[]{VoidSymbol, BackSpace, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_TAB, new int[]{VoidSymbol, Tab, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_ENTER, new int[]{VoidSymbol, Return, VoidSymbol, VoidSymbol, KP_Enter}), new KeySymbol (KeyEvent.VK_ESCAPE, new int[]{VoidSymbol, Escape, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_CONTROL, new int[]{VoidSymbol, Control_L, Control_L, Control_R, VoidSymbol}), new KeySymbol (KeyEvent.VK_ALT_GRAPH, new int[]{VoidSymbol, ISO_Level3_Shift, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_ALT, new int[]{VoidSymbol, ISO_Level3_Shift, Alt_L, Alt_R, VoidSymbol}), new KeySymbol (KeyEvent.VK_SHIFT, new int[]{VoidSymbol, Shift_L, Shift_L, Shift_R, VoidSymbol}), new KeySymbol (KeyEvent.VK_META, new int[]{VoidSymbol, Meta_L, Meta_L, Meta_R, VoidSymbol}), new KeySymbol (KeyEvent.VK_NUMPAD0, new int[]{VoidSymbol, VoidSymbol, VoidSymbol, VoidSymbol, KP_0}), new KeySymbol (KeyEvent.VK_NUMPAD1, new int[]{VoidSymbol, VoidSymbol, VoidSymbol, VoidSymbol, KP_1}), new KeySymbol (KeyEvent.VK_NUMPAD2, new int[]{VoidSymbol, VoidSymbol, VoidSymbol, VoidSymbol, KP_2}), new KeySymbol (KeyEvent.VK_NUMPAD3, new int[]{VoidSymbol, VoidSymbol, VoidSymbol, VoidSymbol, KP_3}), new KeySymbol (KeyEvent.VK_NUMPAD4, new int[]{VoidSymbol, VoidSymbol, VoidSymbol, VoidSymbol, KP_4}), new KeySymbol (KeyEvent.VK_NUMPAD5, new int[]{VoidSymbol, VoidSymbol, VoidSymbol, VoidSymbol, KP_5}), new KeySymbol (KeyEvent.VK_NUMPAD6, new int[]{VoidSymbol, VoidSymbol, VoidSymbol, VoidSymbol, KP_6}), new KeySymbol (KeyEvent.VK_NUMPAD7, new int[]{VoidSymbol, VoidSymbol, VoidSymbol, VoidSymbol, KP_7}), new KeySymbol (KeyEvent.VK_NUMPAD8, new int[]{VoidSymbol, VoidSymbol, VoidSymbol, VoidSymbol, KP_8}), new KeySymbol (KeyEvent.VK_NUMPAD9, new int[]{VoidSymbol, VoidSymbol, VoidSymbol, VoidSymbol, KP_9}), new KeySymbol (KeyEvent.VK_DECIMAL, new int[]{VoidSymbol, KP_Decimal, VoidSymbol, VoidSymbol, KP_Decimal}), new KeySymbol (KeyEvent.VK_ADD, new int[]{VoidSymbol, KP_Add, VoidSymbol, VoidSymbol, KP_Add}), new KeySymbol (KeyEvent.VK_SUBTRACT, new int[]{VoidSymbol, KP_Subtract, VoidSymbol, VoidSymbol, KP_Subtract}), new KeySymbol (KeyEvent.VK_MULTIPLY, new int[]{VoidSymbol, KP_Multiply, VoidSymbol, VoidSymbol, KP_Multiply}), new KeySymbol (KeyEvent.VK_DIVIDE, new int[]{VoidSymbol, KP_Divide, VoidSymbol, VoidSymbol, KP_Divide}), new KeySymbol (KeyEvent.VK_DELETE, new int[]{VoidSymbol, Delete, VoidSymbol, VoidSymbol, KP_Delete}), new KeySymbol (KeyEvent.VK_CLEAR, new int[]{VoidSymbol, Clear, VoidSymbol, VoidSymbol, KP_Begin}), new KeySymbol (KeyEvent.VK_HOME, new int[]{VoidSymbol, Home, VoidSymbol, VoidSymbol, KP_Home}), new KeySymbol (KeyEvent.VK_END, new int[]{VoidSymbol, End, VoidSymbol, VoidSymbol, KP_End}), new KeySymbol (KeyEvent.VK_PAGE_UP, new int[]{VoidSymbol, Page_Up, VoidSymbol, VoidSymbol, KP_Page_Up}), new KeySymbol (KeyEvent.VK_PAGE_DOWN, new int[]{VoidSymbol, Page_Down, VoidSymbol, VoidSymbol, KP_Page_Down}), new KeySymbol (KeyEvent.VK_UP, new int[]{VoidSymbol, Up, VoidSymbol, VoidSymbol, KP_Up}), new KeySymbol (KeyEvent.VK_DOWN, new int[]{VoidSymbol, Down, VoidSymbol, VoidSymbol, KP_Down}), new KeySymbol (KeyEvent.VK_LEFT, new int[]{VoidSymbol, Left, VoidSymbol, VoidSymbol, KP_Left}), new KeySymbol (KeyEvent.VK_RIGHT, new int[]{VoidSymbol, Right, VoidSymbol, VoidSymbol, KP_Right}), new KeySymbol (KeyEvent.VK_BEGIN, new int[]{VoidSymbol, Begin, VoidSymbol, VoidSymbol, KP_Begin}), new KeySymbol (KeyEvent.VK_KP_LEFT, new int[]{VoidSymbol, KP_Left, VoidSymbol, VoidSymbol, KP_Left}), new KeySymbol (KeyEvent.VK_KP_UP, new int[]{VoidSymbol, KP_Up, VoidSymbol, VoidSymbol, KP_Up}), new KeySymbol (KeyEvent.VK_KP_RIGHT, new int[]{VoidSymbol, KP_Right, VoidSymbol, VoidSymbol, KP_Right}), new KeySymbol (KeyEvent.VK_KP_DOWN, new int[]{VoidSymbol, KP_Down, VoidSymbol, VoidSymbol, KP_Down}), new KeySymbol (KeyEvent.VK_PRINTSCREEN, new int[]{VoidSymbol, Print, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_SCROLL_LOCK, new int[]{VoidSymbol, Scroll_Lock, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_CAPS_LOCK, new int[]{VoidSymbol, Caps_Lock, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_NUM_LOCK, new int[]{VoidSymbol, Num_Lock, VoidSymbol, VoidSymbol, Num_Lock}), new KeySymbol (KeyEvent.VK_INSERT, new int[]{VoidSymbol, Insert, VoidSymbol, VoidSymbol, KP_Insert}), new KeySymbol (KeyEvent.VK_AGAIN, new int[]{VoidSymbol, Redo, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_UNDO, new int[]{VoidSymbol, Undo, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_FIND, new int[]{VoidSymbol, Find, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_STOP, new int[]{VoidSymbol, Cancel, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_HELP, new int[]{VoidSymbol, Help, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_WINDOWS, new int[]{VoidSymbol, Super_L, Super_L, Super_R, VoidSymbol}), new KeySymbol (KeyEvent.VK_CONTEXT_MENU, new int[]{VoidSymbol, Menu, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_KANJI, new int[]{VoidSymbol, Kanji, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_KATAKANA, new int[]{VoidSymbol, Katakana, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_HIRAGANA, new int[]{VoidSymbol, Hiragana, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_PREVIOUS_CANDIDATE, new int[]{VoidSymbol, PreviousCandidate, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_CODE_INPUT, new int[]{VoidSymbol, Codeinput, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_JAPANESE_ROMAN, new int[]{VoidSymbol, Romaji, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_KANA_LOCK, new int[]{VoidSymbol, Kana_Lock, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_ABOVEDOT, new int[]{VoidSymbol, Dead_AboveDot, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_ABOVERING, new int[]{VoidSymbol, Dead_AboveRing, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_ACUTE, new int[]{VoidSymbol, Dead_Acute, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_BREVE, new int[]{VoidSymbol, Dead_Breve, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_CARON, new int[]{VoidSymbol, Dead_Caron, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_CIRCUMFLEX, new int[]{VoidSymbol, Dead_Circumflex, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_DIAERESIS, new int[]{VoidSymbol, Dead_Diaeresis, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_DOUBLEACUTE, new int[]{VoidSymbol, Dead_DoubleAcute, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_GRAVE, new int[]{VoidSymbol, Dead_Grave, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_IOTA, new int[]{VoidSymbol, Dead_Iota, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_MACRON, new int[]{VoidSymbol, Dead_Macron, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_OGONEK, new int[]{VoidSymbol, Dead_Ogonek, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_SEMIVOICED_SOUND, new int[]{VoidSymbol, Dead_SemiVoiced_Sound, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_TILDE, new int[]{VoidSymbol, Dead_Tilde, VoidSymbol, VoidSymbol, VoidSymbol}), new KeySymbol (KeyEvent.VK_DEAD_VOICED_SOUND, new int[]{VoidSymbol, Dead_Voiced_Sound, VoidSymbol, VoidSymbol, VoidSymbol})};

	public static int translateFxKeyEvent (javafx.scene.input.KeyEvent ev) {
		javafx.scene.input.KeyCode fxKeyCode = ev.getCode ();

		boolean controlDown = ev.isControlDown ();
		boolean shiftDown = ev.isShiftDown ();

		char keyChar = KeyEvent.CHAR_UNDEFINED;
		String keyCharStr = fxKeyCode.impl_getChar ();
		if (keyCharStr != null && keyCharStr.length () > 0) {
			keyChar = keyCharStr.charAt (0);
			System.out.println ("KeyCharStr " + keyCharStr);
		}
		if (!shiftDown) {
			keyChar = Character.toLowerCase (keyChar);
		}
		System.out.println ("keyChar " + keyChar);

		int awtKeyCode = fxKeyCode.impl_getCode ();

		int location = KeyEvent.KEY_LOCATION_UNKNOWN;

		return translateAwtKeyEvent (controlDown, shiftDown, keyChar, location, awtKeyCode);
	}

	public static int translateAwtKeyEvent (KeyEvent ev) {
		boolean controlDown = ev.isControlDown ();
		boolean shiftDown = ev.isShiftDown ();
		int location = ev.getKeyLocation ();
		char keyChar = ev.getKeyChar ();
		int keyCode = ev.getKeyCode ();
		return translateAwtKeyEvent (controlDown, shiftDown, keyChar, location, keyCode);
	}

	private static int translateAwtKeyEvent (boolean controlDown, boolean shiftDown, char keyChar, int location, int keyCode) {

		// First check for function keys
		if (keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F12) {
			return Keysyms.F1 + keyCode - KeyEvent.VK_F1;
		}
		if (keyCode >= KeyEvent.VK_F13 && keyCode <= KeyEvent.VK_F24) {
			return Keysyms.F13 + keyCode - KeyEvent.VK_F13;
		}
		// Numpad numbers
		if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9 && location == KeyEvent.KEY_LOCATION_NUMPAD) {
			return Keysyms.KP_0 + keyCode - KeyEvent.VK_0;
		}
		//TODO Implement OS X behavior
		//			if (VncViewer.os.startsWith ("mac os x")) {
		//				// Alt on OS X behaves more like AltGr on other systems, and to get
		//				// sane behaviour we should translate things in that manner for the
		//				// remote VNC server. However that means we lose the ability to use
		//				// Alt as a shortcut modifier. Do what RealVNC does and hijack the
		//				// left command key as an Alt replacement.
		//				switch (keyCode) {
		//					case KeyEvent.VK_META:
		//						if (location == KeyEvent.KEY_LOCATION_LEFT) {
		//							return Alt_L;
		//						} else {
		//							return Super_L;
		//						}
		//					case KeyEvent.VK_ALT:
		//						if (location == KeyEvent.KEY_LOCATION_LEFT) {
		//							return Alt_L;
		//						} else {
		//							return ISO_Level3_Shift;
		//						}
		//				}
		//			}
		// Then other special keys
		if (keyCode == KeyEvent.VK_PAUSE) {
			return controlDown ? Break : Pause;
		} else if (keyCode == KeyEvent.VK_PRINTSCREEN) {
			return controlDown ? Sys_Req : Print;
		} else if (keyCode == KeyEvent.VK_ESCAPE) {
			return Escape;
		} else if (keyCode == KeyEvent.VK_ENTER) {
			return Return;
		} else if (keyCode == KeyEvent.VK_BACK_SPACE) {
			return BackSpace;
		} else if (keyCode == KeyEvent.VK_TAB) {
			return Tab;
		} else if (keyCode == KeyEvent.VK_INSERT) {
			return Insert;
		} else if (keyCode == KeyEvent.VK_NUM_LOCK) {
			return Num_Lock;

			/*

			 public static final int BackSpace = 0xFF08;
			 public static final int Tab = 0xFF09;
			 public static final int Linefeed = 0xFF0A;
			 public static final int Clear = 0xFF0B;
			 public static final int Return = 0xFF0D;
			 public static final int Pause = 0xFF13;
			 public static final int Scroll_Lock = 0xFF14;
			 public static final int Sys_Req = 0xFF15;
			 public static final int Escape = 0xFF1B;
			 public static final int Delete = 0xFFFF;
			 public static final int Select = 0xFF60;
			 public static final int Print = 0xFF61;
			 public static final int Execute = 0xFF62;
			 public static final int Insert = 0xFF63;
			 public static final int Undo = 0xFF65;
			 public static final int Redo = 0xFF66;
			 public static final int Menu = 0xFF67;
			 public static final int Find = 0xFF68;
			 public static final int Cancel = 0xFF69;
			 public static final int Help = 0xFF6A;
			 public static final int Break = 0xFF6B;
			 public static final int Mode_switch = 0xFF7E;
			 public static final int script_switch = 0xFF7E;
			 public static final int Num_Lock = 0xFF7F;
			 */
		} else {
			for (int i = 0; i < keySymbols.length; i++) {
				if (keySymbols[i].keycode == keyCode) {
					return (keySymbols[i].keysym)[location];
				}
			}
		}
		// Unknown special key?
		if (KeyEvent.getKeyText (keyCode).isEmpty ()) {
//			String fmt = ev.paramString ().replaceAll ("%", "%%");
			VncCommon.getLogger ().warning ("Unknown key code: " + keyCode);// + String.format (fmt.replaceAll (",", "%n       ")));
			return VoidSymbol;
		}
//		char keyChar = ev.getKeyChar ();
		if (!controlDown && keyChar != KeyEvent.CHAR_UNDEFINED) {
			return UnicodeToKeysym.ucs2keysym (Character.toString (keyChar).codePointAt (0));
		}
		int key = keyChar;
		if (controlDown) {
			// For CTRL-<letter>, CTRL is sent separately, so just send <letter>.
			if ((key >= 1 && key <= 26 && !shiftDown)
					|| // CTRL-{, CTRL-|, CTRL-} also map to ASCII 96-127
					(key >= 27 && key <= 29 && shiftDown)) {
				key += 96;
			} // For CTRL-SHIFT-<letter>, send capital <letter> to emulate behavior
			else if (key < 32) {
				key += 64;
			} // Windows and Mac sometimes return CHAR_UNDEFINED with CTRL-SHIFT
			else if (keyChar == KeyEvent.CHAR_UNDEFINED && keyCode >= 0 && keyCode <= 127) {
				key = keyCode;
			}
		}
		return UnicodeToKeysym.ucs2keysym (key);
	}

}
