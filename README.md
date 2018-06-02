## XML - XSLT - FO - Renderer

Programm zur Transformation eines XML Dokumentes mit einer XSL-FO Vor-  
lage zu einem PDF-Dokument.

## Benutzung

In einem Terminal in den Ordner mit dem entpackten Archiv wechseln und  
folgendes in die Kommandozeile eingeben:

bash bin/XmlXslFoRenderer [source].xml [template].xsl

Es sollte eine Datei mit Namen '[source].pdf' entstehen.

[source] und [template] sind jeweils durch die richtigen Dateinamen zu  
ersetzen und sollten sich in diesem Verzeichnis befinden.

## FOP - Konfiguration

Zusätzliche Anpassungen für den FOP Transformationsprozess lassen sich  
über die Datei 'fop-config.xconf' vornehmen.  Zum Beispiel:  Einbinden  
anderer Schriftarten.
Siehe auch https://xmlgraphics.apache.org/fop/2.1/fonts.html

Die Datei wird automatisch beim Programmaufruf eingebunden, sofern sie  
sich in diesem Verzeichnis befindet!
