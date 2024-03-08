# Ecological Mental Functioning Ontology
**An Ecological Mental Functioning Ontology** 


# Repository Contents #
| FileName     | Description  |
| ------------ |------------- |
|emfo.owl      | The official RDF/XML OWL file           |
|emfo.owl.json | The OWL file rendered in JSON for WebVOWL visualizaton |
|EMFOConceptsAndRelationships.xlsx     | The spreadsheet with the ontology in it |
|EMFOCON.CSV   | The Classes sheet in CSV format         |
|EMFOREL.CSV   | The Relationships sheet in CSV format   |
|EMFODEFC.CSV  | The Classes Definitions in CSV format   |
|EMFODEFR.CSV  | The Relations Definitions in CSV format |
|LICENSE.MD    | The License agreement for the EMFO's use|

## Summary

The Ecological Mental Functioning Ontology (EMFO) is a representation of mental functioning based on the Ecological Model of Mental Functioning.  This is a description of mental functioning that is an evolution of content contained in the International Classification of Functioning, Disability and Health (ICF).

What is mental functioning? This is a question that has been difficult to garner a consensus on. 
<p> 
Mental functioning, represented as a person's observable behavior, is understood as a transaction between the person and their intrinsic attributes, the context and environment where functioning takes place, and the nature and type of activities the person engages in.   

The EMFO differentiates the concept of mental functioning from mental functions.
The ICF includes concepts of mental functioning but there are gaps.  The understanding of what is involved with mental functioning has evolved in the last 23 years since its initial release.  The language of mental functions is included within SNOMED, MeSH, and MedRE, but there is little coverage of mental functioning.  The EMFO is characterized by explicitly noting the perspectives of a clinician observer, such as an occupational or physical therapist.  The EMFO perspective involves behaviors observed by the clinician.  

The EMFO perspective involves a point-in-time timeframe for the observations, and observations written in the language of functioning, where impairments affecting functioning are described as attributes of functioning rather than diagnoses or pathologies.

The EMFO is positioned as a domain ontology that includes classes and mostly hierarchical relationships between the classes, with some part-of and non-hierarchical relationships. 

The EMFO adheres to most of the prescribed ontology and terminology desiderata including no implied relationship cycles and having unique class names.  The EMFO ontology includes class and relationship definitions. The EMFO semantics is noted to include upper levels of the ontology providing the role of mental functioning categories to subsequent descendent classes.   The EMFO semantics are similar to that adopted by the UMLS. 

The initial use case for the EMFO was identifying mental functioning mentions within clinical documents used as part of disability benefits adjudication.  Domain experts created guidelines, based on the EMFO ontological perspectives, then manually annotated a large, diverse national clinical record corpus.  This was an iterative process providing feedback that was then incorporated into the guidelines and ontology as the domain experts came to consensus of what should and should not be a mention.  The subsequent annotations have also provided training and validation for rule-based and BERT-based BioLSTM NERs for extracting functioning mentions. 
The EMFO includes a derived terminology, initially built from identifying relevant top level UMLS concepts and all their descendants.  This has been augmented by adding a curated set of classes from VerbNet, where our domain experts classified VerbNet classes with EMFO categories for Communication, Cognition, and Interpersonal Interactions and Relationships (social functioning).  The terminology has been further tuned by including terms from the manual annotation effort which had no representation in our existing sets and the removal of terms from our terminology where the NERs identified too many false positives mentions.

We are using the EMFO as a rhetorical device to foster a dialog to garner consensus. 

The Ontology has been packaged up into a set of csv and excel files, an RDF/XML Owl file, and a webVowl json file. 

# About Us #
[TBD]  a blurb about or organization, and the Ontology team in particular

# Publications #

# License #
See the License file.

# Contact Us #
[TBD]'
