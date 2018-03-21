package me.vijaychavda.rdfr;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

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

        Resource meta = rmodel.createResource();

        StmtIterator metaStatements = ModelFactory.createDefaultModel()
            .read(metaPath).listStatements();
        while (metaStatements.hasNext()) {
            Statement metaStatement = metaStatements.nextStatement();

            rmodel.add(meta, metaStatement.getPredicate(),
                metaStatement.getObject());
        }

        Model model = ModelFactory.createDefaultModel().read(inputPath);

        StmtIterator statements = model.listStatements(
            model.getResource(subjectURI),
            model.getProperty(propertyURI),
            model.getResource(objectURI)
        );

        while (statements.hasNext()) {
            Statement statement = statements.nextStatement();

            rmodel
                .add(meta, statement.getPredicate(), statement.getObject());
            rmodel.add(statement.getSubject(), statement.getPredicate(),
                meta);
        }

        rmodel.write(System.out, "NT");
    }

    private static void showUsageAndExit() {
        System.err.println("Usage: rdfr <MODE> <INPUT_RDF_PATH> [OPTIONS...]");
        System.err.println("Use rdfr -help for more details.");
        System.exit(0);
    }
}
