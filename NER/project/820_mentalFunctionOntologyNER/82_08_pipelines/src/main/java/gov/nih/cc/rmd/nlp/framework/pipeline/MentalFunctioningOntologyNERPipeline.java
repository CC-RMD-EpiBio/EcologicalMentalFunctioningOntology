package gov.nih.cc.rmd.nlp.framework.pipeline;



import gov.nih.cc.rmd.nlp.framework.annotator.FilterWriter;
import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.MentalFunctionOntologyNERAnnotator;
import gov.nih.cc.rmd.nlp.framework.utils.U;

// =================================================
/**
 * MentalFunctioningOntologyNERPipeline runs the pipeline for mentalFunctionOntologyNER
 *        
 * 
 * @author  G.o.d.
 * @created 2022.03.28
 *
 */


  public class MentalFunctioningOntologyNERPipeline extends AbstractPipeline  {


    // =======================================================
    /**
     * Constructor  
     *
     * @param pArgs
     */
    // =======================================================
    public MentalFunctioningOntologyNERPipeline(String[] pArgs) {
      super(pArgs);
    
    }


    // -----------------------------------------
    /**
     * Constructor 
     * 
     * @throws Exception
     */
    // -----------------------------------------
    public MentalFunctioningOntologyNERPipeline()  throws Exception {
     super( );
      
    } // End Constructor() -----------------------------
    
    
	// =======================================================
	/**
	 * createPipeline defines the pipeline
	 * 
	 * @param pArgs
	 * @return FrameworkPipeline
	 */
	// =======================================================
	@Override
  public FrameworkPipeline createPipeline(String[] pArgs) {

	    FrameworkPipeline pipeline = new FrameworkPipeline(pArgs);
	    String typeDescriptor = U.getOption( pArgs, "--typeDescriptor=", "gov.nih.cc.rmd.mentalFunctionOntologyNER.Model"); //  "gov.nih.cc.rmd.FrameworkAndCtakes");
        pipeline.setTypeDescriptorClassPath( typeDescriptor);  //<--- in the 82_03-type.descriptor/src/main/resources ... folder
	   
       setPipeline(pipeline, pArgs);
     
       return pipeline;
    
    }  // End Method createPipeline() ======================
    
	
	// =======================================================
	/**
	 * setPipeline defines the pipeline
	 * 
	 * @param pArgs
	 * @return FrameworkPipeline
	 */
	// =======================================================
	public synchronized static void setPipeline(FrameworkPipeline pipeline, String[] pArgs) {
	    
		
		  UimaContextParameter argsParameter = new UimaContextParameter("args",  pArgs, "String",  true, true);
	      String typeDescriptor = U.getOption( pArgs, "--typeDescriptor=", "gov.nih.cc.rmd.mentalFunctionOntologyNER.Model"); //  "gov.nih.cc.rmd.FrameworkAndCtakes");
	      pipeline.setTypeDescriptorClassPath( typeDescriptor);  //<--- in the 82_03-type.descriptor/src/main/resources ... folder
		   

	      FrameworkRMDSentenceNoFilterPipeline.setPipeline(pipeline, pArgs);
	     
	      
		  pipeline.add(              MentalFunctionOntologyNERAnnotator.class.getCanonicalName(), argsParameter);
		    
		
	      
	} // end Method setPipeline() ----------------------------

} // end Class DateAndTimePipeline

