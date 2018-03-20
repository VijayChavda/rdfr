package me.vijaychavda.rdfr;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 *
 * @author Vijay
 */
public class Main {

            String base = "...";
    public static void main(String[] args) throws FileNotFoundException, IOException {

        String inputRDF = base + "Q42.nt";
        String metaRDF = base + "Q42-meta.nt";
        String reifiedRDF = base + "reified-Q42.nt";

        Model rmodel = Reifier.reify(inputRDF, reifiedRDF);
        rmodel.write(System.out, "NT");

        System.out.println("\nGAP\n");

        Model mmodel = ModelFactory.createDefaultModel();
        mmodel.read(metaRDF);
        mmodel.write(System.out, "NT");

        System.out.println("\nGAP\n");

        Statement statement = rmodel.listStatements().nextStatement();
        System.out.println(statement.getSubject().getId());

        System.out.println("\nGAP\n");

        String subjectURI = "http://www.wikidata.org/entity/Q42";
        String propertyURI = "http://www.wikidata.org/prop/direct/P26";
        String objectURI = "http://www.wikidata.org/prop/direct/Q14623681";

        Resource subject = rmodel.getResource(subjectURI);
        Property property = rmodel.getProperty(propertyURI);
        Resource object = rmodel.getResource(objectURI);

        Resource bnode = rmodel.createResource();

        StmtIterator metaStatements = mmodel.listStatements();
        while (metaStatements.hasNext()) {
            Statement metaStatement = metaStatements.nextStatement();
            rmodel.add(bnode, metaStatement.getPredicate(), metaStatement.getObject());
        }

        rmodel.add(bnode, property, object);
        rmodel.add(subject, property, bnode);

        System.out.println("\nGAP\n");

        rmodel.write(System.out, "NT");
    }
}
