# The Ecological Mental Functioning Ontology (EMFO) NER Project
The EMFO NER project identifies mentions from text around EMFO classes.  There are two NER's distributed. A general one that identifies all EMFO classes, and one that identifies ComCog and IPIR classes specifically.



# Introduction


# Useful Distribution Contents
-  **[./bin/MentalFunctioningOntologyNERApplication-jar-with-dependencies.jar](https://github.com/CC-RMD-EpiBio/EcologicalMentalFunctioningOntology/raw/main/bin/MentalfunctioningOntologyNERApplication-jar-with-dependencies.jar)**  This is the executable application jar for the general EMFO NER

-  **[./bin/InFACTSubCategoriesApplication-jar-with-dependencies.jar](https://github.com/CC-RMD-EpiBio/EcologicalMentalFunctioningOntology/raw/main/bin/InFACTSubCategoriesApplication-jar-with-dependencies.jar)**  This is the executable application jar for extracting IPIR and ComCog
-  ./project                                                Where the source code and dependencies for the NER is
-  [Publications and Presentations](https://github.com/CC-RMD-EpiBio/EcologicalMentalFunctioningOntology/NER/#publications-and-presentations)

# Example Application Invocation
  > java -jar MentalFunctioningOntologyNERApplication-jar-with-dependencies.jar --inputDir=..\project\820_mentalFunctionOntologyNER\82_01_data\examples --outputDir=.\emfo_example_outputDir


# Example Text Data
Two exemplar files are distributed that include mental functioning mentions in them. 
  
-  [example_1.txt](https://github.com/CC-RMD-EpiBio/EcologicalMentalFunctioningOntology/blob/main/project/820_mentalFunctionOntologyNER/82_01_data/examples/example_1.txt)
-  [example_2.txt](https://github.com/CC-RMD-EpiBio/EcologicalMentalFunctioningOntology/blob/main/project/820_mentalFunctionOntologyNER/82_01_data/example_2.txt)

# Example UIMA XMI Output

Here is a screen shot of the UIMA Annotation Viewer viewing a file processed by the application.
  
  <img src="https://github.com/CC-RMD-EpiBio/bodyFunction/blob/main/project/820_mentalFunctionOntologyNER/82_10_doc/EMFO_UIMAXMI_Output.png" width=1000 />

# Example of GATE Output

Here is a screen shot of the GATE Viewer viewing a file processed by the application.

<img src="https://github.com/CC-RMD-EpiBio/bodyFunction/blob/main/project/820_mentalFunctionOntologyNER/82_10_doc/EMFO_GATE_ExampleOutput.png" width=1000 />


# Application Usage
The application is run from the command line: 
 
    > java -jar MentalFunctionSentenceFromMentionsApplication-jar-with-dependencies.jar [Options]

This is an all-encompassing java application jar.  

# Application Parameters/Options
The following command line parameters are necessary to run the application:

- `--inputDir=` This is the path to the text files for the application to process. The application will recurse through sub-directories to pick up files.
- `--outputDir=` If not filled out, defaults to the value of $inputDir_[dateStamp]

The following parameters are optional:

- `--version`  When this option is used, the application will print the version.  No other processing happens.
- `--help`     When this option is used, the application will print the usage page.  No other processing happens.
- `--outputTypes=` Specifies the kinds of output annotations.  See [Annotation Labels](#Annotation Labels) below for more details.
- `outputFormat=` Specifies the kind of output this application prints.  See the [Output Formats](#Output Formats) below for more details.
- `inputFormat=`  Specifies the kind of files to work on. The default is ascii text files, but the input can also be UIMA xmi or GATE files. See [Input Formats](#input formats) below for more details. 

# Annotation Labels
The application will create annotations for the following annotation types. These annotation types output are specified in a colon delimited list to the parameter --outputTypes=.  By default, all of the below annotation types are specified. 
   
 [TBD]
  
   
# Output Formats
This application returns annotated files in the following formats. These formats can be turned on by specifying them in a colon delimited list to the --outputFormat= parameter. This is the default output format.

 - *XMI_WRITER* - This is the standard Apache UIMA format. Apache UIMA 2.10 code was used. To interpret this format, the type descriptors will be needed. 
  >  The main type descriptor can be found 
                           at 60_03_type_descriptors/src/main/resources/gov/nih/cc/rmd/framework/bodyFunction/BodyFunctionModel.xml 
                           See https://uima.apache.org for documentation to use UIMA.   
                           
 - *GATE_WRITER*        - This is the GATE Developer xml document format.  See https://gate.ac.uk for documentation to use GATE.  GATE 9 codebase was used for this functionality. 
 - GATE_CORPUS_WRITER - This is the GATE Developer serial-datastore format.  All files read in will be put into a corpus named $CORPUS_NAME in this this format. 
 - *VTT_WRITER*         - This is a lightweight text format used by the VTT application. VTT (Visual Tagging Tool) is a simple, lightweight portable Java based annotation tool, created and distributed by the National Library of Medicine.  See https://lexsrv3.nlm.nih.gov/Specialist/Summary/vtt.html for more information about VTT. 
 - *TEXT_WRITER* - This is the text that was processed.  This format is sometimes useful to view as an output when the input came from a non text format, such as UIMA's xmi, GATE's xml, input formats [or drawn from embedded database queries - the current version of framework-legacy does not have this capability] 
 - *BIO_WRITER*         - This is the Begin, Inside, Outside format to use with the Stanford Core NLP toolkit.
 - *CSV_WRITER*         - This is output in pipe delimited formatted text files.
 - *SNIPPET_WRITER*     - [Not really applicable for this application] This is a VTT format, where each $focus annotation has been segmented into its own "snippet", with 3 lines before and 3 lines after surrounding it.  Each $focus mention is labeled with a true|false annotation.

 
# Input Formats
This application reads in data from the following formats.  The input format is specified by the --inputFormat= parameter.   

- *TEXT_READER*        - This is the default.  This reader assumes input to be UTF-8, ASCII-7 formatted files.  It is possible to feed in Windows page-code formatted files, but there is no translation done to UTF-8, and any characters that are out of range will be passed along as-is.  Text tokens that include non-ascii range characters will fail to match dictionary based lookups.
 - *XMI_READER*         - This reader will read in Apache-UIMA formatted XMI files. 
 - *GATE_READER*        - This reader will read in GATE formatted xml files.
 - *GATE_CORPUS_READER* - This reader will read in files within a GATE serial datastore.  

# Annotation Guidelines
It is important to understand how the training and testing annotations were created.  Much work went into codifying what should be coded and how. The guidelines were greatly consulted to insure annotations were consistent and correct. [TBD]

# Publications and Presentations

|Title     | Citation | Description |
| -------- | -------- | ------------| 


# License
All source code and documentation contained in this package are distributed under the terms in the [LICENSE](LICENSE) file (modified BSD). 

# Building From Source Code
This repo includes both the source code for the EMFO NER applications, along with those jars which are dependencies which are not distributed via a nexus service.  See the [project/readme.md](https://github.com/CC-RMD-EpiBio/EcologicalMentalFunctioningOntology/tree/main/project#readme) for details.

Note: This repo relies on functionality from the repo/project *Java-NLP-Framework*. For convenience, those jars in which the EMFO repo relies upon are included in this repo. However, the sources for Java-NLP-Framework are available from the [java-NLP-Framework repo](https://github.com/CC-RMD-EpiBio/java-nlp-framework).  See the [readme.md](https://github.com/CC-RMD-EpiBio/java-nlp-framework#readme) for details.


# Acknowledgments

See the [acknowledgments](acknowldgements.md) file for what software we use and attribute. 


