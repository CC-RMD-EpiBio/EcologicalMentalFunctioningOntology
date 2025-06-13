// =================================================
/**
 * IPIRSentenceExtraction creates ipir_yes and ipir_no sentences from IPIR mentions
 * 
 * @author  GD
 * @created 2024.11.04
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.experimental.categories.Categories;

import gov.nih.cc.rmd.framework.model.Person;
import gov.nih.cc.rmd.inFACT.BehaviorEvidence;
import gov.nih.cc.rmd.inFACT.D710_D720IPIRInteractions;
import gov.nih.cc.rmd.inFACT.D710_D720IPIRInteractionsEvidence;
import gov.nih.cc.rmd.inFACT.D730RelatingWithStrangers;
import gov.nih.cc.rmd.inFACT.D730RelatingWithStrangersEvidence;
import gov.nih.cc.rmd.inFACT.D730_D770RelationshipsIPIR;
import gov.nih.cc.rmd.inFACT.D7400AuthorityRelationships;
import gov.nih.cc.rmd.inFACT.D7400AuthorityRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D740FormalRelationships;
import gov.nih.cc.rmd.inFACT.D740FormalRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D750InformalRelationships;
import gov.nih.cc.rmd.inFACT.D750InformalRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D760FamilyRelationships;
import gov.nih.cc.rmd.inFACT.D760FamilyRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D770IntimateRelationships;
import gov.nih.cc.rmd.inFACT.D770IntimateRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D779OtherRelationshipsEvidence;
import gov.nih.cc.rmd.inFACT.D779OtherRelationships;
import gov.nih.cc.rmd.inFACT.EmotionEvidence;

import gov.nih.cc.rmd.inFACT.IPIRParticipationEvidence;

import gov.nih.cc.rmd.inFACT.manual.IPIRyes;
import gov.nih.cc.rmd.inFACT.model.IPIR_no;
import gov.nih.cc.rmd.inFACT.x.IPIR_yes;

import gov.nih.cc.rmd.inFACT.SupportEvidence;


import gov.nih.cc.rmd.mentalFunctionOntologyNER.IPIRActivities;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.nih.cc.rmd.nlp.lexUtils.LRPRN;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.SlotValue;
import gov.va.chir.model.Utterance;
import gov.va.vinci.model.Concept;



public class IPIRSentenceExtraction extends IPIRCategories {
 
  
  
  // -----------------------------------------
  /**
   * process runs through the text and creates
   * mentalFunctions from mentions.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
      this.performanceMeter.startCounter();
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," Started IPIRCategories");

   
      List<Annotation> ipirSentencesFromOntologyStep = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.x.IPIR_yes.typeIndexID, true );
    
   
      // ----------------------------------------------
      // itterate thru the sentences 
      if ( ipirSentencesFromOntologyStep != null && !ipirSentencesFromOntologyStep.isEmpty()) {
        for ( Annotation proposedIPIRSentenceFromOntology : ipirSentencesFromOntologyStep ) {
          
          // --------------
          // filter out the gold/manual ipir sentences - they will have annotation sets that are not framework
          
          String annotationSet = ((gov.nih.cc.rmd.inFACT.x.IPIR_yes)proposedIPIRSentenceFromOntology).getAnnotationSetName();
          if ( annotationSet == null || annotationSet.contains("framework")) {
         
            if ( processIPIRYesSentence ( pJCas, proposedIPIRSentenceFromOntology, proposedIPIRSentenceFromOntology) ) 
              changeAnnotationSetNameForProposedIPIRSentence( pJCas, proposedIPIRSentenceFromOntology );
            else {
              createIPIR_no_sentence( pJCas, proposedIPIRSentenceFromOntology);
              proposedIPIRSentenceFromOntology.removeFromIndexes();
            }
          } // end if this is a framework generated ipir yes sentence 
        } // end loop thru sentences
      
      } // end if there is an utterance 
      
      // Iterate thru all the utterances, and make comcog no sentences from those
      // utterances that are not comcog_yes
      
      // I would only need this if I'm evaluating comcog_no as gold
      // createIPIRNoSentences(pJCas);
      
      
      
  
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", " Issue with ipir Categories" + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," End IPIRCategories");
    this.performanceMeter.stopCounter();
    
  
  } // end Method process() ----------------
   

  // =================================================
 /**
  * createIPIRNoSentences iterates thru the utterances, and for
  * utterances that don't have an overlapping IPIR_yes, make
  * a IPIR_no sentence.
  * 
  * @param pJCas
 */
 // =================================================
 private final void createIPIRNoSentences(JCas pJCas) {
   
   List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Utterance.typeIndexID, true );
   
   if ( sentences != null & !sentences.isEmpty())
     for ( Annotation sentence : sentences ) {
       List<Annotation> utterancesWithYes = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, gov.nih.cc.rmd.inFACT.x.IPIR_yes.typeIndexID , sentence.getBegin(), sentence.getEnd(), false);
       if ( utterancesWithYes == null || utterancesWithYes.isEmpty()) {
         List<Annotation> utterancesWithNo = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, gov.nih.cc.rmd.inFACT.x.IPIR_no.typeIndexID , sentence.getBegin(), sentence.getEnd(), false);
         if ( utterancesWithNo == null || utterancesWithNo.isEmpty() )
           createIPIR_no_sentence(pJCas, sentence);
       }
     }
   
   
 } // end Method createIPIRNoSentences() ----------


 // =================================================
 /**
  * changeAnnotationSetNameForProposedIPIRSentence sets the annotation set name
  * to one that is the final name, picked up by down stream processes.
  * 
  * @param pJCas
  * @param pIPIRSentence
 */
 // =================================================
 private final void changeAnnotationSetNameForProposedIPIRSentence(JCas pJCas, Annotation pIPIRSentence) {
  
   ((gov.nih.cc.rmd.inFACT.bstract.IPIR_yes) pIPIRSentence).setAnnotationSetName(this.annotationSetName);
   
   
 } // end Method changeAnnotationSetNameForProposedIPIRSentence() ------


  

  
  // =================================================
  /**
   * createIPIR_no_sentence creates an ipir_no mention
   * 
   * @param pJCas
   * @param pMention
   * @return Annotation
  */
  // =================================================
  private final Annotation createIPIR_no_sentence(JCas pJCas, Annotation pMention ) {
   
    IPIR_no statement = new IPIR_no( pJCas);
    statement.setBegin(pMention.getBegin());
    statement.setEnd( pMention.getEnd());
    statement.setId("createIPIR_no_" + this.categoryCtr++);
    statement.setAnnotationSetName(this.annotationSetName);
    
    String assertionStatus =  String.valueOf(VUIMAUtil.getAssertion_Status( pMention));
    
    boolean attribution =    VUIMAUtil.getAttributedToPatient( pMention );
    boolean conditionalStatus = VUIMAUtil.getConditional_Status ( pMention );
    boolean historical = VUIMAUtil.getHistorical_Status((pMention));  
    String subject = VUIMAUtil.getSubject(pMention);
    statement.setAssertionStatus( assertionStatus);
    statement.setAttributedToPatient( attribution );
    statement.setHistoricalStatus(historical);
    statement.setConditionalStatus(conditionalStatus);
    statement.setSubjectStatus(subject);
    statement.addToIndexes();
    
    return statement;
    
    
  } // end Method createIPIR_no_sentence() ------------



  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    this.annotationSetName = U.getOption(pArgs, "--ipirAnnotationSetName=", "ipir_categories_rulebased_model");
      
  } // end Method initialize() -------
 
  

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
  // private HashSet<String> keptCategories = new HashSet<String>();
   private int categoryCtr = 0;
   private String annotationSetName = "ontology_rulebased_model";
   
  
   
  
} // end Class IPIRSentenceExtractionAnnotator() ---------------

