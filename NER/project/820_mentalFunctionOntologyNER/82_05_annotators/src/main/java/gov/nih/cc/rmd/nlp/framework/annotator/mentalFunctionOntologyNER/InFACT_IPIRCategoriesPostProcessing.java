// =================================================
/**
 * InFACT_IPIRCategories creates IPIR categories overlaying the IPIR mentions
 * 
 * @author  GD
 * @created 2023.03.14
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER;



import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.inFACT.manual.IPIRyes;
import gov.nih.cc.rmd.inFACT.x.Comcog_yes;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;

import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class InFACT_IPIRCategoriesPostProcessing extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process removes the originals that need to be replaced
   * by the newly made IPIR_yes annotations so that
   * the final gate file looks like the original IPIR_yes
   * have been updated with updated features
   * 
   * the criteria for removal/replacement is if
   *   the id, span, [maybe set name - for now, no]
   *  are the same.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," Started IPIRCategoriesPostProcessing");

   // Loop through utterances  (that's the span we won't cross) 
    
    
    List<Annotation> updatedIPIRMentions = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.x.IPIR_yes.typeIndexID, true );
           
    
    // ----------------------------------------------
    // If there are IPIRyes annotations, iterate through these sentences 
    if ( updatedIPIRMentions != null && !updatedIPIRMentions.isEmpty()) {
      for ( Annotation ipirYesSentence : updatedIPIRMentions )
          processIPIRYesSentence ( pJCas, ipirYesSentence);
      
    }
    
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," End IPIRCategoriesPostProcessing");
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", " Issue with ipir Categories" + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   


  // =================================================
  /**
   * processIPIRSentence creates ipir sub category annotations an IPIR Sentence
   * 
   * @param pJCas
   * @param pIPIRYesSentence
  */
  // =================================================
   private final void processIPIRYesSentence(JCas pJCas, Annotation pIPIRYesSentence) {
    
     try {
       int id = Integer.parseInt(((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getId());
       String setName = ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getAnnotationSetName();
       String typeName = "IPIR_yes";
     
        
      gov.nih.cc.rmd.inFACT.manual.IPIRyes statement = new gov.nih.cc.rmd.inFACT.manual.IPIRyes  (pJCas );
      statement.setBegin(              ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getBegin() );
      statement.setEnd(                ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getEnd() );
      statement.setId(                 ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getId() );
      statement.setAnnotationSetName(  ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getAnnotationSetName() );
      statement.setInteraction(        ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getInteraction() );
      statement.setD730(               ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getD730() );
      statement.setD740(               ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getD740() );
      statement.setD7400(              ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getD7400() );
      statement.setD750(               ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getD750 () );
      statement.setD760(               ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getD760() );
      statement.setD770(               ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getD770() );
      statement.setD779(               ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getD779() );
      
      statement.setTimeBucket(         ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getTimeBucket());
 	  statement.setDifficult_to_determine( ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).getDifficult_to_determine());
 	  statement.addToIndexes();
   
      
       
       /*
       
       
       // find the original
       List<Annotation> potentialOriginals = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, gov.nih.cc.rmd.inFACT.manual.IPIRyes.typeIndexID,  pIPIRYesSentence.getBegin(), pIPIRYesSentence.getEnd(), false );
       
       if ( potentialOriginals != null && !potentialOriginals.isEmpty() )
         for ( Annotation potentialOriginal : potentialOriginals )
           if ( ((gov.nih.cc.rmd.gate.Annotation) potentialOriginal).getId() == id &&
                ((gov.nih.cc.rmd.gate.Annotation) potentialOriginal).getType().equals( typeName)  && 
            //    ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal).getGateAnnotationSetName().equals( setName)  && 
                ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal).getBegin() ==  pIPIRYesSentence.getBegin()  &&
                ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal).getEnd() ==  pIPIRYesSentence.getEnd()  )  {
            
             String originalDifficultToDetermineValue = getOriginalValue( ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal), "difficult_to_determine" );
             String originalTimeBucket                = getOriginalValue( ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal), "TimeBucket" );
             
             ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).setDifficult_to_determine( Boolean.valueOf( originalDifficultToDetermineValue) );
             ((gov.nih.cc.rmd.inFACT.x.IPIR_yes) pIPIRYesSentence ).setTimeBucket(  originalTimeBucket );
             potentialOriginal.removeFromIndexes();
           }
       */
       
     } catch (Exception e) {
       e.printStackTrace();
       String msg = "Issue with removing the original that has been updated " + e.toString();
       GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processIPIRYesSentence", msg);
       
     }
    
     
   } // end Method processIPIRSentence() ------------



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
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
   
      
  } // end Method initialize() -------
 

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
  
   
  
} // end Class InFact_IPIRCategoriesPostProcessing() ---------------

