package me.vijaychavda.rdfr;

import java.util.HashSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 *
 * @author Vijay
 */
public class Main {

    private static final String inputFile = "/...";
    private static final String metaFile = "";

    static HashSet<ReifiedStatement> statements = new HashSet<>();

    public static void main(String[] args) {
        Model model = getInput();

        //model.write(System.out, "NT");
        System.out.println("\n\n\n\n\n");

        System.out.println(statements.toString());
        System.out.println(statements.size());

        System.out.println("\n\n\n\n\n");

        Model model2 = getMetaData();
        model2.write(System.out, "NT");

        System.out.println("\n\n\n\n\n");

        Model union = model.union(model2);
        union.write(System.out, "NT");
    }

    private static Model getInput() {
        Model rmodel = ModelFactory.createDefaultModel();
        Model model = ModelFactory.createDefaultModel();
        model.read(inputFile);

        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement statement = it.nextStatement();
            ReifiedStatement reified = rmodel.createReifiedStatement(statement);
            statements.add(reified);
        }

        return rmodel;
    }

    private static Model getMetaData() {
        Model model = ModelFactory.createDefaultModel();
        String baseURL = "http://www.example.com/";

        Property start = model.createProperty(baseURL + "start");
        Property end = model.createProperty(baseURL + "end");

        ReifiedStatement s = statements.iterator().next();
        model.add(s, start, "1989");
        model.add(s, end, "2004");

        return model;
    }

}
