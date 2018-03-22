package me.vijaychavda.rdfr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author Vijay
 */
public class Main {

    public static void main(String[] args) {

        if (args.length <= 1 || !args[0].equals("-reify") && !args[0].equals(
            "-add-meta") && !args[0].equals("-help")) {
            showUsageAndExit();
        }

        if (args[0].equals("-help")) {
            showHelpAndExit();
        }

        int arg = 0;

        String mode = args[0];
        String inputPath, outputPath, format;
        String metaPath, subjectURI, propertyURI, objectURI;

        inputPath = args[++arg];
        ensureValidInputPathOrExit(inputPath, "input");

        outputPath = format = null;
        metaPath = subjectURI = propertyURI = objectURI = null;

        if (args[0].equals("-add-meta")) {
            metaPath = args[++arg];
            ensureValidInputPathOrExit(metaPath, "meta");
        }

        while (arg + 1 < args.length) {
            switch (args[++arg]) {
                case "-o":
                    outputPath = ensureValidOutputPathOrExit(args[++arg],
                        inputPath, mode);
                    break;
                case "-f":
                    format = ensureSupportedFormatOrExit(args[++arg]);
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

        Model rmodel = null;
        try {
            rmodel = Reifier.reify(inputPath, outputPath, format);
        } catch (IOException | IllegalArgumentException ex) {
            showErrorAndExit(
                "Reification failed. Error has been logged at: TODO", ex
            );
        }

        if (mode.equals("-add-meta")) {
            try {
                AddMetaWorker.create(inputPath, metaPath)
                    .targetHas(subjectURI, propertyURI, objectURI)
                    .storeOutputAt(outputPath)
                    .inFormat(format)
                    .addMeta(rmodel);
            } catch (IOException ex) {
                showErrorAndExit(
                    "Adding meta-data failed. Error has been logged at: TODO",
                    ex
                );
            }
        }
    }

    private static void ensureValidInputPathOrExit(String inputPath,
        String input) {
        if (inputPath == null || inputPath.isEmpty()) {
            showErrorAndExit("Path to " + input + " RDF file is missing.", null);
        }

        if (!Files.exists(Paths.get(inputPath))) {
            showErrorAndExit(
                "Could not find " + input + " RDF file at location: '" +
                inputPath +
                "'.\nEnsure that the file exist, and is readable.", null
            );
        }
    }

    //assumes inputPath is valid.
    private static String ensureValidOutputPathOrExit(String outputPath,
        String inputPath, String mode) {

        if (outputPath == null || outputPath.isEmpty())
            return inputPath;

        String inputFileName = (mode.equals("-reify") ? "reified-"
            : "augmented-")
            .concat(Paths.get(inputPath).getFileName().toString());

        Path oPath = Paths.get(outputPath);

        try {
            if (Files.isDirectory(oPath))
                Files.createDirectories(oPath);
            else
                Files.createDirectories(oPath.getParent());
        } catch (FileExistsException ex) {
            showErrorAndExit(
                "Failed to create directories in the output path: " +
                oPath.toString() +
                ". Make sure a file with given directory name does not already exist.",
                ex
            );
        } catch (IOException ex) {
            showErrorAndExit(
                "Failed to create directories in the output path: " +
                oPath.toString(), ex
            );
        }

        try {
            if (Files.isDirectory(oPath)) {
                File outputFile = new File(outputPath, inputFileName);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                oPath = outputFile.toPath();
            }

            if (!Files.exists(oPath))
                oPath = Files.createFile(oPath);

            if (Files.isWritable(oPath)) {
                return oPath.toString();
            } else {
                showErrorAndExit(
                    "Failed to create a writable output file at location: " +
                    oPath, null
                );
            }
        } catch (IOException ex) {
            showErrorAndExit(
                "Problem occured while creating output file at location: " +
                oPath, ex
            );
        }

        return null;
    }

    private static String ensureSupportedFormatOrExit(String format) {
        if (format == null || format.isEmpty())
            return "NT";

        switch (format.toUpperCase()) {
            case "NT":
                return "NT";
            case "NQ":
                return "NQ";
            case "TTL":
                return "TTL";
            case "XML":
                return "RDF/XML";
            case "JSON":
                return "RDF/JSON";
            default:
                showErrorAndExit("Unsupported RDF format: " + format, null);
        }

        return format;
    }

    private static void showUsageAndExit() {
        System.err.println("Usage: rdfr <MODE> <INPUT_RDF> [OPTIONS...]");
        System.err.println("Use rdfr -help for more details.");
        System.exit(0);
    }

    private static void showHelpAndExit() {
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

    private static void showErrorAndExit(String message, Exception ex) {
        System.err.println(message);
        if (ex != null)
//            Logger.getLogger(Main.class.getName())
//                .log(Level.SEVERE, message, ex);
        System.exit(-1);
    }
}
