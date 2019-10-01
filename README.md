vnc-client readme
=================

`vnc-client` is an original, pure-Java implementation of a VNC client, written from scratch.

It is an alternative to a few Java VNC viewers ([TightVNC](http://tightvnc.com), [TigerVNC](https://tigervnc.org), etc.) totally repeating the Java viewer shipped with [RealVNC](https://www.realvnc.com).

**pro**:
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

Requirements
------------

**Mandatory**
  - Java 8 minimum
  
**Optional**
  - if JDK version is 11 or newer: JavaFX framework (probably [OpenJFX](https://openjfx.io)) for JavaFX support

Developer contacts
------------------

Dmitriy Merkushov  
Moscow, Russia  
[d.merkushov@gmail.com](mailto:d.merkushov@gmail.com)  
http://dmerkushov.ru  
GitHub repository for the project: https://github.com/dmerkushov/vnc-client  

Usage
=====

Basically. one should implement a password supplier, and then use one or more of the components to connect to the VNC server.

Then, a VNC (RFB) session must be initialized, and a password supplier must be set for the session. As a part of the session startup routine, the password supplier will be invoked.

Finally, a Swing or JavaFX component must be linked to the session, and the session may be started.

Password supplier
-----------------

One should implement the `PasswordSupplier` interface, which has the only method `getPassword()`.

The method returns a `String` and may throw an `RfbOperationException`.

The trivial realization of a password supplier is provided: `UiPasswordSupplier` uses a Swing dialog to ask for the password.

Swing-style
-----------

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

JavaFX-style
------------

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

