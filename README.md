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