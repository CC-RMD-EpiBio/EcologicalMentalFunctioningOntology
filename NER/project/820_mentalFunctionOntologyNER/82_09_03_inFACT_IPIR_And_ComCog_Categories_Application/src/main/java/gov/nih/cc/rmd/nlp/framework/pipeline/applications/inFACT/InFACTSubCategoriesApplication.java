// ------------------------------------------------------------
/**
 * InFACTSubCategoriesApplication kicks off the combined IPIR and comcog
 * sub categories pipeline for the deliverable a files.
 * 
 * This application will move the original files aside, read
 * in the deep learning predictions, and add the subcategories
 * then output the cumulated annotations out to what was
 * the original named files.  The output will be gate xml
 * xmi, and txt files.
 * 
 * The question remaining is if I have the program write
 * out to the output dir or move it manually.
 *  
 * 
 * @author  gd
 * @created 2023.09.01
 * 
 */

package gov.nih.cc.rmd.nlp.framework.pipeline.applications.inFACT;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.MentalFunctionOntologyNERAnnotator;
import gov.nih.cc.rmd.nlp.framework.pipeline.InFACTSubCategorizationBySentencePipeline;
import gov.nih.cc.rmd.nlp.framework.pipeline.applications.FrameworkBaselineApplication;
import gov.nih.cc.rmd.nlp.framework.pipeline.applications.FrameworkBaselineApplicationAux;
import gov.nih.cc.rmd.nlp.framework.pipeline.applications.FrameworkReadersAux;
import gov.nih.cc.rmd.nlp.framework.pipeline.applications.ScaleOutApplication;
import gov.nih.cc.rmd.nlp.framework.pipeline.applications.ScaleOutApplicationAux;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;


public class InFACTSubCategoriesApplication {

  
//------------------------------------------
 /**
  * main
  *   Options:
  *
  *
  * @param pArgs
  */
 // ------------------------------------------
 public static void main(String[] pArgs) {
 
   int returnCode = -1;
   try {
     boolean finished = false;
    
     
     String args[] = setArgs(pArgs);
     String version = U.getOption(args,  "--version=",   "first");
     
     GLog.println("version="  + version );
     GLog.set(args);

     String               outputDir = U.getOption(args, "--outputDir=", "./"); 
     GLog.error_println("The output should now be in " + outputDir );
      
     boolean threaded = Boolean.valueOf(U.getOption(args, "--threaded=", "false")); 
     
     if ( threaded ) 
       finished = main_threaded( args);
     else 
       finished = main_unthreaded( args);
  
     if ( finished ) returnCode = 0;
     // ----------------------
     GLog.println(" DOHN");
     GLog.println("The output should now be in " + outputDir );
  
   } catch (Exception e2) {
     e2.printStackTrace();
     String msg = "Issue with the application " + e2.toString();
     System.err.println(msg);
   }
   
   System.exit(returnCode);
 }  // End Method main() ------------------------------------

 
 
 // ------------------------------------------
 /**
  * main_unthreaded
  * 
  * @param pArgs
  * @return boolean  (finished or not)
  */
 // ------------------------------------------
 public static boolean main_unthreaded(String[] pArgs) {

  
   boolean finished = false;
     try {
     
   //    String args[] = setArgs( pArgs);
   //   GLog.set( args );
       String[] args = pArgs;
     
     
       // --------------------------
       // Set up a performance meter
       // --------------------------
       PerformanceMeter meter = FrameworkBaselineApplication.createPerformanceMeter( args);
       meter.begin("Starting the application");
     
     
   
    
      // -------------------
     // Create a BaselineFrameworkApplication instance
     // -------------------
     FrameworkBaselineApplicationAux application = new FrameworkBaselineApplicationAux();

     // -------------------
     // Add a performance meter to the application (This is optional)
     application.addPerformanceMeter(meter);

     // -------------------
     // Create a pipeline, retrieve an analysis engine
     // that uses the pipeline, attach it to the application
     // -------------------
     InFACTSubCategorizationBySentencePipeline   pipeline = new InFACTSubCategorizationBySentencePipeline( args );
     AnalysisEngine                ae = pipeline.getAnalysisEngine();
     application.setAnalsyisEngine(ae);

     // -------------------
     // Create a reader - this relies on the --inputFormatType=TEXT_READER|GATE_READER|XMI_READER|VTT_READER ....
     // -------------------
     application.createReader(args);

     // ------------------
     // Create a writers to write out the processed cas's (write out xmi, vtt files, stat file, and concordance file)
     // ------------------
     application.addWriters(args);
    
      
    
     meter.mark("Finished Initialization");

     // ------------------
     // gather and process the cas's
     // -----------------

     try {
       application.process();
     } catch (Exception e) {
       e.getStackTrace();
       String msg = "Issue processing the files " + e.toString();
       System.err.println(msg);
     }
    // application.destroy();
     
     if ( application != null ) {
       if ( application.getReader() != null )
         finished = application.getReader().allProcessed();
       else
         finished = true;
     
       application.destroy();
     }
     meter.stop();

     // If need be, we can move and re-name the files in the outputDir 
     
     // ----------------------
     System.err.println(" DOHN");

   } catch (Exception e2) {
     e2.printStackTrace();
     String msg = "Issue with the application " + e2.toString();
     System.err.println(msg);
   }
   
     return finished;
  
 
 } // End Method main() -----------------------------------

 
  
//------------------------------------------
 /**
  * main_threaded
  * 
  * 
  * @param pArgs
  * @return boolean
  */
 // ------------------------------------------
 public static boolean main_threaded(String[] pArgs) {

   boolean finished = false;
   try {
     String[] args = pArgs;
     // --------------------
     // Read in any command line arguments, and add to them needed ones
     // The precedence is command line args come first, followed by those
     // set by the setArgs method, followed by the defaults set here.
     // --------------------
    // String args[] = setArgs(pArgs);
    // GLog.set( args );
     
     
     // ---------------------------
     // Performance configuration Parameters
     // ---------------------------
     int                      metric = Integer.parseInt(U.getOption(args,  "--metric=",                    "100"));
     int             numberToProcess = Integer.parseInt(U.getOption(args,  "--numberToProcess=",            "-1"));
     int initialNumberOfApplications = Integer.parseInt(U.getOption(args,  "--initialNumberOfApplications=", "1"));
     int        numberOfApplications = Integer.parseInt(U.getOption(args,  "--numberOfApplications=",       "80"));
     int     maxNumberOfApplications = Integer.parseInt(U.getOption(args,  "--maxNumberOfApplications=",     "80"));
     int               maxSystemLoad = Integer.parseInt(U.getOption(args,  "--maxSystemLoad=",              "80"));
     int                   recycleAt = Integer.parseInt(U.getOption(args,  "--recycleAt=",                  "10"));
     int                     seconds = Integer.parseInt(U.getOption(args,  "--seconds=",                    "30"));
     String                 inputDir = U.getOption(args, "--inputDir=", "/82_01_data/IPIRCategoryExamples");
     String                outputDir = U.getOption(args, "--outputDir=", inputDir + "_IPIRCategoryExamples_" + "yyyy_mm_dd");
     int       memorySizeThresholdMb = Integer.parseInt(U.getOption(args, "--memorySizeThresholdMb=", "100"));  
     

     
     // --------------------------
     // Set up a performance meter
     // --------------------------
     PerformanceMeter meter = FrameworkBaselineApplication.createPerformanceMeter( args);
     meter.begin("Starting the application");
     
    // -------------------
     // Create the framework set of servers
     // -------------------
    ScaleOutApplicationAux application = new ScaleOutApplicationAux(initialNumberOfApplications, outputDir, metric, recycleAt, numberToProcess, meter, memorySizeThresholdMb);
     

     // -------------------
     // set up a reader  relies upon argument "--inputFormat= FrameworkBaselineApplication.TEXT_READER_  );
     // -------------------
     application.createReader( args );

     
     // -------------------
     // set up writer(s)
     //  String outputFormat = U.getOption(args, "--outputFormat=", FrameworkBaselineApplication.STATS_WRITER_);
     // -------------------
     application.addWriters(args ); // <---- relies upon "--outputFormat=FrameworkBaselineApplication.STATS_WRITER_);
    
     // -------------------
     // Create an engine with a pipeline 
     // -------------------
     // -------------------
     // Create a pipeline, retrieve an analysis engine
     // that uses the pipeline, attach it to the application
     // -------------------
     application.setPipelines(InFACTSubCategorizationBySentencePipeline.class, args);
    

     // --------------------
     // Run until done : if the number to process == 1, it processes them all
     // --------------------
 
     application.run(numberToProcess, 
                     initialNumberOfApplications, 
                     maxNumberOfApplications,
                     maxSystemLoad,
                     recycleAt,
                     seconds);
     
     
     finished = ScaleOutApplication.SingleReader.allProcessed();
     application.destroy();
     
     meter.stop();

     // ----------------------
     System.err.println(" DOHN");
    
    
     System.err.println("The output should now be in " + outputDir );
   
   } catch (Exception e2) {
     e2.printStackTrace();
     String msg = "Issue with the application " + e2.toString();
     System.err.println(msg);
   }
   
   return finished;
  

} // End Method main() -----------------------------------


  

  // ------------------------------------------
  /**
   * setArgs
   * 
   * 
   * @return
   */
  // ------------------------------------------
  public static String[] setArgs(String pArgs[]) {

    // -------------------------------------
    // dateStamp
    String dateStamp = U.getDateStampSimple();

    // -------------------------------------
    // Input and Output

    boolean threaded = Boolean.valueOf(U.getOption(pArgs, "--threaded=", "false")); 
    
    String    inputDir  = U.getOption(pArgs, "--inputDir=", "/projects/fy23_nlp_a/data/interlingua/nlp_predictions_IPIR_COMCOG");
    String productionInputDir = U.getOption(pArgs, "--productionInputDir=", "/projects/fy23_nlp_a/data/interlingua/nlp_predictions");
    		   
    String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_SubCategories_" + dateStamp);
    String       logDir = U.getOption(pArgs, "--logDir=",   outputDir + "/logs" ); 
    String   printToLog = U.getOption(pArgs, "--printToLog=", "false");
   String   printToConsole = U.getOption(pArgs, "--printToConsole=", "false");
    String silenceGLogMsgs = U.getOption(pArgs, "--silenceGLogMsgs=",  "true");
  String  detailedSummary = U.getOption(pArgs, "--detailedSummary=", "true");
   String INFACT_MODE= U.getOption(pArgs, "--INFACT_MODE=", "true" );
   String usePronounsInD750 = U.getOption( pArgs, "--usePronounsInD750=", "true");
   String segmentRelevantFilter =  U.getOption(pArgs, "--segmentRelevantFilter=", "true" );
   String inFACTFileNamingConvention =  U.getOption(pArgs, "--inFACTFileNamingConvention=", "true" );
    
 //   String inputFormatType  = U.getOption(pArgs, "--inputFormat=",  FrameworkReadersAux.MULTI_PAGE_TEXT_READER_);
    String inputFormatType  = U.getOption(pArgs, "--inputFormat=", FrameworkReadersAux.GATE_READER_);
    // if this is coming from a GATE/Interlingua source, set the payloadAnnotationSetName to comcog
    String payloadAnnotationSetName = U.getOption(pArgs, "--payloadAnnotationSetName=", "ipir_sentence_model:comcog_sentence_manual:comcog_sentence_model:ipir:comcog:comcog_sentence:ipir_sentence");
    String  comcogAnnotationSetName = U.getOption(pArgs, "--comcogAnnotationSetName=", "comcog_classification");
    String    ipirAnnotationSetName = U.getOption(pArgs, "--ipirAnnotationSetName=", "ipir_classification");
    String  modelAnnotationSetName = U.getOption(pArgs,  "--modelAnnotationSetName=", comcogAnnotationSetName);
    // note: for the threaded version, make this staticOutputFormat if you want this to be synchronized 
    String outputFormatType = U.getOption(pArgs, "--outputFormat=", 
      //  FrameworkBaselineApplication.VTT_WRITER_  + ":" +                  
    //    FrameworkBaselineApplication.XMI_WRITER_ + ":" +
        FrameworkBaselineApplication.INTERLINGUA_WRITER_   + ":"   
        );
    
    
    String typeDescriptor = U.getOption( pArgs, "--typeDescriptor=", "gov.nih.cc.rmd.mentalFunctionOntologyNER.SubCategoriesModel"); //  "gov.nih.cc.rmd.FrameworkAndCtakes");
    
    String profilePerformanceLogging = U.getOption(pArgs,  "--profilePerformanceLogging=", "true");
    String setMetaDataHeader = U.getOption(pArgs,  "--setMetaDataHeader=", "false");
    String gateHome = U.getOption(pArgs, "--gateHome=",  "C:/Program Files (x86)/GATE_Developer_8.5.1");
   
    
    String localTerminologyFiles = U.getOption(pArgs, "--localTerminologyFiles=",  MentalFunctionOntologyNERAnnotator.MentalFunctioningOntologyNERLexica );
                                                // needed
    String LRPRNFile = U.getOption( pArgs, "--LRPRN=", "/resources/vinciNLPFramework/term/2020AA/LRPRN");
    String mentalFunctioningOntologyCategories = U.getOption(pArgs,"--mfoCategories=", "ComcogActivities" );  // or individual ones or major categories of
    
    String gateViewAnnotationSetNameFilterOutList_ = U.getOption(pArgs, "--gateViewAnnotationSetNameFilterOutList=", "framework");
    
    String processingListings =  U.getOption( pArgs,  "--processListings=", "false");
    String highlightMentions = U.getOption(pArgs,  "--highlightMentions=", "false"); 
    String columnDelimiter = U.getOption(pArgs,  "--columnDelimiter=", "PIPE");   //  COLUMN|PIPE
    
    String        fileSizeThreshold = U.getOption(pArgs,  "--fileSizeThreshold=",  "1508000" ); // 1508000" ); //"1508000");  // largest cdr: 1,574,050 cpd: 1,526,853 
    String    memorySizeThresholdMb = U.getOption(pArgs, "--memorySizeThresholdMb=", "100");  
    String sentenceSizeThreshold = U.getOption( pArgs,  "--sentenceSizeThreshold=",  "100");
   
    // ---------------------------
    // Performance configuration Parameters
    // ---------------------------
    int                      metric = Integer.parseInt(U.getOption(pArgs,  "--metric=",                    "100"));
    int             numberToProcess = Integer.parseInt(U.getOption(pArgs,  "--numberToProcess=",            "-1"));
    int        numberOfApplications = Integer.parseInt(U.getOption(pArgs,  "--numberOfApplications=",        "1"));
    int initialNumberOfApplications = Integer.parseInt(U.getOption(pArgs,  "--initialNumberOfApplications=", "1"));
    int     maxNumberOfApplications = Integer.parseInt(U.getOption(pArgs,  "--maxNumberOfApplications=",     "1"));
    int               maxSystemLoad = Integer.parseInt(U.getOption(pArgs,  "--maxSystemLoad=",              "80"));
    int                   recycleAt = Integer.parseInt(U.getOption(pArgs,  "--recycleAt=",               "10000"));
    int                     seconds = Integer.parseInt(U.getOption(pArgs,  "--seconds=",                    "30"));
    String                off_level =                  U.getOption(pArgs, "--off_level=", "false");
    String pickUpWhereYouLeftOffBefore =               U.getOption(pArgs,  "--pickUpWhereYouLeftOffBefore=", "true");
    
    
    
    String version = "2025-06_06_segmentFilter_flag_added";
    String args[] = {
        
        "--threaded=" + threaded,
        "--productionInputDir=" + productionInputDir,
        "--inputDir=" + inputDir,
        "--outputDir=" + outputDir,
        "--inputFormat=" + inputFormatType,
        "--outputFormat=" + outputFormatType,
        "--payloadAnnotationSetName=" + payloadAnnotationSetName,
        "--ipirAnnotationSetName=" + ipirAnnotationSetName,
        "--comcogAnnotationSetName=" + comcogAnnotationSetName,
        "--modellAnnotationSetName=" + modelAnnotationSetName,
        "--gateViewAnnotationSetNameFilterOutList=" + gateViewAnnotationSetNameFilterOutList_,
        
        "--mfoCategories=" + mentalFunctioningOntologyCategories,
        
        "--detailedSummary=" + detailedSummary,
        "--INFACT_MODE=" + INFACT_MODE,
        "--segmentRelevantFilter=" + segmentRelevantFilter,
        "--usePronounsInD750=" + usePronounsInD750,
        "--inFACTFileNamingConvention=" + inFACTFileNamingConvention,
        
        "--logDir=" + logDir,
        "--printToLog=" + printToLog,
        "--silenceGLogMsgs=" + silenceGLogMsgs,
        "--profilePerformanceLogging=" + profilePerformanceLogging,
        "--printToConsole=" + printToConsole,
        
        
        "--setMetaDataHeader=" + setMetaDataHeader,
        
        "--typeDescriptor=" + typeDescriptor,
        "--LRPRN=" + LRPRNFile,
       
        "--gateHome="     + gateHome,
        "--localTerminologyFiles=" + localTerminologyFiles,
       
        "--processingListings=" + processingListings,
        "--highlightMentions=" + highlightMentions,
        "--columnDelimiter=" + columnDelimiter,
        
        "--fileSizeThreshold=" + fileSizeThreshold, 
        "--memorySizeThresholdMb=" + memorySizeThresholdMb,
         "--sentenceSizeThreshold=" + sentenceSizeThreshold,
         

         // -----------------------------------
         // Pipeline scale-out parameter..
           "--numberOfApplications="        + numberOfApplications,
           "--metric="                      + metric,
           "--numberToProcess="             + numberToProcess,
           "--initialNumberOfApplications=" + initialNumberOfApplications,
           "--maxNumberOfApplications="     + maxNumberOfApplications,
           "--maxSystemLoad="               + maxSystemLoad,
           "--recycleAt="                   + recycleAt,
           "--seconds="                     + seconds,
           "--off_level="                   + off_level,
           "--pickUpWhereYouLeftOffBefore=" + pickUpWhereYouLeftOffBefore,
       
         
   
        "--version="                   + version
         
    };

      
  if ( Use.version(pArgs, args ) )
    Runtime.getRuntime().exit(0);
  
  if (     Use.usageAndExitIfHelp( "InFACTSubCategoriesApplication", pArgs, args ) )
    Runtime.getRuntime().exit(0);
    
    return args;

  }  // End Method setArgs() -----------------------
  

  // ------------------------------------------------
  // Global Variables
  // ------------------------------------------------
  
  
     
    

} // End SyntaticApplication Class -------------------------------
