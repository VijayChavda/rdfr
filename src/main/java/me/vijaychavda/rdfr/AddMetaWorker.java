package me.vijaychavda.rdfr;

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

    public Model addMeta(Model rmodel) {
        Resource meta = rmodel.createResource();

        StmtIterator metaStatements = ModelFactory.createDefaultModel()
            .read(metaPath).listStatements();
        while (metaStatements.hasNext()) {
            Statement metaStatement = metaStatements.nextStatement();

            rmodel.add(meta, metaStatement.getPredicate(),
                metaStatement.getObject());
        }

        Model model = ModelFactory.createDefaultModel().read(inputPath);

        StmtIterator statements = propertyURI != null
            ? model.listStatements(
                model.createResource(subjectURI),
                model.createProperty(propertyURI),
                model.createResource(objectURI)
            )
            : model.listStatements(
                model.createResource(subjectURI),
                null,
                model.createResource(objectURI)
            );

        while (statements.hasNext()) {
            Statement statement = statements.nextStatement();

            rmodel
                .add(meta, statement.getPredicate(), statement.getObject());
            rmodel.add(statement.getSubject(), statement.getPredicate(),
                meta);
        }

        rmodel.write(System.out, format);

        return rmodel;
    }

    public AddMetaWorker storeOutputAt(String outputPath) {
        this.outputPath = outputPath;
        return this;
    }

    public AddMetaWorker targetHasSubject(String subjectURI) {
        this.subjectURI = subjectURI;
        return this;
    }

    public AddMetaWorker targetHasProperty(String propertyURI) {
        this.propertyURI = propertyURI;
        return this;
    }

    public AddMetaWorker targetHasObject(String objectURI) {
        this.objectURI = objectURI;
        return this;
    }

    public AddMetaWorker targetHas(String subjectURI, String propertyURI,
        String objectURI) {
        this.subjectURI = subjectURI;
        this.propertyURI = propertyURI;
        this.objectURI = objectURI;
        return this;
    }

    public AddMetaWorker inFormat(String format) {
        this.format = format;
        return this;
    }

    public static AddMetaWorker create(String inputPath, String metaPath) {
        AddMetaWorker worker = new AddMetaWorker();
        worker.inputPath = inputPath;
        worker.metaPath = metaPath;
        worker.outputPath = inputPath;
        worker.format = "NT";
        return worker;
    }

}
