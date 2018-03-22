package me.vijaychavda.rdfr;

import java.io.FileWriter;
import java.io.IOException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 *
 * @author Vijay
 */
public class AddMetaWorker {

    private String inputPath, outputPath, metaPath, subjectURI, propertyURI, objectURI, format;

    Model addMeta(Model rmodel) throws IOException {
        Model model = ModelFactory.createDefaultModel().read(inputPath);

        StmtIterator statements = model.listStatements();
        while (statements.hasNext()) {
            Statement statement = statements.nextStatement();

            if (subjectURI != null && !statement.getSubject().getURI().equals(subjectURI) ||
                propertyURI != null && !statement.getPredicate().getURI().equals(propertyURI) ||
                objectURI != null && !statement.getObject().asNode().getURI().equals(objectURI)) {
                continue;
            }

            Resource bnode = rmodel.createResource();

            StmtIterator metaStatements = ModelFactory.createDefaultModel().read(metaPath).listStatements();
            while (metaStatements.hasNext()) {
                Statement metaStatement = metaStatements.nextStatement();
                rmodel.add(bnode, metaStatement.getPredicate(), metaStatement.getObject());
            }

            rmodel.add(bnode, statement.getPredicate(), statement.getObject());
            rmodel.add(statement.getSubject(), statement.getPredicate(), bnode);
        }

        try (FileWriter writer = new FileWriter(outputPath)) {
            rmodel.write(writer, format.toUpperCase());
        }

        return rmodel;
    }

    AddMetaWorker storeOutputAt(String outputPath) {
        this.outputPath = outputPath;
        return this;
    }

    AddMetaWorker targetHasSubject(String subjectURI) {
        this.subjectURI = subjectURI;
        return this;
    }

    AddMetaWorker targetHasProperty(String propertyURI) {
        this.propertyURI = propertyURI;
        return this;
    }

    AddMetaWorker targetHasObject(String objectURI) {
        this.objectURI = objectURI;
        return this;
    }

    AddMetaWorker targetHas(String subjectURI, String propertyURI, String objectURI) {
        this.subjectURI = subjectURI;
        this.propertyURI = propertyURI;
        this.objectURI = objectURI;
        return this;
    }

    AddMetaWorker inFormat(String format) {
        this.format = format;
        return this;
    }

    static AddMetaWorker create(String inputPath, String metaPath) {
        AddMetaWorker worker = new AddMetaWorker();
        worker.inputPath = inputPath;
        worker.metaPath = metaPath;
        worker.outputPath = inputPath;
        worker.format = "NT";
        return worker;
    }

}
