# Resources

The files in the resources folder are interim versions of files within a workflow
that transforms the content within the Excel spreadsheets into the production OWL file. 

# Workflow and Pedigree of the files

 

1. The beginning of the workflow is the spreadsheet containing the classes and relationships.
2. Open Protégé and start with the *BasicEcologicalMentalFunctioningOntology* RDF/XML Owl file. This file has the attributes and relations populated but not the classes.  See [Note 1]
3. Use the Protégé Cellfie plugin to load the json rules to load the classes from the *emfoOntologyRules.json* file.
4. Run the generate axioms in the cellfie window (to add the classes from the spreadsheet to Protégé's current ontology, and add the axioms to the current ontology.
5. Save the resulting ontology as an RDF/XML flavor of OWL

----------
## Human intervention is needed here ##
<p> When Protégé saves the OWL file, the names that were borrowed from other ontologies, if the names contained a representation that differs than the name we used in the excel spreadsheet, Protégé ignores what was absorbed by cellfie, and saves the version of each name as it was in the borrowed ontology.  This becomes a problem as we desire to have multi-word names replace spaces with underbars.  There are a number of borrowed names where the name was a multi-word name, but the original form had the spaces removed.  This becomes a problem when matching names in relationship tuples back to the classes.  These get missed. 
<p>
 6. Go into the resulting owl file, and alter the borrowed names that had the spaces removed. An example is *bodily process* being munged into *bodilyProcess*.

----------
### Do this Next ##

 7. Run the EMFOAddRelationshipssToOWL program to inject the relationships into the owl file.
 8. Then move the resulting file into the github repo
 9. Load the resulting file into WEBVowl 
 10. OPTIONAL: Add a title, author, and description into the right side tab manually
 11. Export the resulting state as a json file.
 12. Copy the resulting json file (maybe going to the downloads dir) to the github

# Notes
[1] The *BasicEcologicalMentalFunctioningOntology* was hand crafted to include the attributes and relations.  If we need a new attribute or new relation, this file needs to be updated.
 
