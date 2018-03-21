package me.vijaychavda.rdfr;

import java.io.FileWriter;
import java.io.IOException;
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

        Model rmodel = do_reify(inputPath);
        try (FileWriter writer = new FileWriter(outputPath)) {
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
