package densityEstimator;

/**
 * This class implements a naive online GMM learning mechanism using the
 * idea of Expectation-Maximization (EM). The key difference between this
 * online implementation and the original EM concept is that we use different
 * samples in the different EM iterations.<br/>
 * More precisely the learning mechanism works like this:
 * <ul> 
 *   <li>The model takes a random walk through the observations.</li>
 *   <li>It estimates the P(C_i|x_j) probabilities for each sample and component and computes some necessary 
 *   statistics for M-step updates (It requires O(K) space where K is the number of components).<br/> 
 *   This can be considered as an E-step computed on B different samples from the random walk.</li>
 *   <li>If the above defined statistics are computed all of the B samples i.e. the model saw exactly B samples, 
 *   an M-step is perfomed based on the these statistics.</li>
 * </ul>
 * The original EM algorithm does the same process except it uses the same B samples in each EM iteration.
 * 
 * @author Róbert Ormándi
 */
public class BatchBasedOnlineGMM extends AbstractGMM {
  private static final long serialVersionUID = 9035582797449162031L;
  
  private static final int COMPUTED_MOMENTS = 3;
  private static final double EPS = 1.0E-12;
  
  private int bufferLength;       // number of samples from which gamma statistics are collected
  private double[] pcx;           // bufferLength x numOfComps array in which the statistics are collected (probabilities)
  private double[][] pcx_moments; // C x numOfComps array in which some other statistics are collected (moments)
  private int c = 0;              // sample counter
  
  
  public BatchBasedOnlineGMM(int numberOfComponents, int bufferLength) {
    setNumberOfComponents(numberOfComponents);
    this.bufferLength = bufferLength;
    pcx = new double[numberOfComponents];
    pcx_moments = new double[BatchBasedOnlineGMM.COMPUTED_MOMENTS][numberOfComponents];
  }
  
  @Override
  public void update(double x) {
    // compute the P(C_i|x) = P(C_i|x_c) values for each component
    double sumProbs = 0.0;
    for (int i = 0; i < k; i ++) {
      pcx[i] = computeComponentDensity(i, x);
      sumProbs += pcx[i];
    }
    // normalize values
    //System.out.print("\nx=" + x + ", components: ");
    for (int i = 0; i < k; i ++) {
      pcx[i] /= sumProbs;
      pcx_moments[0][i] += pcx[i];
      pcx_moments[0][i] = (pcx_moments[0][i] < BatchBasedOnlineGMM.EPS) ? BatchBasedOnlineGMM.EPS : pcx_moments[0][i]; // numeric issues
      pcx_moments[1][i] += (pcx[i] * x);
      pcx_moments[1][i] = (Math.abs(pcx_moments[1][i]) < BatchBasedOnlineGMM.EPS) ? Math.signum(pcx_moments[1][i])*BatchBasedOnlineGMM.EPS : pcx_moments[1][i]; // numeric issues
      pcx_moments[2][i] += (pcx[i] * x * x);
      pcx_moments[2][i] = (pcx_moments[2][i] < BatchBasedOnlineGMM.EPS) ? BatchBasedOnlineGMM.EPS : pcx_moments[2][i]; // numeric issues
      //System.out.print("\n  " + i + ": g("+i+","+x+")="+pcx[c][i]+", sum0: g("+i+","+x+")="+pcx_moments[0][i]+", sum1: g("+i+","+x+")="+pcx_moments[1][i]+", sum2: g("+i+","+x+")="+pcx_moments[2][i]);
    }
    
    // increment the sample counter
    c++;    
    if (c == bufferLength) {
      // all P(C_i|x_j) are computed so perform an EM step and clear statistics
      
      //System.out.print("\nM-step, components: ");
      // perform M-step
      for (int i = 0; i < k; i ++) {
        m[i] = pcx_moments[1][i] / pcx_moments[0][i];
        v[i] = Math.sqrt(pcx_moments[2][i] / pcx_moments[0][i] - m[i] * m[i]);
        w[i] = pcx_moments[0][i] / ((double)bufferLength);
        w[i] = (w[i] < BatchBasedOnlineGMM.EPS) ? 0.0 : w[i]; // numeric issue
        //System.out.print("\n  " + i + ": m=" + m[i] + ", v=" + v[i] + ", w=" + w[i]);
      }
      
      
      // set the sample counter to zero
      c = 0;
      // clear buffer
      for (int i = 0; i < k; i ++) {
        pcx_moments[0][i] = 0.0;
        pcx_moments[1][i] = 0.0;
        pcx_moments[2][i] = 0.0;
      }
      //System.out.println();
    }
  }
}
