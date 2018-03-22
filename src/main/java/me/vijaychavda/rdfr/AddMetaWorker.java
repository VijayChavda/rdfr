package me.vijaychavda.rdfr;

import java.io.FileWriter;
import java.io.IOException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * A helper class to add given meta-data to existing RDF data-set. You can
 * specify the subject, property or objects of statements to which meta-data
 * should be added.
 *
 * @author Vijay
 */
public class AddMetaWorker {

    private String inputPath, outputPath, metaPath, subjectURI, propertyURI, objectURI, format;

    /**
     * Adds meta-data to given RDF data-set.
     *
     * This method will reify all statements in given data-set, and add all
     * meta-data statements found at <i>metaPath</i> to statements in the given
     * data-set which have subject URI specified by <i>subjectURL</i>, property
     * URI specified by <i>propertyURI</i>, and object URI specified by
     * <i>objectURI</i>. The output RDF data-set will be written in file whose
     * path is specified by <i>outputPath</i> in format specified by
     * <i>format</i>.
     *
     * @param rmodel An RDF model with original statements, reified and with
     * added meta-data.
     * @return Instance of this object, for chaining.
     * @throws Throws an <b>IOException</b> when this method fails to write
     * reified data to output file.
     */
    Model addMeta(Model rmodel) throws IOException {
        Model model = ModelFactory.createDefaultModel().read(inputPath);

        //For each reified statement (to some of them we want to add meta-data):
        StmtIterator statements = model.listStatements();
        while (statements.hasNext()) {
            Statement statement = statements.nextStatement();

            //If any of the triplet URI is given, then to only the statements with matching 
            //respective triplet values will the meta-data be added.
            if (subjectURI != null && !statement.getSubject().getURI().equals(subjectURI) ||
                propertyURI != null && !statement.getPredicate().getURI().equals(propertyURI) ||
                objectURI != null && !statement.getObject().asNode().getURI().equals(objectURI)) {
                continue;
            }

            //An auxilary note to make the n-ary relationship as binary relationships.
            Resource bnode = rmodel.createResource();

            StmtIterator metaStatements = ModelFactory.createDefaultModel().read(metaPath).listStatements();
            while (metaStatements.hasNext()) {
                Statement metaStatement = metaStatements.nextStatement();
                //Add meta-data statement to auxilary node.
                rmodel.add(bnode, metaStatement.getPredicate(), metaStatement.getObject());
            }

            //Add the original object of statement to this auxilary node.
            rmodel.add(bnode, statement.getPredicate(), statement.getObject());

            //Add the auxilary node as object of the original statement.
            rmodel.add(statement.getSubject(), statement.getPredicate(), bnode);
        }

        //Write the new RDF data-set.
        try (FileWriter writer = new FileWriter(outputPath)) {
            rmodel.write(writer, format.toUpperCase());
        }

        return rmodel;
    }

    /**
     * A property setter for setting path to file, where output data-set will be
     * written. Keep this same as the input RDF to override the data.
     *
     * @param outputPath The path to output RDF data-set.
     * @return Instance of this object, for chaining.
     */
    AddMetaWorker storeOutputAt(String outputPath) {
        this.outputPath = outputPath;
        return this;
    }

    /**
     * A property setter for setting URIs of subject, property, and object of
     * statement, to which meta-data is to be added. Keep them null to keep them
     * as <i>wild cards</i>.
     *
     * @param subjectURI The URI of subject of statement to which meta-data is
     * to be added.
     * @param propertyURI The URI of property of statement to which meta-data is
     * to be added.
     * @param objectURI The URI of object of statement to which meta-data is to
     * be added.
     * @return Instance of this object, for chaining.
     */
    AddMetaWorker targetHas(String subjectURI, String propertyURI, String objectURI) {
        this.subjectURI = subjectURI;
        this.propertyURI = propertyURI;
        this.objectURI = objectURI;
        return this;
    }

    /**
     * A property setter for setting format of output RDF data-set.
     *
     * @param format The format for output RDF data-set.
     * @return Instance of this object, for chaining.
     */
    AddMetaWorker inFormat(String format) {
        this.format = format;
        return this;
    }

    /**
     * Creates a new instance of <b>AddMetaWorker</b>.
     *
     * @param inputPath Path to input RDF data-set, to which meta-data is to be
     * added.
     * @param metaPath Path to meta RDF data-set, which contains meta-data
     * statements.
     * @return An instance of <b>AddMetaWorker</b>.
     */
    static AddMetaWorker create(String inputPath, String metaPath) {
        AddMetaWorker worker = new AddMetaWorker();
        worker.inputPath = inputPath;
        worker.metaPath = metaPath;
        worker.outputPath = inputPath;
        worker.format = "NT";
        return worker;
    }

}
