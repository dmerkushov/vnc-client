# vnc-client readme

`vnc-client` is an original, pure-Java implementation of a VNC client, written from scratch.

It is an alternative to a few Java VNC viewers ([TightVNC](http://tightvnc.com), [TigerVNC](https://tigervnc.org), etc.) as they are all just copies of the Java viewer shipped with [RealVNC](https://www.realvnc.com).

**pro**:
  - designed to use as a library in your own Java application
  - complete support for zlib and Tight encodings used by the widely-spread TightVNC server 
  - ability to view several remote framebuffers at once
  - thumbnail views
  - readable Java source not based on the C source of the VNC viewer
  
**contra**:
  - no support for a few RFB protocol features, such as
    - clipboard support
    - ZRLE encoding
    - framebuffer size changing
  - not completely tested (RRE, Hextile and TRLE encodings, for example)

## Requirements

**Mandatory**
  - Java 8 minimum
  
**Optional**
  - if JDK version is 11 or newer: JavaFX framework (probably [OpenJFX](https://openjfx.io)) for JavaFX support

## Settings

Settings are populated using Java Preferences. The settings are located in the system node of the preferences hierarchy, in `ru/dmerkushov/vnc/client`.

  * `LOGGING_LEVEL` - logging level. May be `ALL` (log everything), `OFF` (log nothing), or one of level names: `SEVERE`, `WARNING`, `INFO`, `CONFIG`, `FINE`, `FINER`, `FINEST`. Default is `ALL`
  * `JPEG_QUALITY` - JPEG quality when using Tight framebuffer encoding. May be `HIGH`, `MEDIUM`, `LOW`, or `NOJPEG` (don't use JPEG compression with Tight encoding). Default is `NOJPEG`.
  * `FRAMEBUFFER_UPDATE_DELAY` - delay between framebuffer update requests, in milliseconds. Default is 100.
  * `FULL_UPDATE_COUNTER` - as soon as the counter of framebuffer update requests reaches this value, it is reset and a full framebuffer update request is issued. Default is 256.
  * `MOUSEMOVE_SEND_DELAY` - delay between mouse position notifications, in milliseconds. Default is 100.

## Logging

Logging is done using the `java.util.logging` framework. The logger is named "`ru.dmerkushov.vnc.client.VncCommon`"

Setting the logging level is described in the **Settings** section.

## Developer contacts

Dmitriy Merkushov  
Moscow, Russia  
[d.merkushov@gmail.com](mailto:d.merkushov@gmail.com)  
http://dmerkushov.ru  
GitHub repository for the project: https://github.com/dmerkushov/vnc-client  

# VNC specifications

VNC communication is based on the RFB (Remote FrameBuffer) protocol, described in [RFC 6143](https://tools.ietf.org/html/rfc6143), issued in March 2011. An extended variant of RFB is described in a non-standard spec called [rfbproto](https://github.com/rfbproto/rfbproto) available on GitHub.

Copies of both the RFC 6143 and the rfbproto spec (as of December 14, 2016) are present in this repository.

# Usage

Basically. one should implement a password supplier, and then use one or more of the components to connect to the VNC server.

Then, a VNC (RFB) session must be initialized, and a password supplier must be set for the session. As a part of the session startup routine, the password supplier will be invoked.

Finally, a Swing or JavaFX component must be linked to the session, and the session may be started.

## Password supplier

One should implement the `PasswordSupplier` interface, which has the only method `getPassword()`.

The method returns a `String` and may throw an `RfbOperationException`.

The trivial realization of a password supplier is provided: `UiPasswordSupplier` uses a Swing dialog to ask for the password.

## Swing-style

```
public static void main (String[] args) {

String host = JOptionPane.showInputDialog ("Host", "localhost");
int port = Integer.parseInt (JOptionPane.showInputDialog ("Port", "5901"));

JFrame frame = new JFrame ();
frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
frame.setSize (new java.awt.Dimension (500, 500));

RfbClientSession session = new RfbClientSession (Main.host, Main.port);
session.setPasswordSupplier (new UiPasswordSupplier ());

VncView vncView = new DefaultSwingVncView ();
vncView.setSession (session);

frame.add (vncView.getSwingComponent ());

session.startSession ();

frame.setVisible (true);

vncView.repaint ();

}
```

## JavaFX-style

```
public void start (Stage primaryStage) {

String host = JOptionPane.showInputDialog ("Host", "localhost");
int port = Integer.parseInt (JOptionPane.showInputDialog ("Port", "5901"));

final RfbClientSession session;
try {
	session = new RfbClientSession (host, port);
} catch (java.io.IOException ex) {
	Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
	return;
}
session.setPasswordSupplier (new UiPasswordSupplier ());

VncView vncView = new DefaultJavaFxVncView ();
vncView.setSession (session);

try {
	session.startSession ();
} catch (RfbSessionException | java.io.IOException ex) {
	Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
}

HBox root = new HBox ();
root.getChildren ().add (vncView.getJavafxNode ());
primaryStage.setScene (new Scene (root, 1200, 800));

primaryStage.setOnHiding (event -> Platform.runLater (() -> {
	try {
		session.setSessionState (RfbSessionState.Finished);
	} catch (RfbSessionException ex) {
		Logger.getLogger (Main.class.getName ()).log (Level.SEVERE, null, ex);
	}
}));

primaryStage.show ();

}
```

# Dive-in

## VNC basics

### RFB session

A VNC session is broken into three main stages:

  * Handshake
  * Initialization
  * Normal (regular) operation
  
First, a TCP connection is established by the client. Then, the handshake stage of the session is started.

Handshake stage includes:

  * adjustment of the RFB protocol version (protocol version handshake)
  * testing the client's authenticity (security handshake)

If the handshake is passed successfully, the routine continues with the initialization stage.

Initialization stage includes:

  * the client informs the server if the session should be shared among several clients
  * the server informs the client of the preferred pixel representation and the session identification

After that, goes the normal (regular) operation. The client sends framebuffer update requests, pointer and keyboard events. The server sends framebuffer updates. Other supplementary messages are probable, too:

  * by the client
    * set pixel format
    * set available framebuffer representation formats (set encodings) and/or inform of the supported RFB extensions, using the so-called pseudo encodings
    * set text in the clibboard (client cut text)
    * other messages not described in RFC 6143, but approved by the client and the server, according to the supported RFB extensions
  * by the server
    * beep (bell)
    * set colormap entries
    * set text in the clipboard (server cut text)
    * other messages not described in RFC 6143, but approved by the client and the server, according to the supported RFB extensions

Approval of the supported RFB extensions on the client side is usually reached by the set encodings message including one or more pseudo encodings. Agreement of the supported RFB extensions on the server side is usually reached by sending framebuffer update messages using these pseudo encodings. But anyway, the approval procedure depends on the extension specification.  

### Some standardized framebuffer encodings

According to RFC 6143, the following framebuffer encodings are allowed:

  * **Raw** - a direct representation of a framebuffer as a rectangular array of pixels (MUST be supported by every VNC client and every VNC server)
  * CopyRect - copying of a previously-sent rectangle to another location
  * RRE - a group of rectangles of the same color and differrent sizes
  * Hextile - a variadic encoding of tiles sized 16x16 pixels
  * TRLE - another variadic encoding, more effective than Hextile
  * ZRLE - Zlib-compressed TRLE

rfbproto adds the following encodings:

  * CoRRE
  * **zlib**
  * **Tight**
  * zlibhex

**Bold** in these two lists are the encodings that are supported AND tested with this vnc-client.

Actually, there are much more framebuffer encodings than described in RFC 6143 and rfbproto, many of them proprietary.

## vnc-client source

### Licensing

vnc-client is licensed with GNU General Public License 2. A copy of the license is supplied within the repository.

Though the author's favorite is the Apache License, two of the project's classes (`ru.dmerkushov.vnc.client.ui.events.Keysyms` и `ru.dmerkushov.vnc.client.rfb.messages.handshake.SecurityHandshake4_VNCauth_C2S.DesCipher`) are based on classes of the TigerVNC Java viewer that is licensed with GPLv2. Hence, this work may be seen as a derived work in GPL terms.

### Source hierarchy

Source java packages:

  * `ru.dmerkushov.vnc.client` - base package, contains classes: `Main` (a usage sample), `VncCommon` (common routines), `VncException` (base class for the client's exceptions)
    * `rfb` - RFB protocol realization
      * `data` - basic RFB data: `RfbColorMap` (palette), `RfbPixelFormat` (format for the pixel representation), `RfbRectangle` (framebuffer rectangle)
        * `pixeldata` - graphic data representations (encodings)
      * `messages` - RFB messages, broken by RFB connection stages: `handshake`, `initialization`, `normal`
      * `operation` - realizations of RFB connection stages
      * `session` - client-side representation of an RFB session (`RfbClientSession`) and utility classes for it
        * `password` - `PasswordSupplier` interface and its default implementation, `UiPasswordSupplier`
    * `ui` - GUI classes:
      * `VncView` - an interface that must be implemented to connect a view component to a client session
      * `DefaultJavaFxVncView` - default implementation of `VncView`, JavaFX-style
      * `DefaultSwingVncView` - default implementation of `VncView`, Swing-style
      * `ThumbnailJavaFxVncView` - auto-resizing implementation of `VncView` (for the purpose of thumbnail view of the remote framebuffer), JavaFX-style
      * `ThumbnailSwingVncView` - auto-resizing implementation of `VncView` (for the purpose of thumbnail view of the remote framebuffer), Swing-style
      * `events` - events to send to the VNC server
