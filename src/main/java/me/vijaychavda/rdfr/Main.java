package me.vijaychavda.rdfr;

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
public class Main {

            String base = "...";
    public static void main(String[] args) {

        String inputPath = base + "Q42.nt";
        String outputPath = base + "reified-Q42.nt";
        String format = "NT";

        Model rmodel = null;
        try {
            rmodel = Reifier.reify(inputPath, outputPath, format);
        } catch (IOException | IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (rmodel == null) {
            System.err.println("Reification failed. The program will exit.");
            System.exit(-1);
        }

        String metaPath = base + "Q42-meta.nt";
        String subjectURI = "http://www.wikidata.org/entity/Q42";
        String propertyURI = "http://www.wikidata.org/prop/direct/P26";
        String objectURI = "http://www.wikidata.org/entity/Q14623681";

        Model model = ModelFactory.createDefaultModel().read(inputPath);

        StmtIterator statements = model.listStatements(
            model.getResource(subjectURI),
            model.getProperty(propertyURI),
            model.getResource(objectURI)
        );

        StmtIterator metaStatements = ModelFactory.createDefaultModel()
            .read(metaPath).listStatements();

        Resource meta = rmodel.createResource();
        while (metaStatements.hasNext()) {
            Statement metaStatement = metaStatements.nextStatement();

            rmodel.add(meta, metaStatement.getPredicate(),
                metaStatement.getObject());
        }

        while (statements.hasNext()) {
            Statement statement = statements.nextStatement();

            rmodel.add(meta, statement.getPredicate(), statement.getObject());
            rmodel.add(statement.getSubject(), statement.getPredicate(), meta);
        }

        rmodel.write(System.out, "NT");
    }
}
