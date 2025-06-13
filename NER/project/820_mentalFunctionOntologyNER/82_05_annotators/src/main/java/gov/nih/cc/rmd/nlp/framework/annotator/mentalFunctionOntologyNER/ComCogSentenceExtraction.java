// =================================================
/**
 * ComcogSentenceExtraction creates comcog_yes and comcog_no sentences
 * from mentions found.  
 * 
 * @author  GD
 * @created 2024.11.04
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;


import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.inFACT.model.Comcog_no;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Utterance;



public class ComCogSentenceExtraction extends ComCogCategories {
 
  
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
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," Started ComCog extraction");

   
      List<Annotation> comcogSentencesFromOntologyStep = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.x.Comcog_yes.typeIndexID, true );
      // ----------------------------------------------
      // itterate thru the proposed comcog yes sentences 
      if ( comcogSentencesFromOntologyStep != null && !comcogSentencesFromOntologyStep.isEmpty()) {
        for ( Annotation proposedComcogSentence : comcogSentencesFromOntologyStep ) {
          if ( processComcogSentence ( pJCas, proposedComcogSentence ) ) {
           changeAnnotationSetNameForProposedComcogSentence( pJCas, proposedComcogSentence);
          }
          else {
            createComCog_no_Sentence( pJCas, proposedComcogSentence);
            proposedComcogSentence.removeFromIndexes();
          }
        } // end loop thru proposed comcog yes sentences 
      
      } // end if there is a proposed Comcog_yes from the ontology 
      
      // Iterate thru all the utterances, and make comcog no sentences from those
      // utterances that are not comcog_yes
      
      // I would only need this if I'm evaluating comcog_no as gold
      // createComcogNoSentences(pJCas);
      
  
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", " Issue with comcog extraction" + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," End ComCogExtraction");
    this.performanceMeter.stopCounter();
    
  
  } // end Method process() ----------------
   

   // =================================================
  /**
   * createComcogNoSentences iterates thru the utterances, and for
   * utterances that don't have an overlapping comcog_yes, make
   * a comcog_no sentence.
   * 
   * @param pJCas
  */
  // =================================================
  private final void createComcogNoSentences(JCas pJCas) {
    
    List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Utterance.typeIndexID, true );
    
    if ( sentences != null & !sentences.isEmpty())
      for ( Annotation sentence : sentences ) {
        List<Annotation> utterancesWithYes = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, gov.nih.cc.rmd.inFACT.x.Comcog_yes.typeIndexID , sentence.getBegin(), sentence.getEnd(), false);
        if ( utterancesWithYes == null || utterancesWithYes.isEmpty()) {
          List<Annotation> utterancesWithNo = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, gov.nih.cc.rmd.inFACT.x.Comcog_no.typeIndexID , sentence.getBegin(), sentence.getEnd(), false);
          if ( utterancesWithNo == null || utterancesWithNo.isEmpty() )
            createComCog_no_Sentence(pJCas, sentence);
        }
      }
    
    
  } // end Method createComCogNoSentences() ----------


  // =================================================
  /**
   * changeAnnotationSetNameForProposedComcogSentence sets the annotation set name
   * to one that is the final name, picked up by down stream processes.
   * 
   * @param pJCas
   * @param pComcogSentence
  */
  // =================================================
  private final void changeAnnotationSetNameForProposedComcogSentence(JCas pJCas, Annotation pComcogSentence) {
   
    ((gov.nih.cc.rmd.inFACT.bstract.Comcog_yes) pComcogSentence).setAnnotationSetName(this.annotationSetName);
    
    
  } // end Method changeAnnotationSetNameForProposedComcogSentence() ------


  
  // =================================================
  /**
   * createComCog_no_Sentence creates an comcog_no sentence
   * 
   * @param pJCas
   * @param pMention
   * @return Annotation
  */
  // =================================================
  private final Annotation createComCog_no_Sentence(JCas pJCas, Annotation pMention ) {
   
    Comcog_no statement = new Comcog_no( pJCas);
    statement.setBegin(pMention.getBegin());
    statement.setEnd( pMention.getEnd());
    statement.setId("createIPIR_yes_" + this.categoryCtr++);
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
    
    
  } // end Method createIPIR_no() ------------

  
   
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
    this.annotationSetName = U.getOption(pArgs, "--comcogAnnotationSetName=", "comcog_categories_rulebased_model");
     
  } // end Method initialize() -------
 
  
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
   private int categoryCtr = 0;
   private String annotationSetName = "comcog_categories_rulebased_model";
  
       
  
  
} // end Class LineAnnotator() ---------------

