package me.vijaychavda.rdfr;

import java.util.HashSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

/**
 *
 * @author Vijay
 */
public class Main {

    private static final String baseURL = "http://www.example.com/";

    static HashSet<ReifiedStatement> statements = new HashSet<>();

    public static void main(String[] args) {
        Model model = getInput();
        model.write(System.out, "NT");

        System.out.println("\n\n\n\n\n");

        System.out.println(statements.toString());

        System.out.println("\n\n\n\n\n");

        Model model2 = getMetaData();
        model2.write(System.out, "NT");

        System.out.println("\n\n\n\n\n");

        Model union = model.union(model2);
        union.write(System.out, "NT");
    }

    private static Model getInput() {
        Model model = ModelFactory.createDefaultModel();

        Resource john = model.createResource(baseURL + "john");
        Property spouse = model.createProperty(baseURL + "spouse");
        Resource jenny = model.createResource(baseURL + "jenny");

        Statement s = model.createStatement(john, spouse, jenny);
        ReifiedStatement bnode = model.createReifiedStatement(s);
        statements.add(bnode);

        return model;
    }

    private static Model getMetaData() {
        Model model = ModelFactory.createDefaultModel();

        Property start = model.createProperty(baseURL + "start");
        Property end = model.createProperty(baseURL + "end");

        ReifiedStatement s = statements.iterator().next();
        model.add(s, start, "1989");
        model.add(s, end, "2004");

        return model;
    }

}
