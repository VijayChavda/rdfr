# rdfr
> Reification and adding meta-data.

##### USAGE:
~~~~
	-reify <MODE> <INPUT_RDF> [META_RDF] [OPTIONS...]
~~~~

##### MODE:
		-reify		Use this mode to reify triples of an existing RDF data-set.
		-add-meta	Use this mode to add meta-data to existing triples of given RDF data-set.
		-help		Displays this manual.

##### INPUT_RDF:
		Specifies the path to input RDF data-set.

##### META_RDF:
		Specifies the path to meta RDF data-set.

##### OPTIONS:
		-o		Specify path to the file where output data-set will be stored.

		-f		Specify the formatting for output data-set.
					
				The following formats for output are supported:
					
				Format		Description
				*************************
				NT		NTRIPLES
				NQ		NQUADS
				TTL		TURTLE
				XML		RDF/XML
				JSON	        RDF/JSON

		-s		Used with -add-meta mode to specify that meta-data should only be added to statements having subject of given URI.

		-p		Used with -add-meta mode to specify that meta-data should only be added to statements having property of given URI.

		-v		Used with -add-meta mode to specify that meta-data should only be added to statements having object of given URI.

##### NOTES:
1. Adding meta-data will first reify the input RDF data-set.

2. If -o is not used, then input file becomes the output file (existing data-set is overriden)

3. If -f is not used, the output will be formatted as n-triples (NT format).

4. By using combinations of -s -p and -o options, you can specify the set of statements in your input RDF data-set to which meta-data should be added.

5. If all of -s -p -o options are not used, then meta-data will be added to every statement in the given input RDF data-set.

6. What file extensions are used for the input or output RDF files have nothing to do with the format of output RDF data-set.

7. Input RDF file, and meta-data RDF file must have one of following extension:
					
		Format		Description
		*************************
		.nt		NTRIPLES
		.nq		NQUADS
		.ttl		TURTLE
		.xml		RDF/XML
		.rj		RDF/JSON

##### EXAMPLES:
1. Reify the RDF data-set in "./input.nt" and store the output in "./output.nt" in XML format.
			
		Example:	$ rdfr -reify ./input.nt -o ./output.xml -f xml
	
2. Reify the RDF data-set in "./input.nt" and store the output in "./output.nt" in NT format.
			
		Example:	$ rdfr -reify ./input.nt -o ./output.nt

3. Reify the RDF data-set in "./input.nt" and override the output in same file in NT format.
			
		Example:	$ rdfr -reify ./input.nt
	
4. Reify the RDF data-set in "./input.nt" and override the output in same file in JSON format.
			
		Example:	$ rdfr -reify ./input.nt -f json
	
5. Add the RDF meta-data data-set in "./meta.nt" to the input RDF data-set in "/input.nt". Meta-data will be added to only those statements in "/input.nt" which have matching -s, -p and -o values.

			
		Example:	$ rdrf -add-meta ./input.nt ./meta.nt -s http://www.example.com/subject -p http://www.example.com/property -o http://www.example.com/object
	
6. Add the RDF meta-data data-set in "./meta.nt" to the input RDF data-set in "/input.nt". Meta-data will be added to all the statements in "/input.nt".
			
		Example:	$ rdrf -add-meta ./input.nt ./meta.nt

##### SAMPLE INPUT DATA SET in NT:
~~~~
<http://www.wikidata.org/entity/Q42> <http://www.wikidata.org/prop/direct/P26> <http://www.wikidata.org/entity/Q14623681> .
~~~~

##### SAMPLE META DATA SET in NT:
~~~~
_:Bc8d03d29X2D4784X2D40a6X2D886cX2Dfb1a84da4db4 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://wikiba.se/ontology-beta#Statement> .
_:Bc8d03d29X2D4784X2D40a6X2D886cX2Dfb1a84da4db4 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://wikiba.se/ontology-beta#BestRank> .
_:Bc8d03d29X2D4784X2D40a6X2D886cX2Dfb1a84da4db4 <http://wikiba.se/ontology-beta#rank> <http://wikiba.se/ontology-beta#NormalRank> .
_:Bc8d03d29X2D4784X2D40a6X2D886cX2Dfb1a84da4db4 <http://www.wikidata.org/prop/qualifier/P580> "1991-11-25T00:00:00Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
_:Bc8d03d29X2D4784X2D40a6X2D886cX2Dfb1a84da4db4 <http://www.wikidata.org/prop/qualifier/value/P580> <http://www.wikidata.org/value/c8ae0d38443d4671d3f893d7000a859e> .
_:Bc8d03d29X2D4784X2D40a6X2D886cX2Dfb1a84da4db4 <http://www.wikidata.org/prop/qualifier/P582> "2001-05-11T00:00:00Z"^^<http://www.w3.org/2001/XMLSchema#dateTime> .
_:Bc8d03d29X2D4784X2D40a6X2D886cX2Dfb1a84da4db4 <http://www.wikidata.org/prop/qualifier/value/P582> <http://www.wikidata.org/value/1c30ade7914d072877b2db404a683d7c> .
_:Bc8d03d29X2D4784X2D40a6X2D886cX2Dfb1a84da4db4 <http://www.w3.org/ns/prov#wasDerivedFrom> <http://www.wikidata.org/reference/9177d75c6061e9e1ab149c0aa01bee5a90e07415> .
~~~~
