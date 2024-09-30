// =================================================
/**
 * UseInFACTSentencesAnnotator converts the sentences that come
 * from gate to framework's sentences (spans that is)
 *   
 * 
 * @author  GD
 * @created 2023.05.17
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

import gov.nih.cc.rmd.framework.Manual_Sentence;
import gov.nih.cc.rmd.inFACT.manual.Comcog_no;
import gov.nih.cc.rmd.inFACT.manual.Comcog_yes;
import gov.nih.cc.rmd.inFACT.manual.IPIRno;
import gov.nih.cc.rmd.inFACT.manual.IPIRyes;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.model.Concept;




public class UseInFACTSentencesAnnotator extends JCasAnnotator_ImplBase {
 
  
  
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
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process","started UseInFACTSentenceAnnotator");
    
    List<Annotation> inFACTSentences0 = UIMAUtil.getAnnotations(pJCas, Manual_Sentence.typeIndexID , false);
    
    List<Annotation> inFACTSentences1 = UIMAUtil.getAnnotations(pJCas, Comcog_yes.typeIndexID , false);
    List<Annotation> inFACTSentences2 = UIMAUtil.getAnnotations(pJCas, Comcog_no.typeIndexID , false);
    List<Annotation> inFACTSentences3 = UIMAUtil.getAnnotations(pJCas, IPIRyes.typeIndexID , false);
    List<Annotation> inFACTSentences4 = UIMAUtil.getAnnotations(pJCas, IPIRno.typeIndexID , false);
   
    
    
    
    List<Annotation> frameworkSentences = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID , false);
    
    if ( ( inFACTSentences0 != null && !inFACTSentences0.isEmpty() ||
           inFACTSentences1 != null && !inFACTSentences1.isEmpty() ||
           inFACTSentences2 != null && !inFACTSentences2.isEmpty() ||
           inFACTSentences3 != null && !inFACTSentences3.isEmpty() ||
           inFACTSentences4 != null && !inFACTSentences4.isEmpty()  )
        
        
        &&
         frameworkSentences != null && !frameworkSentences.isEmpty() ) 
    
      for ( Annotation frameworkSentence : frameworkSentences )
    		frameworkSentence.removeFromIndexes();
    
  
    //  if there are manual sentence segmentations
    if ( inFACTSentences0 != null && !inFACTSentences0.isEmpty())
      for ( Annotation inFACTSentence : inFACTSentences0)
        createFrameworkSentence( pJCas, inFACTSentence);
    
    else 
    {
      
      // there are comcog yes and comcog no manual sentences  
      boolean comcogSentencesSeen = false;
      if ( inFACTSentences1 != null && !inFACTSentences1.isEmpty())
        for  ( Annotation inFACTSentence : inFACTSentences1) {
          createFrameworkSentence( pJCas, inFACTSentence);
          comcogSentencesSeen = true;
        }
    
      if ( inFACTSentences2 != null && !inFACTSentences2.isEmpty())
        for ( Annotation inFACTSentence : inFACTSentences2) {
          createFrameworkSentence( pJCas, inFACTSentence);
          comcogSentencesSeen = true;
        }
      
      if ( !comcogSentencesSeen ) {
        if ( inFACTSentences3 != null && !inFACTSentences3.isEmpty())
          for  ( Annotation inFACTSentence : inFACTSentences3) 
            createFrameworkSentence( pJCas, inFACTSentence);
     
        if ( inFACTSentences4 != null && !inFACTSentences4.isEmpty())
          for ( Annotation inFACTSentence : inFACTSentences4) 
            createFrameworkSentence( pJCas, inFACTSentence);
           
      }// end if comcogSentences not Seen
    } // end if manual sentences not seen
    
    
    
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process","end UseInFACTSentenceAnnotator");
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   
        // =================================================
        /**
         * createFrameworkSentence 
         * 
         * @param pJCas
         * @param pSentence 
         
        */
        // =================================================
           private final Annotation createFrameworkSentence(JCas pJCas, Annotation pSentence ) {
          
        	   Sentence statement = new Sentence( pJCas);
        	   statement.setBegin( pSentence.getBegin());
        	   statement.setEnd( pSentence.getEnd());
        	   try {
        	   statement.setId( "UseInFACTSentence_" + ((Concept) pSentence).getId());
        	   } catch (Exception e) {
        	     GLog.println(GLog.ERROR_LEVEL, this.getClass(), "createFrameworkSentence", "Issue getting the id " + e.toString());
        	   }
        	   statement.addToIndexes();
        	  
          return statement;
        }  // end Method createBehaviorEvidence() ------------


         
           
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

