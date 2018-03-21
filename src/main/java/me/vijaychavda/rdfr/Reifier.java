package me.vijaychavda.rdfr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;

/**
 *
 * @author Vijay
 */
public class Reifier {

    public static Model reify(String inputPath, String outputPath, String format)
        throws IOException, IllegalArgumentException {

        if (inputPath == null || inputPath.isEmpty())
            throw new IllegalArgumentException(
                "Path to input RDF file is missing.");

        if (!Files.exists(Paths.get(inputPath)))
            throw new IllegalArgumentException(
                "Could not find input RDF file. Ensure that the file exist, and is readable.");

        Path defaultOutputPath = Paths.get(new File(inputPath).getParent(),
            "reified-" + Paths.get(inputPath).getFileName()
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
            throw new IOException(
                "Could not create a writable output RDF file at given path: " +
                outputPath);
        }

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

        Model rmodel = do_reify(inputPath);
        try (FileWriter writer = new FileWriter(outputFile)) {
            rmodel.write(writer, format.toUpperCase());
        }

        return rmodel;
    }

    public static Model reify(String inputPath, String outputPath)
        throws IOException, IllegalArgumentException {
        return reify(inputPath, outputPath, "");
    }

    public static Model reify(String inputPath)
        throws IOException, IllegalArgumentException {
        return reify(inputPath, "", "");
    }

    private static Model do_reify(String rdfFilePath) {
        Model rmodel = ModelFactory.createDefaultModel();

        Model model = ModelFactory.createDefaultModel();
        model.read(rdfFilePath);

        StmtIterator listStatements = model.listStatements();
        while (listStatements.hasNext()) {
            rmodel.createReifiedStatement(listStatements.nextStatement());
        }

        return rmodel;
    }
}
