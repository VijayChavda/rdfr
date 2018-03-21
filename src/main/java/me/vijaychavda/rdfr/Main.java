package me.vijaychavda.rdfr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author Vijay
 */
public class Main {

    private static void displayHelpAndExit() {
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

        System.exit(0);
    }

    public static void main(String[] args) {

        if (args.length == 0 || !args[0].equals("-reify") && !args[0].equals(
            "-add-meta") && !args[0].equals("-help")) {
            showUsageAndExit();
        }

        if (args[0].equals("-help")) {
            displayHelpAndExit();
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

        EnsureValidInputPath(inputPath);

        EnsureAValidOutputPath(outputPath, inputPath);

        format = processFormat(format);

        Model rmodel;
        try {
            rmodel = Reifier.reify(inputPath, outputPath, format);
        } catch (IOException | IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        if (args[0].equals("-add-meta"))
            AddMetaWorker.create(inputPath, metaPath).inFormat(format)
                .addMeta(rmodel);
    }

    private static void showUsageAndExit() {
        System.err.println("Usage: rdfr <MODE> <INPUT_RDF_PATH> [OPTIONS...]");
        System.err.println("Use rdfr -help for more details.");
        System.exit(0);
    }

    private static void EnsureValidInputPath(String inputPath) {
        if (inputPath == null || inputPath.isEmpty())
            System.err.println("Path to input RDF file is missing.");

        if (!Files.exists(Paths.get(inputPath)))
            System.err.println(
                "Could not find input RDF file. Ensure that the file exist, and is readable.");
    }

    private static void EnsureAValidOutputPath(String outputPath,
        String defaultName) {
        Path defaultOutputPath = Paths.get(new File(defaultName).getParent(),
            "reified-" + Paths.get(defaultName).getFileName()
        );

        File outputFile;
        if (outputPath == null || outputPath.isEmpty()) {
            outputFile = defaultOutputPath.toFile();
            outputFile.createNewFile();
        } else if (!Files.exists(Paths.get(outputPath))) {
            outputFile = new File(outputPath);
            outputFile.createNewFile();
        } else {
            outputFile = new File(outputPath);
        }

        if (!outputFile.exists() || !outputFile.canWrite()) {
            System.err.println(
                "Could not create a writable output RDF file at given path: " +
                outputPath);
        }
    }

    private static String processFormat(String format) {
        if (format == null || format.isEmpty())
            format = "NT";

        switch (format.toUpperCase()) {
            case "NT":
                format = "NT";
                break;
            case "NQ":
                format = "NQ";
                break;
            case "TTL":
                format = "TTL";
                break;
            case "XML":
                format = "RDF/XML";
                break;
            case "JSON":
                format = "RDF/JSON";
                break;
            default:
                throw new IllegalArgumentException(
                    "Unsupported RDF format: " + format);
        }

        return format;
    }
}
