package me.vijaychavda.rdfr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;

/**
 * The main class. It is main. Main class. Yes, Main. The MAIN class!!
 *
 * @author Vijay
 */
public class Main {

    /**
     * Entry point for this program. See manual for usage.
     *
     * @param args See the manual please :)
     */
    public static void main(String[] args) {
        //Ensure proper usage of program.
        if (args.length <= 1 || !args[0].equals("-reify") && !args[0].equals("-add-meta") && !args[0].equals("-help")) {
            showUsageAndExit();
        }

        //Show help
        if (args[0].equals("-help")) {
            showHelpAndExit();
        }

        int arg = 0;

        String mode = args[0];  //Mode can be "-reify" or "-add-meta".
        String inputPath, outputPath, format;   //General variables.
        String metaPath, subjectURI, propertyURI, objectURI;    //Used in adding meta-information

        //Read and validate input path.
        inputPath = args[++arg];
        ensureValidInputPathOrExit(inputPath, "input");

        //Read and validate meta-data path, if mode is "-add-meta".
        if (args[0].equals("-add-meta")) {
            metaPath = args[++arg];
            ensureValidInputPathOrExit(metaPath, "meta");
        }

        metaPath = subjectURI = propertyURI = objectURI = null;
        outputPath = inputPath; //By default, input file will is overriden.
        format = "NT";  //Default output format.

        //<editor-fold defaultstate="collapsed" desc="Reading CLAs">
        while (arg + 1 < args.length) {
            switch (args[++arg]) {
                case "-o":
                    outputPath = ensureValidOutputPathOrExit(args[++arg], inputPath, mode);
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
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Reification">
        Model rmodel = null;
        try {
            rmodel = Reifier.reify(inputPath, outputPath, format);
        } catch (IOException | IllegalArgumentException ex) {
            showErrorAndExit(
                "Reification failed. Error has been logged at: TODO", ex
            );
        }
        //</editor-fold>

        if (mode.equals("-add-meta")) {
            //<editor-fold defaultstate="collapsed" desc="Add meta-data">
            try {
                AddMetaWorker
                    .create(inputPath, metaPath)
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
            //</editor-fold>
        }
    }

    /**
     * Ensures that given path to any input file is a valid one; meaning that a
     * readable RDF file must exist there.
     *
     * @param path The given input path.
     * @param input Mode in which this program is running (<i>-reify</i> or
     * <i>-add-meta</i>)
     */
    private static void ensureValidInputPathOrExit(String path, String input) {
        if (path == null || path.isEmpty()) {
            showErrorAndExit("Path to " + input + " RDF file is missing.", null);
        }

        if (!Files.exists(Paths.get(path))) {
            showErrorAndExit(
                MessageFormat.format(
                    "Could not find {0} RDF file at location: {1}. \n" +
                    "Ensure that the file exists, and is readable",
                    input, path
                ), null
            );
        }
    }

    /**
     * Ensures that the given input path is a valid one, and that a readable
     * file exists there.
     *
     * The format of the input file is determined by it's extension, and it must
     * be one of the following:
     * <ul>
     * <li>ttl</li>
     * <li>rdf</li>
     * <li>nt</li>
     * <li>nq</li>
     * <li>json</li>
     * </ul>
     *
     * If the value of <i>outputPath</i> is <b>null</b> or "", then the input
     * file is treated as output file; meaning that input file will be
     * over-ridden. Note that it is assumed that given input file exists and is
     * readable.
     *
     * @param outputPath The given output path.
     * @param inputPath The given input path.
     * @param mode Mode in which this program is running (<i>-reify</i> or
     * <i>-add-meta</i>)
     * @return Returns path to file which exists and is writable. It may be a
     * different file than the one passed in argument of this method.
     */
    private static String ensureValidOutputPathOrExit(String outputPath, String inputPath, String mode) {
        if (outputPath == null || outputPath.isEmpty()) {
            return inputPath;
        }

        String inputFileName = (mode.equals("-reify") ? "reified-" : "augmented-")
            .concat(Paths.get(inputPath).getFileName().toString());

        Path oPath = Paths.get(outputPath);

        try {
            if (Files.isDirectory(oPath)) {
                Files.createDirectories(oPath);
            } else {
                Files.createDirectories(oPath.getParent());
            }
        } catch (FileExistsException ex) {
            showErrorAndExit(
                "Failed to create directories in the output path: " +
                oPath.toString() +
                ". Make sure a file with given path names does not already exist.",
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

            if (!Files.exists(oPath)) {
                oPath = Files.createFile(oPath);
            }

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

    /**
     * Associates user given format to the JENA specific format, while ensuring
     * that the format is supported by this application. If the given format is
     * unrecognized, the program exits.
     *
     * @param format The given format.
     * @return A valid JENA specific format.
     */
    private static String ensureSupportedFormatOrExit(String format) {
        if (format == null || format.isEmpty()) {
            return "NT";
        }

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

    /**
     * Prints the usage hint for this program to the output stream, and exits.
     */
    private static void showUsageAndExit() {
        System.err.println("Usage: rdfr <MODE> <INPUT_RDF> [OPTIONS...]");
        System.err.println("Use rdfr -help for more details.");
        System.exit(0);
    }

    /**
     * Prints the manual to output stream, and exits.
     */
    private static void showHelpAndExit() {
        try {
            InputStream istream = Main.class.getResourceAsStream("/manual.txt");
            System.out.println(IOUtils.toString(istream, (String) null));
        } catch (IOException ex) {
            showErrorAndExit("Failed to load the manual.", ex);
        }

        System.exit(0);
    }

    /**
     * Prints an error message to output stream and exits. If value of 'ex' is
     * not null, then it will also log the exception.
     *
     * @param message The message to display.
     * @param ex The exception to log.
     */
    private static void showErrorAndExit(String message, Exception ex) {
        System.err.println(message);
        if (ex != null) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, message, ex);
        }

        System.exit(-1);

    }
}
