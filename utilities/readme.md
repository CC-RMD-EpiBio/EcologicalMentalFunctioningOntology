
# Ecological Mental Functioning Ontology Add Classes, Definitions and Relationships to OWL  #

## Description ##
This program opens an existing RDF/XML OWL file, finds the classes,  opens a relationship excel spreadsheet,
and injects the relationships between classes into the OWL file as class restrictions on the first 
class mentioned in the relationships.  

The program will deposit a version of the RDF/XML Owl file in the specified --outputDir= directory which
is composed of --inputDir + _[$DATE_STAMP] unless the --outputDir= is specified.

### Note ###
This program is a quick and dirty pragmatic way of bulk relationships into our OWL files.  It does not
include a true OWL parser, it just knows about the syntax around classes and restrictions. 


## Program ##
java -jar $FRAMEWORK_HOME/10_libs/EMFOAddRelationshipsToOWL-jar-with-dependencies.jar 
    
    --inputDir=                           point this to the resources dir with the excel file in it
    --outputDir=                          defaults to inputDir + _[$DATE_STAMP] if not specified
    --inputBAREOWL=                       This is the starting, manually created owl file with borrowed classes, 
                                          relations, and properties in it.  By default, this is the 
                                          BasicEcologicalMentalFunctioningOntology.owl
    --classesAndRelationshipEXCELFile=    defaults to $inputDir + /EMFOConceptsAndRelationships.xlsx
    --definitionsExcelFile=               defaults to $inputDir + /OntologyTopLevelDefinitions.xlsx
    
    --outputOWL=                          defaults to $outputDir/EMFO.owl
    --version                             returns the version 
    --help                                returns this man
      
      
      
