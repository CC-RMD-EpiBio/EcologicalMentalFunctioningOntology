// =================================================
/**
 * MakeXFromManualFeataures creates X annotations from
 * the machine and manual features for the IPIR_yes 
 * and ComCog_yes Annotations.
 * 
 * Downstream annotators are looking for the x.__yes
 * versions, not the Manual____yes versions. 
 * 
 * The Manual versions are what get mapped in from
 * the GateReader.  
 * 
 *   
 * 
 * @author  GD
 * @created 2023.09.01
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

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.MakeGoldFromIPIRFeatures;




public class MakeXFromMachineFeatures extends JCasAnnotator_ImplBase {
 
  
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
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process","started MakeXFromManualFeatures");
    
    String documentId = VUIMAUtil.getDocumentId(pJCas);
    // ----------------------------
    // IPIR
    // ---------------------------
                                                                      
    
    // turn manual ipir mentions into x ipir sentence mentions
    
    List<Annotation> IPIRYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.model.IPIR_yes.typeIndexID , false);
    
    
    if (IPIRYess != null && !IPIRYess.isEmpty() )
      for ( Annotation ipirYes : IPIRYess ) 
    	  MakeGoldFromIPIRFeatures.createIPIRYes( pJCas, (gov.nih.cc.rmd.inFACT.bstract.IPIR_yes) ipirYes, this.ipirAnnotationSetName);
      
      
  
    List<Annotation> comCogYess = UIMAUtil.getAnnotations(pJCas, gov.nih.cc.rmd.inFACT.model.Comcog_yes.typeIndexID , false);
    if (comCogYess != null && !comCogYess.isEmpty() )

      for ( Annotation comcogYes : comCogYess ) 
        MakeGoldFromComCogFeatures.createComCogYes( pJCas, (gov.nih.cc.rmd.inFACT.bstract.Comcog_yes) comcogYes, this.comcogAnnotationSetName);
    
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process"," End MakeXFromManualFeatures");
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
  
  } // end Method process() ----------------
   
 

  // ----------------------------------
  /**
   * destroy
   * 
   **/
  // ----------------------------------
  public void destroy() {
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());
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
    this.comcogAnnotationSetName = U.getOption(pArgs, "--comcogAnnotationSetName=", "comcog_category");
    this.ipirAnnotationSetName = U.getOption(pArgs, "--ipirAnnotationSetName=", "ipir_category");

    
  } // end Method initialize() -------
 
  

  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
   private PerformanceMeter performanceMeter = null;
   
   private String comcogAnnotationSetName = "comcog_category";
   private String ipirAnnotationSetName = "pir_category";
 
   
  
} // end Class LineAnnotator() ---------------

