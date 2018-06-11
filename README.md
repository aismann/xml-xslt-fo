## XML - XSLT - FO - Renderer

Programm zur Umwandlung einer XML in eine PDF oder HTML Datei mittels  
einer XSL-FO oder XSL Vorlagedatei.

## Benutzung

In einem Terminal in den Ordner mit dem entpackten Archiv wechseln und  
folgendes in die Kommandozeile eingeben:

    Programmaufruf: bin/XmlXslFoRenderer[.bat] [-c <fop-config>] [-h] 
                [-html <Dateiname>] [-pdf <Dateiname>] -xml <Datei> -xsl <Datei>

    Programm zur Umwandlung einer XML in eine PDF oder HTML Datei mittels einer
    XSL-FO oder XSL Vorlagedatei.

     -c,--config <fop-config>   FOP Transformationskonfigurationsdatei. Vorgabe:
                                "fop-config.xconf"
     -h,--help                  Diese Nachricht.
     -html <Dateiname>          Ausgabeformat HTML.
     -pdf <Dateiname>           Ausgabeformat PDF.
     -xml <Datei>               XML Eingabedatei.
     -xsl <Datei>               XSL Vorlagedatei.

    Ist kein Ausgabedateiname angegeben, wird als Basis der XML Dateiname verwendet.


`<Datei>` ist jeweils durch den richtigen Dateinamen zu  
ersetzen und sollte sich in diesem Verzeichnis befinden.

## FOP - Konfiguration

Zusätzliche Anpassungen für den FOP Transformationsprozess lassen sich  
über die Datei `fop-config.xconf` vornehmen.  Zum Beispiel:  Einbinden  
anderer Schriftarten.
Siehe auch https://xmlgraphics.apache.org/fop/2.1/fonts.html

Die Datei wird automatisch beim Programmaufruf eingebunden, sofern sie  
sich in diesem Verzeichnis befindet!

Alternativ lässt sich auch eine andere Datei über den entsprechenden  
Parameter einbinden.
