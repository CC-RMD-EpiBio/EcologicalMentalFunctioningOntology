# The EMF Ontology Dictionary Files 

The following are the dictionary files the Ontology Named Entity Recognition Tools employ to find mentions.  These are pipe delimited csv files in a format akin to 
the National Library of Medicine's SPECIALIST Lexicon's LRAGR file.  (LRAGR was the name for the lexical relational files that includes syntatic agreement information in it).

<p>
Note: These files include comments or commented out rows, where comment rows start with a #.
<p>
The file format for each of these LRAGR files is the following:
 
| Column | Description |
| ------- | ---------- |
| Identifier | An identifier to trace back a term to this file. The SPECIALIST Lexicon's identifier, EUI, were assigned one per citation form.  So several rows could share an ID, as long as they were inflectionally related. I have not had the resources to keep to that guideline.  I've sometimes assigned the same identifier for all the entries in the file.  I had kept to re-using identifiers like CUI's or EUI's of the terms if I knew the terms came from the UMLS or SPECIALIST. | 
| key term | This is the lowercased[1] exact spelling form that is used for the matching in text.  That is, it is the inflected form that is used to do the match. The key term could be a multi word term, or a word with spaces or hyphens in it.  If a term has hypens in it, matches are made first looking for exact matches, then if none are found, hyphens are replaced with spaces and looked for. [1] If a term is lower cased, the match is made regardless of case.  If there is case variation, such as in ALL CAPs as would be found in an acronym or abbreviation, the match is only made to the exact case match. |
| part of speech | These are syntatic part of speech including noun, verbs, adj, adv.  The enumerated forms come from the forms found in the SPECIAlist Lexicon.  Some of the Ontology NER code utalizes part of speech to favor verb entries over noun entries when looking for function-ing matches. | 
| inflection and agreement type | These are the distinguing categories that indicate the inflection type such as singular or plural for nouns, infinitive, past, present, present participle for verbs.  The enumerated categories come from the SPECIALIST Lexicon.  In general, this information is not used for the ontology annotators, although, depending on what phrase chunker or sentence segmentation tool gets employed, the agreement pos, inflection and agreement information is utalized for making segmentation decisions. | 
| uninflected form | This is the uninflected version of the key term field. For example, if "dogs" is the key term, the uninflected form would be seen in this field as "dog".  This is information is not used for the Ontology annotators, and this field is often left empty. |
| citation form | This is the preferred uninflected version of the key term. In general, it is the same as the uninflected form, but occattionaly, when there are spelling variants, it is the preferred version.  This information is not used for any of the Ontology annotators, and is often left empty. |
| category(ies)  | This is a colon delimited set of categories, or semantic types, or types that have been manually assigned to this key'd term.  This is a very important field, as the categories this term has been tagged with are what gets carried along and used for the match filtering.  Each annotator has it's own set of categories it looks for. | 
| Source | Some of the lragr files include a field after the category field to indicate where this term came from, for example, MeSH, or SNOMED-CT, etc.  This is an optional field only used for debugging purposes |
| Source Identifiers | Some of the lragr files include a field after the category and source fields, what house source identifiers for where the term came from.  These would mostly be either CUI's or EUI's if seen |

Those LRAGR files listed here have to do with matches to EMFO categorized terminology. The Ontology NER's rely on many additional LRAGR files to decompose text into documents, sections, 
slot:values, sentences, phrases, terms and tokens. That is, there are dictionary files that catalog section names, slot names, addresses pieces, disease names and the like.  These can
be found by looking into the source code directly.

# EMFO Related Dictionary files #


|Number of Rows| File                                        | Description                                                                                                        |
| ------------ | ------------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
|              |                                             |               **General Ontology Dictionaries**                                                                      |
|         7439 | 001_TopExternalFactors.lragr                |  Top External Factors were terms extracted from 26 seed UMLS concepts that had external factors as a semantic type |
|        36168 | 002_TopPersonFactors.lragr                  |  Derived Person Factor inflected forms from 32 seed UMLS concepts                                                  |
|         9825 | 003_TopActivitiesAndParticipation.lragr     |  Derived Activities and Particiaption inflected forms from 17 seed UMLS concepts                                   |
|        19657 | 004_TopBehavior.lragr                       |  Dervied Behavior inflected forms from 31 seed UMLS concepts                                                       |
|        27176 | 005_TopActivitiesOfDailyLiving.lragr        |  Derived ADL inflected forms from 6 seed UMLS Concepts                                                             |
|           41 | 006_TopGeneralTasksAndDemands.lragr         |  Derived General tasks and demands from 1 UMLS Concept                                                             |
|         4494 | 007_TopLearningAndApplyingKnowledge.lragr   |  Derived Learning and Applying Knowledge from 2 UMLS Concepts                                                      |
|         5169 | 008_TopCommunicationActivities.lragr        |  Derived Communication Activities from 8 UMLS Concepts                                                             |
|        20567 | 009_TopCognitiveActivities.lragr            |  Derived Cognitive Activities from 5 UMLS Concepts                                                                 |
|        35869 | 010_TopVerbNetClasses.lragr                 |  IPIR, Comcog inflected forms from VerbNet Classes that were manually annotated with IPIR and/or ComCog subclasses |
|           36 | 011_TopIPIRNominalizations.lragr            |  Nominalizations of IPIR Classes from chosen VerbNet Classes                                                       |
|              |                                             |  Lexical Variants Generated from existing forms                                                                    | 
|        12630 | lvg_Verbs_inflected.lragr                   |  lvg generated inflections from the top seed files                                                                 |
|         7825 | lvg_verbs_DerivationsAndNomilizations.lragr |  lvg generated derived forms from the top seed files                                                               |
|         2947 | lvg_verbs_Synonyms.lragr                    |  lvg generated synonym forms from the top seed files                                                               |
|        12692 | lvg_verbs_spellingVariants_inflections_FACTS.lragr  |  lvg gnenerated spelling variants and inflections from the top seed files                                  |
|              |                                             |                        **Communication and Cognition related Dictionaries**                                          |
|          164 | ComCogUMLSTermsAndVariants.lragr            |  ComCog UMLS inflected forms with ComCog subclassifications                                                        |
|         6146 | ComCogVerbNetCategories.lragr               |  Verbnet ComCog inflected forms with curated ComCog sub-classifications                                            |
|         1755 | MoreComProducing.lragr                      |  Additional ComCog inflected forms from manual training annotations not found in other sources                     |
|          115 | missingTerms.lragr                          |  Variants manually created from existing forms that otherwise would have been missed                               |
|           33 | mentalFunctionTriggerTerms.lragr            |  Triggering function mentions like "able to"                                                                       |
|          349 | SSAListingTerms.lragr                       |  Comcog, IPIR terms from manual training annotations not in other sources                                          |
|              |                                             |             **Interpersonal Interactions and Relationship related Dictionaries**                                     |
|         1238 | IPIRInteractions.lragr                      |  IPIR (and not IPIR) forms from traversals through thesauri around seed IPIR interaction defintional terms         |
|          112 | informalRelationships.lragr                 |  Informal Relationships from traversals through thesauri around seed IPIR informal relationship definital terms    |
|          118 | intimateRelationships.lragr                 |  Intimate Relationships from traversals through thesauri around seed IPIR intimate relationship definital terms    |
|          119 | strangerRelationships.lragr                 |  Stranger Relationships from traversals through thesauri around seed IPIR stranger relationship definital terms    |
|          198 | OtherIPIRVerbs.lragr                        |  IPIR forms identified from VerbNet that got missed on the first pass thru VerbNet                                 |
|           14 | IPIRExceptions.lragr                        |  Confounding IPIR-like terms that are not IPIR terms                                                               |
|              |                                             |            **Dictionaries employed for Ontology related evidences**                                                 |
|          131 | denies.lragr                                |  A bunch of ways that negation evidence is seen                                                                    |
|          734 | emotion.lragr                               |  Emotions pulled from UMLS sources                                                                                 |
|         1075 | workRoles.lragr                             |  Work Roles, Occupations from BLS, SSA, ONGIG sources                                                              |

<p>
Last Updated 2024/09/24
