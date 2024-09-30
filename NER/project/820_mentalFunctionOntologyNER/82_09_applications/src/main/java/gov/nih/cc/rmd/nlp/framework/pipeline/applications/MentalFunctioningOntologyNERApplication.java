// ------------------------------------------------------------
/**
 * MentalFunctioningOntologyNERApplication kicks off the MentalFunctionOntologyNER pipeline on a corpus
 * This application runs from annotated gate files and creates vtt and xmi output files.
 * 
 * @author  G.o.d.
 * @created 2022.03.28
 * 
 */

package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.MentalFunctionOntologyNERAnnotator;
import gov.nih.cc.rmd.nlp.framework.pipeline.MentalFunctioningOntologyNERPipeline;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;


public class MentalFunctioningOntologyNERApplication {

  
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
     application.setPipelines(MentalFunctioningOntologyNERPipeline.class, args);
    

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
      MentalFunctioningOntologyNERPipeline   pipeline = new MentalFunctioningOntologyNERPipeline( args );
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

    String    inputDir  = U.getOption(pArgs, "--inputDir=", "/82_01_data/examples");
    String    outputDir = U.getOption(pArgs, "--outputDir=", inputDir + "_MentalFunctionOntologyNER_" + dateStamp);
    String       logDir = U.getOption(pArgs, "--logDir=",   outputDir + "/logs" ); 
    String   printToLog = U.getOption(pArgs, "--printToLog=", "true");
    String   printToConsole = U.getOption(pArgs, "--printToConsole=", "true");
    String silenceGLogMsgs = U.getOption(pArgs, "--silenceGLogMsgs=",  "true");
    String INFACT_MODE= U.getOption(pArgs, "--INFACT_MODE=", "true" );
    
    String inputFormatType  = U.getOption(pArgs, "--inputFormat=", FrameworkReaders.TEXT_READER_);
    String outputFormatType = U.getOption(pArgs, "--outputFormat=", 
        FrameworkBaselineApplication.XMI_WRITER_ + ":" +
        FrameworkBaselineApplication.GATE_WRITER_ + ":" +
        FrameworkBaselineApplication.TEXT_WRITER_ 
        );
   
    

    String typeDescriptor = U.getOption( pArgs, "--typeDescriptor=", "gov.nih.cc.rmd.mentalFunctionOntologyNER.SubCategoriesModel"); //  "gov.nih.cc.rmd.FrameworkAndCtakes");
    
    String profilePerformanceLogging = U.getOption(pArgs,  "--profilePerformanceLogging=", "false");
    String setMetaDataHeader = U.getOption(pArgs,  "--setMetaDataHeader=", "false");
    String gateHome = U.getOption(pArgs, "--gateHome=",  "C:/Program Files (x86)/GATE_Developer_8.5.1");
   
   
    
    String localTerminologyFiles = U.getOption(pArgs, "--localTerminologyFiles=",  MentalFunctionOntologyNERAnnotator.MentalFunctioningOntologyNERLexica );
    
   
    String inFACTFileNamingConvention =  U.getOption(pArgs, "--inFACTFileNamingConvention=", "true" );
   
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
    
    String        fileSizeThreshold = U.getOption(pArgs,  "--fileSizeThreshold=",  "80769" ); // 1508000" ); //"1508000");  // largest cdr: 1,574,050 cpd: 1,526,853 
    String    memorySizeThresholdMb = U.getOption(pArgs, "--memorySizeThresholdMb=", "100");  
    String sentenceSizeThreshold = U.getOption( pArgs,  "--sentenceSizeThreshold=",  "100");
    String  threaded = U.getOption(pArgs, "--threaded=", "true"); 
    
    
    String version = "2024-09-26.1";
    
    
  
    String args[] = {
        "--threaded=" + threaded,
        "--inputDir=" + inputDir,
        "--outputDir=" + outputDir,
        "--inputFormat=" + inputFormatType,
        "--outputFormat=" + outputFormatType,
        
        "--logDir=" + logDir,
        "--printToLog=" + printToLog,
        "--profilePerformanceLogging=" + profilePerformanceLogging,
        "--printToConsole=" + printToConsole,
        
        
        "--setMetaDataHeader=" + setMetaDataHeader,
        
        "--typeDescriptor=" + typeDescriptor,
       
        "--gateHome="     + gateHome,
        "--localTerminologyFiles=" + localTerminologyFiles,
        
        "--INFACT_MODE=" + INFACT_MODE,
        
        "--logDir=" + logDir,
        "--printToLog=" + printToLog,
        "--silenceGLogMsgs=" + silenceGLogMsgs,
        "--inFACTFileNamingConvention=" + inFACTFileNamingConvention,

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
          "--sentenceSizeThreshold=" + sentenceSizeThreshold, 
          "--fileSizeThreshold=" + fileSizeThreshold,
          "--memorySizeThresholdMb="       + memorySizeThresholdMb,
        
        "--version="                   + version
         
    };

      
  if ( Use.version(pArgs, args ) )
    Runtime.getRuntime().exit(0);
  
  if (     Use.usageAndExitIfHelp( "MentalFunctioningOntologyNERApplication", pArgs, args ) )
    Runtime.getRuntime().exit(0);
    
    return args;

  }  // End Method setArgs() -----------------------
  

  // ------------------------------------------------
  // Global Variables
  // ------------------------------------------------

} // End SyntaticApplication Class -------------------------------
