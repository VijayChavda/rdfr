package me.vijaychavda.rdfr;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author Vijay
 */
public class Main {

    public static void main(String[] args) {

        if (args.length == 0 || !args[0].equals("-reify") &&
            !args[0].equals("-add-meta") && !args[0].equals("-help")) {
            showUsageAndExit();
        }

        if (args[0].equals("-help")) {
            try {
                InputStream resourceAsStream = Main.class.getResourceAsStream(
                    "/manual.txt");
                System.out.println(
                    IOUtils.toString(resourceAsStream, (String) null)
                );
            } catch (IOException ex) {
                System.err.println("Failed to load the manual.");
//                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }

        String inputPath, outputPath, metaPath, subjectURI, propertyURI, objectURI;
        outputPath = metaPath = subjectURI = propertyURI = objectURI = null;

        String format = "NT";

        int arg = 0;

        inputPath = args[++arg];

        if (args[0].equals("-add-meta")) {
            metaPath = args[++arg];
        }

        while (arg + 1 < args.length) {
            switch (args[++arg]) {
                case "-o":
                    outputPath = args[++arg];
                    break;
                case "-f":
                    format = args[++arg];
                    break;
                case "-s":
                    subjectURI = args[++arg];
                    break;
                case "-p":
                    propertyURI = args[++arg];
                    break;
                case "-v":
                    objectURI = args[++arg];
                    break;
                default:
                    //ignore
                    break;
            }
        }

        Model rmodel;
        try {
            rmodel = Reifier.reify(inputPath, outputPath, format);
        } catch (IOException | IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        if (args[0].equals("-reify"))
            return;

        AddMetaWorker.create(inputPath, metaPath)
            .addMeta(rmodel);
    }

    private static void showUsageAndExit() {
        System.err.println("Usage: rdfr <MODE> <INPUT_RDF_PATH> [OPTIONS...]");
        System.err.println("Use rdfr -help for more details.");
        System.exit(0);
    }
}
