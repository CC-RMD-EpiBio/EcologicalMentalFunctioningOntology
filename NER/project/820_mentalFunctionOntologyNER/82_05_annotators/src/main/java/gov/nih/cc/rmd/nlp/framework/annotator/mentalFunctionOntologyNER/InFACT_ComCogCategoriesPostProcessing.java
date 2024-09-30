// =================================================
/**
 * InFACT_ComcogCategoriesPostProcessing filters out mentions
 * that will not be needed downstream.
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


import gov.nih.cc.rmd.inFACT.x.Comcog_yes;


import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class InFACT_ComCogCategoriesPostProcessing extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process removes the originals that need to be replaced
   * by the newly made Comcog_yes annotations so that
   * the final gate file looks like the original Comcog_yes
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
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," Started ComCogCategoriesPostProcessing");


   // Loop through utterances  (that's the span we won't cross) 
    
    
    List<Annotation> updatedComcogMentions = UIMAUtil.getAnnotations(pJCas, Comcog_yes.typeIndexID, false );
           
    
    // ----------------------------------------------
    // If there are Comcog_yes annotations, iterate through these sentences 
    if ( updatedComcogMentions != null && !updatedComcogMentions.isEmpty()) {
      for ( Annotation comcogYesSentence : updatedComcogMentions )
          processComcogYesSentence ( pJCas, comcogYesSentence);
      
    }
    
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," Started ComCogCategoriesPostProcessing");
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", " Issue with comcog Categories" + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   


  // =================================================
  /**
   * processComcogSentence creates comcog sub category annotations an Comcog Sentence
   * 
   * @param pJCas
   * @param pComcogYesSentence
  */
  // =================================================
   private final void processComcogYesSentence(JCas pJCas, Annotation pComcogYesSentence) {
    
     try {

      // int id = Integer.parseInt( ((Comcog_yes) pComcogYesSentence ).getId());
      // String setName = ((Comcog_yes) pComcogYesSentence ).getAnnotationSetName();
      // String typeName = "Comcog_yes";

       
       // turn this comcog yes into a manual comcog yes
       
       
       gov.nih.cc.rmd.inFACT.manual.Comcog_yes statement = new gov.nih.cc.rmd.inFACT.manual.Comcog_yes(pJCas);
       statement.setBegin( pComcogYesSentence.getBegin());
 	  statement.setEnd( pComcogYesSentence.getEnd());
 	  statement.setId( "PostProcessing_" + ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getId());
 	  statement.setAnnotationSetName( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getAnnotationSetName() );
 	  statement.setAnnotationSetName(this.comcogSubcategorySetName );
 	  statement.setD110_d129  ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD110_d129() );
 	  statement.setD130_d159  ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD130_d159() );      
 	  statement.setD160       ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD160() );            
 	  statement.setD163       ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD163() );
 	  statement.setD166       ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD166() );
 	  statement.setD170       ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD170() );
 	  statement.setD172       ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD172() );
 	  statement.setD175       ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD175() );
 	  statement.setD177       ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD177() );
 	  statement.setD179       ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD179() );
 	  statement.setD210_d220  ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD210_d220() );
 	  statement.setD230       ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD230() );
 	  statement.setD240       ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD240() );
 	  statement.setD310_d329  ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD310_d329() );
 	  statement.setD330_d349  ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD330_d349() );
 	  statement.setD350_d369  ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getD350_d369() );
 	  statement.setApplied_memory  ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getApplied_memory() );
 	  statement.setAdaptation    (   ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getAdaptation() );
 	  statement.setPacing        ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getPacing() );
 	  statement.setPersistence   ( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getPersistence() );
 	  
 	  statement.setTimeBucket(((gov.nih.cc.rmd.inFACT.x.Comcog_yes)  pComcogYesSentence).getTimeBucket());
 	  statement.setDifficult_to_determine( ((gov.nih.cc.rmd.inFACT.x.Comcog_yes) pComcogYesSentence).getDifficult_to_determine());
 	  statement.addToIndexes();
       
       
     
     /*  
       
       // find the original
       List<Annotation> potentialOriginals = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, gov.nih.cc.rmd.gate.Annotation.typeIndexID,  pComcogYesSentence.getBegin(), pComcogYesSentence.getEnd(), false );
       
       if ( potentialOriginals != null && !potentialOriginals.isEmpty() )
         for ( Annotation potentialOriginal : potentialOriginals )
           if ( ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal).getId() == id &&
                ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal).getType().equals( typeName)  && 
            //    ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal).getGateAnnotationSetName().equals( setName)  && 
                ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal).getBegin() ==  pComcogYesSentence.getBegin()  &&
                ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal).getEnd() ==  pComcogYesSentence.getEnd()  )  {
            
             String originalDifficultToDetermineValue = getOriginalValue( ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal), "difficult_to_determine" );
             String originalTimeBucket                = getOriginalValue( ((gov.nih.cc.rmd.gate.Annotation)  potentialOriginal), "TimeBucket" );
             
             ((Comcog_yes) pComcogYesSentence ).setDifficult_to_determine( Boolean.valueOf( originalDifficultToDetermineValue) );
             ((Comcog_yes) pComcogYesSentence ).setTimeBucket(  originalTimeBucket );
             
             potentialOriginal.removeFromIndexes();
           }
       */
       
     } catch (Exception e) {
       e.printStackTrace();
       String msg = "Issue with removing the original that has been updated " + e.toString();
       GLog.println(GLog.ERROR_LEVEL, this.getClass(), "processComcogYesSentence", msg);
       
     }
    
     
   } // end Method processComcogSentence() ------------



   
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
    this.comcogSubcategorySetName = U.getOption(pArgs,  "--comcogAnnotationSetName=", comcogSubcategorySetName);
   
      
  } // end Method initialize() -------
 

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
   private String comcogSubcategorySetName = "comcog_subcategory_model";
   
  
} // end Class InFact_ComcogCategoriesPostProcessing() ---------------

