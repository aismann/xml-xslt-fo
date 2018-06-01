package com.github.euvbonk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author euvbonk
 * @version 17.05.2018
 */
public class XmlXslFoRenderer
{
    private static final String USAGE = "" +
            "Programm zur Umwandlung einer XML in eine PDF Datei mittels\n" +
            "einer XSL-FO Vorlagedatei.\n\n" +
            "Ist kein Ausgabe-Name angegeben, wird als Basis der XML Datei-\n" +
            "name verwendet.\n\n" +
            "Programmaufruf:\n" +
            "\tXmlXslFoRenderer[.bat] xml-datei xsl-fo-datei [ausgabe-name]\n";

    public static void main(String[] args) throws IOException, SAXException, TransformerException
    {
        if (args.length < 2)
        {
            System.out.println(USAGE);
            System.exit(1);
        }
        new XmlXslFoRenderer().convertToPDF(args[0], args[1], args.length > 2 ? args[2] : null);
    }

    @SuppressWarnings("unused")
    private void convertToHTML(final String xml, final String xsl)
            throws IOException, SAXException, ParserConfigurationException, TransformerException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //factory.setNamespaceAware(true);
        //factory.setValidating(true);
        File stylesheet = new File(xsl);
        File datafile = new File(xml);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(datafile);

        // Use a Transformer for output
        TransformerFactory tFactory = TransformerFactory.newInstance();
        StreamSource stylesource = new StreamSource(stylesheet);
        Transformer transformer = tFactory.newTransformer(stylesource);

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(System.out);
        transformer.transform(source, result);
    }

    private void convertToPDF(final String xml, final String xsl, String pdf)
            throws IOException, SAXException, TransformerException
    {
        File xsltFile = new File(xsl);
        File xmlFile = new File(xml);
        StreamSource xmlSource = new StreamSource(xmlFile);
        FopFactory fopFactory = FopFactory.newInstance();
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        if (pdf == null)
        {
            pdf = (xmlFile.getParent() != null ? xmlFile.getParent() : ".") + "/" + xmlFile.getName().replace(".xml", ".pdf");
        }
        FileOutputStream out = new FileOutputStream(pdf);
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));
        SAXResult result = new SAXResult(fop.getDefaultHandler());
        transformer.transform(xmlSource, result);
        out.close();
    }
}
