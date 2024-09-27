# Resources

The files in the resources folder are interim versions of files within a workflow
that transforms the content within the Excel spreadsheets into the production OWL file. 

# Workflow and Pedigree of the files

 
# Materials
 There are two manually curated excel spreadsheets that contain the cognitive material that is rendered in the Protege owl representation: 

1. *EMFOConceptsAndRelationships.xlsx* which holds the concepts, relations, relation definitions[1], and the relationships.
2. *OntologyTopLevelDefinitions.xlsx* which holds the definitions for the classes.

In addition, there is a manually created owl file, *BasicEcologicalMentalFunctioningOntology.owl*,  containing classes we are either mapping to, or borrowed from the Basic Formal Ontology (BFO), object properties, we refer to as relations, we've borrowed from the Relation Ontology (RO), as well as some class attributes we've borrowed from mostly the BFO.  

# Transform Program 
The Java program *EMFOAddClassesRelationshipsAndDefinitionsToOWL*[2] opens up the RDF owl file, and injects the classes from the classes excel sheet and relationships from the relationships sheet into an OWL file.  The program also extracts definitions from the definitions excel spreadsheet into the OWL file as well.
 
This assumes that the classes from the excel spreadsheet have already been curated to insure all the definitions refer to existing classes, and the classes are all consistently named across the sheets.  (a spelling difference creates duplicate classes).


### Do this Next ##

 8. Then move the resulting file into the github repo
 9. Load the resulting file into WEBVowl 
 10. OPTIONAL: Add a title, author, and description into the right side tab manually
 11. Export the resulting state as a json file.
 12. Copy the resulting json file (maybe going to the downloads dir) to the github

# Caveats

1. I have not (yet) added the relation definitions to the owl file.
2.  I've included the fat jar file with the *EMFOAddClassesRelationshipsAndDefinitionsToOWL* in the utilities directory.  The source code for this program is embedded in the distributed source code in the 85_09-emfo_utilities-Application project.  

 
