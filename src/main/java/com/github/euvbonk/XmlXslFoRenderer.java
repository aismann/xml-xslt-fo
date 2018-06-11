package com.github.euvbonk;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author euvbonk
 * @version 11.06.2018
 */
public class XmlXslFoRenderer
{
    private static final String FOP_CONFIG_FILE_NAME = "fop-config.xconf";

    private static final String USAGE = XmlXslFoRenderer.class.getSimpleName();
    private static final String HEADER = "\nProgramm zur Umwandlung einer XML in eine PDF oder HTML Datei mittels " +
                "einer XSL-FO oder XSL Vorlagedatei.\n\n";
    private static final String FOOTER = "\nIst kein Ausgabedateiname angegeben, wird als Basis der XML Datei" +
            "name verwendet.";

    private static final Options OPTIONS = new Options();
    private static final CommandLineParser COMMAND_LINE_PARSER = new DefaultParser();
    private static final HelpFormatter HELP_FORMATTER = new HelpFormatter();

    static
    {
        OPTIONS.addOption(
                Option.builder("c").longOpt("config").optionalArg(true).hasArg().argName("fop-config").type(File.class)
                        .desc("FOP Transformationskonfigurationsdatei. Vorgabe: \"" + FOP_CONFIG_FILE_NAME + "\"").build());
        OPTIONS.addOption(
                Option.builder("xml").required().hasArg().argName("Datei").type(File.class).desc("XML Eingabedatei.")
                        .build());
        OPTIONS.addOption(
                Option.builder("xsl").required().hasArg().argName("Datei").type(File.class).desc("XSL Vorlagedatei.")
                        .build());
        OPTIONS.addOption(Option.builder("pdf").optionalArg(true).hasArg().argName("Dateiname").type(String.class)
                .desc("Ausgabeformat PDF.").build());
        OPTIONS.addOption(Option.builder("html").optionalArg(true).hasArg().argName("Dateiname").type(String.class)
                .desc("Ausgabeformat HTML.").build());
        OPTIONS.addOption(
                Option.builder("h").longOpt("help").optionalArg(true).hasArg(false).desc("Diese Nachricht.").build());

        HELP_FORMATTER.setSyntaxPrefix("Programmaufruf: ");
    }

    public static void main(String[] args)
            throws IOException, SAXException, TransformerException, ParserConfigurationException
    {
        File xml = null;
        File xsl = null;
        File fopConfig = new File(FOP_CONFIG_FILE_NAME);

        try
        {
            CommandLine cmd = COMMAND_LINE_PARSER.parse(OPTIONS, args);
            if (args.length < 5 || cmd.hasOption("h") ||
                    (cmd.hasOption("pdf") && cmd.hasOption("html")) || (!cmd.hasOption("pdf") && !cmd.hasOption("html")))
            {
                printUsageAndExit();
            }
            if (cmd.hasOption("xml"))
            {
                xml = (File)cmd.getParsedOptionValue("xml");
            }
            if (cmd.hasOption("xsl"))
            {
                xsl = (File)cmd.getParsedOptionValue("xsl");
            }
            if (xml == null || !xml.exists() || xsl == null || !xsl.exists())
            {
                printUsageAndExit();
            }
            if (cmd.hasOption("pdf") && !cmd.hasOption("html"))
            {
                if (cmd.hasOption("c"))
                {
                    fopConfig = (File)cmd.getParsedOptionValue("c");
                }
                new XmlXslFoRenderer().convertToPDF(xml, xsl, fopConfig, cmd.getOptionValue("pdf", null));
            }
            if (cmd.hasOption("html") && !cmd.hasOption("pdf"))
            {
                if (xml != null && xml.exists() && xsl != null && xsl.exists())
                {
                    new XmlXslFoRenderer().convertToHTML(xml, xsl, cmd.getOptionValue("html", null));
                }
            }
        }
        catch (ParseException e)
        {
            printUsageAndExit();
        }
    }

    private void convertToHTML(final File datafile, final File stylesheet, String html)
            throws IOException, SAXException, ParserConfigurationException, TransformerException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //factory.setNamespaceAware(true);
        //factory.setValidating(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(datafile);

        // Use a Transformer for output
        TransformerFactory tFactory = TransformerFactory.newInstance();
        StreamSource stylesource = new StreamSource(stylesheet);
        Transformer transformer = tFactory.newTransformer(stylesource);

        DOMSource source = new DOMSource(document);
        if (html == null)
        {
            html = (datafile.getParent() != null ? datafile.getParent() : ".") + "/" + datafile.getName().replace(".xml", ".html");
        }
        if (!html.endsWith(".html"))
        {
            html = html.concat(".html");
        }
        File file = new File(html);
        try (final FileWriter fw = new FileWriter(file)) {
            StreamResult result = new StreamResult(fw);
            transformer.transform(source, result);
        }
        // open created html file in browser
        if (Desktop.isDesktopSupported())
        {
            Desktop.getDesktop().browse(file.toURI());
        }
    }

    private void convertToPDF(final File xmlFile, final File xsltFile, final File fopConfig, String pdf)
            throws IOException, SAXException, TransformerException
    {
        StreamSource xmlSource = new StreamSource(xmlFile);
        FopFactory fopFactory = FopFactory.newInstance();
        if (fopConfig != null && fopConfig.exists())
        {
            fopFactory.setUserConfig(fopConfig);
        }
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        if (pdf == null)
        {
            pdf = (xmlFile.getParent() != null ? xmlFile.getParent() : ".") + "/" + xmlFile.getName().replace(".xml", ".pdf");
        }
        if (!pdf.endsWith(".pdf"))
        {
            pdf = pdf.concat(".pdf");
        }
        FileOutputStream out = new FileOutputStream(pdf);
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));
        SAXResult result = new SAXResult(fop.getDefaultHandler());
        transformer.transform(xmlSource, result);
        out.close();
    }

    private static void printUsageAndExit()
    {
        HELP_FORMATTER.printHelp(80, USAGE, HEADER, OPTIONS, FOOTER, true);
        System.exit(0);
    }
}
