// ------------------------------------------------------------
/**
 * An api annotating subcategory elements in documents
 * The input is the contents of an interlingua file 
 * and the output is the contents of the processed interlingua file
 *  
 * 
 * @author Divita
 * June 11, 2024
 * 
 */
// -------------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.pipeline.applications.inFACT;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.uima.analysis_engine.AnalysisEngine;

import gov.nih.cc.rmd.nlp.framework.annotator.mentalFunctionOntologyNER.MentalFunctionOntologyNERAnnotator;
import gov.nih.cc.rmd.nlp.framework.marshallers.frameworkObject.FrameworkObject;
import gov.nih.cc.rmd.nlp.framework.pipeline.InFACTSubCategorizationBySentenceComcogMachinePipeline;

import gov.nih.cc.rmd.nlp.framework.pipeline.applications.ApplicationAPI;
import gov.nih.cc.rmd.nlp.framework.pipeline.applications.FrameworkBaselineApplication;
import gov.nih.cc.rmd.nlp.framework.pipeline.applications.FrameworkReadersAux;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;


// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class InFACT_SubCategoriesAPI extends ApplicationAPI {

  
//=======================================================
 /**
  * Constructor InFACT_SubCategoriesAPI 
  *
  */
  // ====================================================
 public InFACT_SubCategoriesAPI() throws Exception {
 
   String dummyArgs[] = new String[1];
   dummyArgs[0] = "--silenceGLogMsgs=true";
   init( dummyArgs );
   
  } // end Constructor() ----------------------------

 //=======================================================
 /**
  * Constructor InFACT_SubCategoriesAPI 
  * 
  * @param pArgs 
  *
  */
 // =====================================================
     public InFACT_SubCategoriesAPI( String[] pArgs) throws Exception {
       
       init( pArgs);
       
      } // end Constructor() ----------------------------

 
  // =======================================================
  /**
   * init 
   *
   * @param pArgs
   */
  // =======================================================
  public void init(String[] pArgs) throws Exception {

    
    try {
      // --------------------
      // Read in any command line arguments, and add to them needed ones
      // The precedence is command line args come first, followed by those
      // set by the setArgs method, followed by the defaults set here.
      // --------------------
    
      this.args  = setArgs(pArgs );
      GLog.setPrintToLog(false);  
      GLog.setPrintToConsole(false);  
      GLog.setSilenceGLogMsgs( true) ;
     
    
      
      // GLog.setLogDir( U.getOption(args,  "--logDir=", "./logs" ));
      
      // -------------------
      // Create an engine with a pipeline, attach it to the application
      // -------------------
      InFACTSubCategorizationBySentenceComcogMachinePipeline aPipeline = new InFACTSubCategorizationBySentenceComcogMachinePipeline(args );
    
      AnalysisEngine ae = aPipeline.getAnalysisEngine( );
      super.initializeApplication( this.args, ae);
    
      
    
      
      
      
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initializing Line " + e.toString();
      System.err.println( msg);
      throw new Exception( msg);
      
    }
      
  } // end Method initialize() -----------------------------
  
  
  
  // ------------------------------------------
  /**
   * main
   *    See the setArgs method to see what specific command line 
   *    arguments should be passed in here.
   *
   * @param pArgs
   */
  // ------------------------------------------
  public static void main(String[] pArgs) {
    

    try {
      String[] args = InFACTSubCategoriesApplication.setArgs( pArgs);
      String   platformBasePath  = U.getOption(pArgs, "--platformBasePath=", "/media/dgx2_nfs1/nlp/divitag/softwareRepos");
      GLog.set(args);
      InFACT_SubCategoriesAPI anApplication = new InFACT_SubCategoriesAPI( args);
      String payloadAnnotationSetName = "comcog:ipir";
    
      // ------------------
      // gather and process the cas's
      // -----------------
   

      // loop thru input files from the command line, process and
      // put the results into a file specified from the commandline
    
      
    
      String[] stdInRows = {"inputFilename="  + platformBasePath + "/comcog_flask/data/input/testFile1.xml|" +
                            "outputFilename=" + platformBasePath + "/comcog_flask/data/output/testFile1.xml",
                            "finished"
         };
      
     
     /*
      BufferedReader in = new BufferedReader( new InputStreamReader( System.in ));
      String row = null;
      System.err.print("input : ");
      while ( (row = in.readLine() )!= null && row.trim().length() > 0  ) {
      */
        for ( String row: stdInRows ) {
      
        if ( row.toLowerCase().contains("finished"))  break;
        
        String[] inputOutputPaths = getInputAndOutputPaths( row );
        
        
        String inputContent = U.readFile( inputOutputPaths[0] );
        String interlinguaOutputContent = anApplication.process_from_stream_to_stream( inputContent, null, payloadAnnotationSetName);
       
        System.out.println("--------------------");
        System.out.println("-- Processed file contents --");
        System.out.println("--------------------");
        System.out.print(interlinguaOutputContent);
        
         
        System.out.println();
        System.out.println("completed" );
        System.err.println("--------------------");
        System.err.print("input : ");
      }
    
      System.err.println("Finished");
      
     
      anApplication.cleanup();
    
    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue processing the files " + e.toString();
      System.err.println(msg);
    }
    
  } // end Method main() --------------------------------------

  // =================================================
  /**
   * cleanup closes down any open stuff
   * 
  */
  // =================================================
  public void cleanup() {
    
    try {
    
    
      this.closeInputStream();
      this.closeOutputStream();
      this.destroy();
    
    } catch (Exception e) {
      
    }
  }

  // =================================================
  /**
   * processGateInputFileToGateOutputFile [TBD] summary
   * 
   * @param pInputFilePath
   * @param pOutputFilePath
  */
  // =================================================
public final void processInterlingua_InputFile_to_InterlinguaOutputFile(String pInputFilePath, String pOutputFilePath) {
    // TODO Auto-generated method stub
    
  } // end Method processGateInputFileToGateOutputFile

  // =================================================
  /**
   * process_from_file_to_file 
   * 
   * @param pRow
  */
  // =================================================
  public final void process_from_file_to_file(String pRow) {
   
  } // end Method process_from_file_to_file() --------
    
 
  // =================================================
  /**
   * process_from_file_to_file 
   * 
   * @param pInterlinguaContent
   * @param pModelName                           (this is ignored)
   * @param pInterlinguaPayloadAnnotationSetName (I have to check to see if I can change this on the fly)
  */
  // =================================================
  public final String process_from_stream_to_stream(String pInterlinguaContent, String pModelName, String pPayloadAnnotationSetName) {
   
    String returnVal = null;
    

    returnVal = this.application.processGATEStringToGATEString( this.args, pInterlinguaContent);
    
    return returnVal;
  } // end Method process_from_file_to_file() --------
    
  
//=================================================
 /**
  * getInputAndOutputPaths
  * @param pStdIn      in the format of inputFileName=/some/path/to/input|outputFileName=/some/path/to/output/fileName
  * @return String[]  [0] = input path
  *                   [1] = output path  
  * 
 */
 // =================================================
 public static String[] getInputAndOutputPaths( String pStdIn ) {
   
   String[] returnVal = new String[2];
   
   if (pStdIn != null && !pStdIn.isEmpty() && pStdIn.contains( "|") ) {
     String cols[] = U.split( pStdIn);
     String cols2[] = U. split( cols[0], "=");
     returnVal[0] = cols2[1].trim();
     if ( cols[1] != null && cols[1].trim().length() > 0 ) {
       String cols3[] = U.split( cols[1], "=");
       returnVal[1] = cols3[1].trim();
      }
   } else {
     GLog.error_println("malformed standard input, it should be in the form of inputFileName=/path/to/inputFileName.xml|outputFileName=/path/to/outputFileName.xml");
   }
   
   
   return returnVal;
 }
   
  

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
    // Input and Output

    String   platformBasePath  = U.getOption(pArgs, "--platformBasePath=", "/home/share/projects/softwareRepos");
    String   printToLog = U.getOption(pArgs, "--printToLog=", "false");
    String silenceGLogMsgs = U.getOption(pArgs, "--silenceGLogMsgs=",  "true");
    String   printToConsole = U.getOption(pArgs, "--printToConsole=", "false");
    String  detailedSummary = U.getOption(pArgs, "--detailedSummary=", "false");
    String INFACT_MODE= U.getOption(pArgs, "--INFACT_MODE=", "true" );
    String inputFormatType  = U.getOption(pArgs, "--inputFormat=", FrameworkReadersAux.GATE_READER_);
    String outputFormatType = U.getOption(pArgs, "--outputFormat=",  FrameworkBaselineApplication.GATE_WRITER_   );
    
    String payloadAnnotationSetName = U.getOption(pArgs, "--payloadAnnotationSetName=", "ipir_sentence_model:comcog_sentence_model");
    String  comcogAnnotationSetName = U.getOption(pArgs, "--comcogAnnotationSetName=", "comcog_subcategory_model");
    String    ipirAnnotationSetName = U.getOption(pArgs, "--ipirAnnotationSetName=", "ipir_subcategory_model");
    String  modelAnnotationSetName = U.getOption(pArgs,  "--modelAnnotationSetName=", comcogAnnotationSetName);
   
    String typeDescriptor = U.getOption( pArgs, "--typeDescriptor=", "gov.nih.cc.rmd.mentalFunctionOntologyNER.SubCategoriesModel"); //  "gov.nih.cc.rmd.FrameworkAndCtakes");
    
    String profilePerformanceLogging = U.getOption(pArgs,  "--profilePerformanceLogging=", "false");
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
    String inFACTFileNamingConvention =  U.getOption(pArgs, "--inFACTFileNamingConvention=", "true" );
    String        fileSizeThreshold = U.getOption(pArgs,  "--fileSizeThreshold=",  "80769" ); // 1508000" ); //"1508000");  // largest cdr: 1,574,050 cpd: 1,526,853 
    String    memorySizeThresholdMb = U.getOption(pArgs, "--memorySizeThresholdMb=", "100");  
    String sentenceSizeThreshold = U.getOption( pArgs,  "--sentenceSizeThreshold=",  "100");
  
    
    String version = "2024_06_12.9";
    String args[] = {
        "--platformBasePath=" + platformBasePath,
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
        "--inFACTFileNamingConvention=" + inFACTFileNamingConvention,
     
        "--sentenceSizeThreshold=" + sentenceSizeThreshold, 
        "--fileSizeThreshold=" + fileSizeThreshold,
        "--memorySizeThresholdMb="       + memorySizeThresholdMb,
        "--version="                   + version
         
    };

    return args;

  }  // End Method setArgs() -----------------------
  
  // ----------------
  // Global Variables
  // ----------------
  private String[] args = null;

  // End InFACT_SubCategoriesAPI Class -------------------------------
}
