package densityEstimator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.TreeMap;


/**
 * This is a basic mixture model (MM) simulator. Generally it receives a mixture model as input, then
 * it generates samples from the MM and tries to estimate the parameteres of the given MM. During the
 * simulation in every <i>snapshot</i>th iteration it takes a picture about the current state of the
 * simulation. Finally it stitches together the pictures to a move.<br/>
 * This is a demo implementation, it is not optimized and has a lot of prerequirements like having bash,
 * gnuplot, convert commands installed on the target machine!
 * 
 * @author Róbert Ormándi
 */
public class MixtureModelSimulator {
  
  private static double generateNextDouble(double[] expW, double[] expNu, double[] expSigma, Random r){
    double rand = r.nextDouble();
    int index = 0;
    double lower = 0.0, upper = 0.0;
    for (int i = 0; i < expW.length && rand != 0.0; i++){
      upper += expW[i];
      if (lower < rand && rand <= upper){
        index = i;
        break;
      }
      lower += expW[i];
    }
    return r.nextGaussian()*expSigma[index] + expNu[index];
  }
  
  private static String asGnuplotFunction(String name, double[] w, double[] nu, double[] sigma) {
    StringBuffer ret = new StringBuffer().append(name);
    
    for (int i = 0; i < w.length && i < nu.length && i < sigma.length; i ++) {
      if (i == 0) {
        ret.append(" = ");
      } else {
        ret.append(" + ");
      }
      ret.append(w[i]).append(" * 1/sqrt(2*pi*").append(sigma[i]).append("**2) * ");
      ret.append("exp(-(x-(").append(nu[i]).append("))**2 / ( 2*").append(sigma[i]).append("**2))");
    }
    return ret.toString();
  }
  
  //private static String arrayToString() {
  //}
  
  public static void main(String[] args) throws Exception{
    final int numberOfComponents = 3;
    final double[] expW = new double[]{0.5, 0.2, 0.3};
    final double[] expNu = new double[]{-2.0, 1.0, 2.0};
    final double[] expSigma = new double[]{0.2, 1.0, 0.5};
    final String outputFileName = "simulation.gif";
    final String tmpDirName = "./gmmSimualtion/";
    final int snapshotStepSize = 1000;
    final int numOfGeneratedSamples = 20 * snapshotStepSize;
    final long seed = 123456789;
    final boolean isClear = true;
    
    // create gmm
    final MixtureModel gmm = new BatchBasedOnlineGMM(numberOfComponents, snapshotStepSize);
    
    // init tmp directory
    File tmpDir = new File(tmpDirName);
    if (isClear) {
      if (tmpDir.exists()) {
        System.err.println("The given temporary directory (" + tmpDirName + ") exists! Please, give another one.");
        return;
      } else {
        if (!tmpDir.mkdir()) {
          System.err.println("Temporary directory cannot be created (" + tmpDirName + ")!");
          return;
        }
      }
    }
    
    // define some other variables and script templates
    final double precision = 100.0;
    final Random r = new Random(seed);
    final String gptTemplate = 
      "#!/usr/bin/gnuplot\n\n" +
      "set term png large nocrop enhanced font \"/usr/share/fonts/truetype/arial.ttf\" 14 size 800,600\n" +
      "set output \"${picture_file}\"\n" + 
      "set title \"GMM Simulation, Iteration=${i}\"\n" +
      "set xrange [-4:6]\n" + 
      "set yrange [0:1]\n\n" +
      "${component_functions}" + 
      "${pdf_current}\n" +
      MixtureModelSimulator.asGnuplotFunction("pdf_expected(x)", expW, expNu, expSigma) +
      "\n\nplot \\\n  '${data_file}' using 1:($2 * " + precision + ") w l title 'generated data'," + 
      "\\\n  pdf_current(x) lw 2 title 'current pdf'," +
      "${component_function_calls}" + 
      "\\\n  pdf_expected(x) title 'expected pdf';\n";
    final String generatorScriptName = "generateFig.sh";
    final String generatorScript = 
      "#!/bin/bash\n\n" +
      "echo `pwd`;\n" +
      "for f in `ls *.gpt`; do ./${f} 2>/dev/null; done;\n" + 
      "convert -delay 5 -loop 0 `ls *.png` ";
    PrintWriter fileOut = null;
    
    // perform simulation
    String out = "";
    TreeMap<Double, Integer> histogram = new TreeMap<Double, Integer>();
    for (int i = 0; i < numOfGeneratedSamples + 1; i++){
      if ((i % snapshotStepSize == 0 && i != 0) || i == 1){
        if (out.length() > 0) {
          for (int c = 0; c < out.length(); c ++) {
            System.out.print('\b');
          }
        }
        out = "Generating snapshot " + (i/snapshotStepSize) + " out of " + (numOfGeneratedSamples/snapshotStepSize);
        System.out.print(out);
        
        // create simulation snapshot
        String baseName = String.format("out_%08d", i/snapshotStepSize);
        File txtFile = new File(tmpDir, baseName + ".txt");
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
        for (double k : histogram.keySet()){
          fileOut.println(k + "\t" + (histogram.get(k) / (double)i));
        }
        fileOut.close();
        
        // create gnuplot script for the snapshot
        File pngFile = new File(baseName + ".png");
        File gptFile = new File(tmpDir, baseName + ".gpt");
        fileOut = new PrintWriter(new BufferedWriter(new FileWriter(gptFile)));
        String gptScript = gptTemplate.replaceFirst("\\$\\{picture_file\\}", pngFile.getName());
        gptScript = gptScript.replaceFirst("\\$\\{data_file\\}", txtFile.getName());
        gptScript = gptScript.replaceFirst("\\$\\{i\\}", new Integer(i).toString());
        
        // get current values from the GMM model
        double[] currentW = gmm.getComponentWeights();
        double[] currentNu = gmm.getComponentMeans();
        double[] currentSigma = gmm.getComponentVariances();
        //
        StringBuffer componentFunctions = new StringBuffer();
        StringBuffer componentFunctionCalls = new StringBuffer();
        for (int componentId = 0; componentId < currentW.length; componentId ++) {
          componentFunctions.append(MixtureModelSimulator.asGnuplotFunction("pdf_current_component_" + componentId + "(x)", new double[]{currentW[componentId]}, new double[]{currentNu[componentId]}, new double[]{currentSigma[componentId]})).append("\n");
          componentFunctionCalls.append("\\\\\n  pdf_current_component_" + componentId + "(x) title 'component " + componentId + " pdf',");
        }
        gptScript = gptScript.replaceFirst("\\$\\{component_functions\\}", componentFunctions.toString());
        gptScript = gptScript.replaceFirst("\\$\\{component_function_calls\\}", componentFunctionCalls.toString());
        //
        gptScript = gptScript.replaceFirst("\\$\\{pdf_current\\}", MixtureModelSimulator.asGnuplotFunction("pdf_current(x)", currentW, currentNu, currentSigma));
        fileOut.println(gptScript);
        
        // make gpt file runnable
        gptFile.setExecutable(true);
        
        // close gpt file
        fileOut.close();
        fileOut = null;
      }
      
      // generate the next sample
      double x = generateNextDouble(expW, expNu, expSigma, r);
      
      // update histogram
      double h = Math.floor(x * precision) / precision;
      if (histogram.containsKey(h)){
        histogram.put(h, histogram.get(h) + 1);
      } else {
        histogram.put(h, 1);
      }
      
      // update gmm
      gmm.update(x);
    }
    System.out.println(". Done!");
    
    System.out.print("Generating final result");
    // create generator script and final output
    File outputFile = new File(outputFileName);
    // writing generator script
    File scriptFile = new File(tmpDir, generatorScriptName);
    fileOut = new PrintWriter(new BufferedWriter(new FileWriter(scriptFile)));
    fileOut.println(generatorScript + outputFile.getName());
    scriptFile.setExecutable(true);
    fileOut.close();    
    // calling generator script and showing its error stream
    ProcessBuilder pb = new ProcessBuilder(scriptFile.getCanonicalPath());
    pb.directory(tmpDir);
    Process p = pb.start();
    BufferedReader generatorError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    String line = generatorError.readLine();
    while (line != null) {
      System.err.println(line);
      line = generatorError.readLine();
    }
    
    
    // remove temporary files
    if (isClear) {
      File[] files = tmpDir.listFiles();
      boolean isSuccess = true;
      for (int fIdx = 0; fIdx < files.length; fIdx ++) {
        if (files[fIdx].getName().equals(outputFile.getName())) {
          // move the output file out of the temp directory
          isSuccess &= files[fIdx].renameTo(outputFile);
        } else {
          // delete temp file
          isSuccess &= files[fIdx].delete();
        }
        if (!isSuccess) {
          // error while trying to remove file => stop
          break;
        }
      }
      if (isSuccess) {
        // remove temporary directory as well (which should be empty)
        isSuccess &= tmpDir.delete();
      }
      if (!isSuccess) {
        System.err.println("\nErrors occured during deleting/moving files! Unneccessary files can be remained in the temporary directory! They have to be removed manually.");
      }
    }
    System.out.println(". Done!");
    
    // show final parameter set
    System.out.println("Final parameters: ");
    System.out.println("  Component weights: " + Arrays.toString(gmm.getComponentWeights()));
    System.out.println("  Means:             " + Arrays.toString(gmm.getComponentMeans()));
    System.out.println("  Variances:         " + Arrays.toString(gmm.getComponentVariances()));
  }
}
