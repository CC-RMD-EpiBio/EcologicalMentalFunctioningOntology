// =================================================
/**
 * EfficacyReImaginedPreProcessingAnnotator creates new machine annotations from
 * the manual comcog_yes and ipir_yes annotations
 * 
 *
 * comcog_setnence_model:ComCog_yes
 *  
 *   
 * 
 * @author  GD
 * @created 2023.11.28
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

import gov.nih.cc.rmd.inFACT.x.Comcog_yes;
import gov.nih.cc.rmd.inFACT.x.IPIR_yes;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;




public class EfficacyReImaginedPreProcessingAnnotator extends JCasAnnotator_ImplBase {
 
  
  
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
    
    
    List<Annotation> comCogYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.manual.Comcog_yes.typeIndexID , false);
    List<Annotation> ipirYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.manual.IPIRyes.typeIndexID , false);
    
                                                                 
    
    if (comCogYess != null && !comCogYess.isEmpty() )
      for ( Annotation comcogYes : comCogYess ) 
    	  createMachineCogCogYes( pJCas, ((gov.nih.cc.rmd.inFACT.manual.Comcog_yes) comcogYes));
     
    if (ipirYess != null && !ipirYess.isEmpty() )
        for ( Annotation ipirYes : ipirYess ) 
      	  createMachineIPIRYes( pJCas, ((gov.nih.cc.rmd.inFACT.manual.IPIRyes) ipirYes));
       
   
   
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   

  //----------------------------------
  /**
   * createMachineCogCogYes  
   * @throws Exception
   * 
   **/
  // ----------------------------------
  private final void createMachineCogCogYes(JCas pJCas, gov.nih.cc.rmd.inFACT.manual.Comcog_yes pAnnotation ) throws Exception {
      
	 Comcog_yes statement = new Comcog_yes(pJCas);
	 statement.setId(pAnnotation.getId());
	 statement.setBegin(pAnnotation.getBegin());
	 statement.setEnd(pAnnotation.getEnd());
	 statement.setAnnotationSetName("comcog_sentence_model");
	 // statement.setDifficult_to_determine(pAnnotation.getDifficult_to_determine());
	 // statement.setTimeBucket(evalGoldFocus);
	 statement.addToIndexes();
	 

	  
  } // end Method createMachineCogCogYes() -------
  
//----------------------------------
  /**
   * createMachineIPIRYes  
   * @throws Exception
   * 
   **/
  // ----------------------------------
  private final void createMachineIPIRYes(JCas pJCas, gov.nih.cc.rmd.inFACT.manual.IPIRyes pAnnotation ) throws Exception {
      
	 IPIR_yes statement = new IPIR_yes(pJCas);
	 statement.setId(pAnnotation.getId());
	 statement.setBegin(pAnnotation.getBegin());
	 statement.setEnd(pAnnotation.getEnd());
	 statement.setAnnotationSetName("ipir_sentence_model");
	 // statement.setDifficult_to_determine(pAnnotation.getDifficult_to_determine());
	 // statement.setTimeBucket(evalGoldFocus);
	 statement.addToIndexes();
	 

	  
  } // end Method createMachineCogCogYes() -------
  
  


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

