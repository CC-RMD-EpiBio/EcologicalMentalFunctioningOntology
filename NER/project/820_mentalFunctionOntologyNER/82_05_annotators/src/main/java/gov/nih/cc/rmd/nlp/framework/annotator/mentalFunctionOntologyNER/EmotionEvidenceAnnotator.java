// =================================================
/**
 * emotionEvidenceAnnotator marks emotion Evidence from sentences
 *   
 * 
 * @author  GD
 * @created 2023.02.15
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.inFACT.EmotionEvidence;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.Utterance;




public class EmotionEvidenceAnnotator extends JCasAnnotator_ImplBase {
 
  
  
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
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," Started EmotionEvidenceAnnotator");
    
    List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Utterance.typeIndexID , true);
    
    if (sentences != null && !sentences.isEmpty() )
      for ( Annotation sentence : sentences )
        processSentence(pJCas, sentence);
  
  
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," End EmotionEvidenceAnnotator");
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   



// =================================================
  /**
   * processSentence finds emotion evidence in this sentence
   * 
   * @param pJCas
   * @param pSentence
  */
  // =================================================
  private void processSentence(JCas pJCas, Annotation pSentence) {
    
    
      List<Annotation> terms = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, LexicalElement.typeIndexID, pSentence.getBegin(), pSentence.getEnd() , true);
      
      if ( terms != null && !terms.isEmpty()) {
        for ( Annotation term: terms ) {
          
         
          String semanticTypes = ((LexicalElement) term).getSemanticTypes();
          if ( semanticTypes == null || semanticTypes.trim().length() == 0 ) continue;
          
          if ( semanticTypes.contains("emotion") ) {
           
            createEmotionEvidence( pJCas, term);
          }
        } // end loop through terms
      } // end if there are terms
    
  } // end Method processSentence() ------------------

        // =================================================
        /**
         * createEmotionEvidence [TBD] summary
         * 
         * @param pJCas
         * @param term
        */
        // =================================================
           private final void createEmotionEvidence(JCas pJCas, Annotation pMention) {
          Annotation statement = new EmotionEvidence(pJCas);
          statement.setBegin( pMention.getBegin());
          statement.setEnd( pMention.getEnd());
          statement.addToIndexes();

          
        }  // end Method createemotionEvidence() ------------


//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
}

  
  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
      
   
      
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   *                
   *   This has an optional parameter, a resource directory with pattern files in it
   *   --pagecategoryPatternsDir= ./resources/vinciNLPFramework/pageFiltering
   *
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
  
    
    try {
      
    
    } catch (Exception e) {
      throw new ResourceInitializationException();
    }
   
   
      
  } // end Method initialize() -------
 
  


  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
  
   
  
} // end Class LineAnnotator() ---------------

