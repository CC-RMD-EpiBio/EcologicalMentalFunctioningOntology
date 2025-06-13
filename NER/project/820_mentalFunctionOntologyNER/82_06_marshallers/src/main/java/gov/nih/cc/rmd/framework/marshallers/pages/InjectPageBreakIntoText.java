// =================================================
/**
 * InjectPageBreakIntoText.java  Summary [TBD]
 *
 * @author     Guy Divita
 * @created    Feb 17, 2023
 * 
*/
// =================================================
package gov.nih.cc.rmd.framework.marshallers.pages;

import java.io.PrintWriter;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;


public class InjectPageBreakIntoText {

  private static final String PAGE_BREAK_TAG = "[PAGE_BREAK]";
  private static final char PAGE_BREAK_CHAR = new Character((char) 12);
  private static final String PAGE_BREAK_STRING =  Character.toString(PAGE_BREAK_CHAR) ;

  

  // =================================================
  /**
   * main 
   * 
   * @param pArgs
  */
  // =================================================
  public static void main(String[] pArgs) {
   
    String inputFileName = null;
    String outputFileName = null;
    try {
      String[] args = setArgs(pArgs);
      inputFileName = U.getOption(args,  "--inputFileName=", "./some/file.txt"); 
      outputFileName = U.getOption(args,  "--outputFileName=", "./some/file.txt"); 
      
      String content = U.readFile(inputFileName );
      String newContent = content.replace(PAGE_BREAK_TAG, PAGE_BREAK_TAG + PAGE_BREAK_STRING );
      
      PrintWriter out = new PrintWriter( outputFileName );
      out.print(newContent);
      out.close();
      
      GLog.println("Doghn");
      
      
      
    } catch (Exception e) {
      e.printStackTrace();
      GLog.error_println(" Issue trying to inject page breaks into text " + inputFileName + " " + e.toString());
    }
    
  } // end Method main() ----------------------------

//------------------------------------------
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

   String    inputFileName  = U.getOption(pArgs, "--inputFileName=", "./82_01_data/multiPageExamples/templateWithPages.txt");
   String    outputFileName = U.getOption(pArgs, "--outputFileName=", inputFileName + "_" + dateStamp + ".txt");
  
   
   String version = "2023-02-17.1";
   String args[] = {
       
       "--inputFileName=" + inputFileName,
       "--outputFileName=" + outputFileName,
       "--version="                   + version
        
   };

   /*  
 if ( Use.version(pArgs, args ) )
   Runtime.getRuntime().exit(0);
 
 if (     Use.usageAndExitIfHelp( "InjectPageBreakIntoText", pArgs, args ) )
   Runtime.getRuntime().exit(0);
   */
   
   return args;

 }  // End Method setArgs() -----------------------
 

 // ------------------------------------------------
 // Global Variables
 // ------------------------------------------------
 
}
