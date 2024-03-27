# Ecological Mental Functioning Ontology
**An Ecological Mental Functioning Ontology** 


# Repository Contents #
| FileName     | Description  |
| ------------ |------------- |
|emfo.owl      | The official RDF/XML OWL file           |
|emfo.owl.json | The OWL file rendered in JSON for WebVOWL visualization |
|EMFOConceptsAndRelationships.xlsx     | The spreadsheet with the ontology in it |
|EMFOCON.CSV   | The Classes sheet in CSV format         |
|EMFOREL.CSV   | The Relationships sheet in CSV format   |
|EMFODEFC.CSV  | The Classes Definitions in CSV format   |
|EMFODEFR.CSV  | The Relations Definitions in CSV format |
|LICENSE.MD    | The License agreement for the EMFO's use|
|EMFO_Picture.pptx| A visual Representation of the top Levels of the Ontology |
|Derived Terminologies | Terminologies derived from the Ontology |


## Summary
The Ecological Mental Functioning Ontology (EMFO) is a representation of mental functioning based on the Ecological Model of Mental Functioning (EMMF) (Sacco et al. 2024) that integrates concepts taken from the International Classification of Functioning, Disability and Health (ICF) (World Health Organization, 2001); person-environment-activities transactive models (Dean et al., 2019; Dunn et al., 1994; Law et al., 1996), social-ecological theories (Stineman & Streim, 2010; Van Assche et al., 2019); and Open System Theory (von Bertalanffy, 1968).   
<p> 
What is Mental functioning? Is a question that has been difficult to garner a consensus to answer.
Human mental functioning, represented as observable behavior, is understood as a transaction between the person and their intrinsic attributes, the context and environment where functioning takes place, and the nature and type of activities the person engages in.  We have defined mental functioning as the “mind-directed observable external behaviors that represent what people actually do (or do not do) in response to decisions occurring during real-time transactions as they unfold moment by moment in daily life and over time” (Sacco et al., 2023).  
<p>
The EMFO differentiates the concept of mental functioning from mental functions. The ICF has conceptual coverage of mental functioning but there are gaps. The understanding of what is involved with mental functioning has evolved in the last 23 years since its release. The language of mental functions is included within SNOMED, MeSH, and MedRE but little coverage of mental functioning. 
The EMFO can be used conceptually to understand and explain behaviors indicative of mental functioning from either the individual’s perspective or the perspective of others, such as a clinical observer.  For the purpose of our use case (described below), we adopt the perspectives of a clinician observer, such as a behavioral health specialist or rehabilitation professional who document functioning in patients’ clinical records. 
<p>
The EMFO perspective represents observations that are a point-in-time, and written in the language of functioning.  Impairments that may or may not affect functioning, are classified as attributes of the person, rather than diagnoses or pathologies.  
The EMFO is positioned as a domain ontology which includes classes and mostly hierarchical relationships between the classes, with some part-of and non-hierarchical relationships.
<p>
The EMFO adheres to most of the prescribed ontology and terminology desiderata including no implied relationship cycles and having unique class names. The EMFO includes class and relationship definitions. The EMFO semantics is noted to include upper levels of the ontology providing the role of mental functioning categories to subsequent descendant classes. The EMFO semantics are similar to that adopted by the UMLS.
<p>
The initial use case was to support the United States Social Security Administration (SSA) disability determination process by leveraging clinical natural language processing methods to identify mental functioning mentions within clinical documents to provide evidence for or against disability benefits adjudication. Domain experts created guidelines, based on the EMFO perspectives, then manually annotated a large, diverse national clinical record corpus. This was an iterative process providing feedback that further refined the guidelines and ontology as the domain experts came to consensus of what should and should not be included as a mention. The subsequent annotations have also provided training and validation for rule-based and BERT based BioLSTM NERs extracting functioning. The EMFO includes a derived terminology, initially built from identifying relevant top level UMLS concepts and all their descendants. This has been augmented by adding a curated set of classes from VerbNet, where our domain experts classified VerbNet classes with EMFO categories for communication and cognition (ComCog) and interpersonal interactions and relationships (IPIR). The terminology has been further tuned by including terms from the manual annotation effort which had no representation in our existing sets and the removal of terms from our terminology where the NERs identified too many false positives mentions.
<p>
We are using the EMFO as a rhetorical device to foster a dialog to garner consensus.
<p>
The Ontology has been packaged into a set of csv and excel files, an RDF/XML Owl file, and a webVowl json file.


![image](https://github.com/CC-RMD-EpiBio/EcologicalMentalFunctioningOntology/blob/main/EMFO_Picture.png)
Figure 1: Visualization of the Upper Levels of the Ecological Mental Functioning Ontology
<p>

# About Us #
This work stems from the Epidemiology and Biostatistics section of the Rehabilitation Medicine Department, National Institutes of Health (NIH) Clinical Center that uses the ICF as a framework to develop analytic tools for clinical natural language processing and the development of functional assessment measures. The group is composed of professionals from behavioral health, rehabilitation, medicine, computer science, and health services researchers. The ontology team, in particular, is made up of individuals representing computer science, occupational and physical therapy, medicine, and psychiatry, with consults from speech and language pathology. 

# Publications #
Sacco, M. J., Divita, G., & Rasch, E. (2023). Development of an ontology to characterize mental functioning. Disabil Rehabil, 1-10. https://doi.org/10.1080/09638288.2023.2252337

# License #
See the [***License file***](https://github.com/CC-RMD-EpiBio/EcologicalMentalFunctioningOntology/blob/main/LICENSE.txt)


# Contact Us #
  Guy Divita guy.divita@nih.gov


# References #

 - International Classification of Functioning, Disability and Health, Geneva: World Health Organization, 2001. 
 - M. J. Sacco, G. Divita and E. Rasch, "Development of an ontology to characterize mental functioning," Disability and Rehabilitation, p. 1, 2023. 
 - von Bertalanffy, L. General system theory: Foundations, development, applications, Aldine Publishing Co., 1968. 
 - M. G. Stineman and J. E. Streim, "The biopsycho-ecological paradigm: a foundational theory for medicine," PM&R, vol. 2, no. 11, pp. 1035-1045, 2010. 
 - Van Assche, K et al., "The social, the ecological, and the adaptive. Von Bertalanffy's general systems theory and the adaptive governance of social?ecological systems.," Systems Research and Behavioral Science, vol. 36, no. 3, pp. 308-321, 2019. 
 - Dean, E.E. et al. "Adaptation as a transaction with the environment perspectives from the ecology of human performance model," in Adaptation through occupation: multidimensional perspectives, Danvers, MA, SLACK Incorporated, 2019, pp. 141-155.
 - W. Dunn, C. Brown and A. McGuigan, "The ecology of human performance: A framework for considering the effect of context," The American Journal of Occupational Therapy , vol. 47, no. 7, pp. 595-607, 1994. 
 - M. law et al. "The person-environment-occupation model: A transactive approach to occupational performance," Canadian journal of occupational therapy, vol. 63, no. 1, pp. 9-23, 1996. 
 - G. O. klein and B. Smith, "Concept Systems and Ontologies: Recommendations for Basic Terminology," Trans Jpn Soc Artif Intell., vol. 25, no. 3, pp. 433-441, 2010. 
 - P. L. Schuyler, W. T. Hole, M. S. Tuttle and D. D. Sheretz, "The UMLS Metathesaurus: representing different views of biomedical concepts," Bulletin of the Medical Library Association, vol. 81, no. 2, p. 217, 1993. 
 - D. A. lindberg, B. L. Humphreys and A. T. McCray, "The unified medical language system," Yearbook of medical informatics, vol. 2, no. 01, pp. 41-51, 1993. 
 - S. Cozzi, A. Martinuzzi and V. Della Mea, "Ontological modeling of the International Classification of Functioning, Disabilities and Health (ICF): activities&participation and environmental factors components," BMC medical informatics and decision making, vol. 21, pp. 1-21, 2021. 
 - E. Beisswanger, S. Schulz, H. Stenzhorn and U. hahn, "BioTop: An upper domain ontology for the life sciences," Applied Ontology, vol. 3, no. 4, pp. 205-212, 2008. 
 - I. Niles and A. Pease, "Towards a standard upper ontology," in Proceedings of the international conference on Formal Ontology in Information Systems, 2001. 
 - J. Hastings, W. Ceusters, M. Jensen, K. Milligan and B. Smith, "Towards an Ontology of Mental Functioning," in ICBO Workshop, 2012. 
 - N. J. Otte, J. Beverley and A. Ruttenberg, "BFO: Basic formal ontology," Applied ontology, vol. 17, no. 1, pp. 17-43, 2022. 
 - B. Smith et al. "The OBO Foundry: coordinated evolution of ontologies to support biomedical data integration," Nature biotechnology, vol. 25, no. 11, pp. 1251-1255, 2007. 
 - J. Cimino, "Desiderata for controlled medical vocabularies in the twenty-first century," Methods of information in medicine, vol. 37, no. 04/05, pp. 393-403, 1998. 
 - A. burgun, "Desiderata for domain reference ontologies in biomedicine," Journal of biomedical informatics, vol. 39, no. 3, pp. 307-313, 2006. 
 - M. J. Sacco, G. Divita, H. Goldman, K. Coale and C. P. Ros�, "An Ecological Model in Support of an Ontology of Mental Functioning," Unpublished manuscript, 2024. 
 - G. Divita, "Java-NLP-Framework," NIH Clinical Center Rehabilitation Medicine Department EpiBio Branch, 29 07 2022. [Online]. Available: https://github.com/CC-RMD-EpiBio/java-nlp-framework. [Accessed 29 March 2024].
 - "Medical Subject Headings (MeSH)," National Libarary of Medicine, National Institutes of Health, 2022. [Online]. Available: https://www.nlm.nih.gov/mesh/meshhome.html. [Accessed 20 March 2024].
 - "SNOMED CT," National Library of Medicine, National Institutes of Health, 2019. [Online]. Available: https://www.nlm.nih.gov/healthit/snomedct/index.html. 
 - "MedDRA Medical Dictionarly for Regulatory Activities," International Council for Harmonisation of Technical Requirements for Pharmaceuticals for Human Use (ICH), March 2024. [Online]. Available: https://www.meddra.org
 - "Thesaurus of Psychological Index Terms," American Psychological Association, March 2024. [Online]. Available: https://www.apa.org/pubs/databases/training/thesaurus. 
 - "Chapter 3: Related Concepts," in UMLS Reference Manual [Internet], Bethesda MD, National Library of Medicine, National Institutes of Health, 2009. 
 - Kipper-Schuler, "VerbNet, A computational Lexical Resource for Verbs," Computational Language and Education Research (CLEAR) University of Colorado, 2006. [Online]. Available: verbs.colorado.edu/verbnet. [Accessed 20 March 2024].
 - C. E. Lipscomb, "Medical subject headings (MeSH)," Bulletin of the Medical Library Association, vol. 88, no. 3, p. 265, 2000. 
 - K. K. Schuler, VerbNet: A broad-coverage, comprehensive verb lexicon, University of Pennsylvania, 2005.
