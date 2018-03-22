package me.vijaychavda.rdfr;

import java.io.FileWriter;
import java.io.IOException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * A helper class to reify given RDF data-set.
 *
 * @author Vijay
 */
public class Reifier {

    /**
     * Reifies the given RDF file located at <i>inputPath</i>, and stores the
     * output at <i>outputPath</i> in <i>format</format> format.
     *
     * @param inputPath Given input path where RDF data-set exists.
     * @param outputPath Given output path, where reified RDF data-set will be
     * stored.
     * @param format The format for reified RDF data-set.
     * @return A JENA model with reified statements.
     * @throws IOException Throws an <b>IOException</b> when this method fails
     * to write reified data to output file.
     */
    static Model reify(String inputPath, String outputPath, String format) throws IOException {
        Model rmodel = do_reify(inputPath);
        try (FileWriter writer = new FileWriter(outputPath)) {
            rmodel.write(writer, format.toUpperCase());
        }

        return rmodel;
    }

    /**
     * Reifies the given RDF file located at <i>inputPath</i>, and stores the
     * output at <i>outputPath</i> in NT format.
     *
     * @param inputPath Given input path where RDF data-set exists.
     * @param outputPath Given output path, where reified RDF data-set will be
     * stored.
     * @return A JENA model with reified statements.
     * @throws IOException Throws an <b>IOException</b> when this method fails
     * to write reified data to output file.
     */
    static Model reify(String inputPath, String outputPath) throws IOException {
        return reify(inputPath, outputPath, "");
    }

    /**
     * Reifies the given RDF file located at <i>inputPath</i> in NT format.
     *
     * @param inputPath Given input path where RDF data-set exists. The reified
     * data-set will be overriden in this same file.
     * @return A JENA model with reified statements.
     * @throws IOException Throws an <b>IOException</b> when this method fails
     * to write reified data in the given file.
     */
    static Model reify(String inputPath) throws IOException {
        return reify(inputPath, "", "");
    }

    /**
     * A helper method to do the actual reification.
     *
     * @param rdfFilePath Path to the file which contains input RDF data-set.
     * @return A reified model.
     */
    private static Model do_reify(String rdfFilePath) {
        Model rmodel = ModelFactory.createDefaultModel();   //Reified model.

        Model model = ModelFactory.createDefaultModel();    //Existing model.
        model.read(rdfFilePath);

        StmtIterator listStatements = model.listStatements();
        while (listStatements.hasNext()) {
            rmodel.createReifiedStatement(listStatements.nextStatement());
        }

        return rmodel;
    }

}
