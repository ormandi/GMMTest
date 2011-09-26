package densityEstimator;

import java.util.Arrays;

/**
 * In this class we perform an averaging through the parameters.
 * 
 * @author Róbert Ormándi
 */
public class SmoothGMM extends BatchBasedOnlineGMM {
  private static final long serialVersionUID = 3747450428580792659L;
  
  protected double alpha;
  
  protected double[] prev_w = null;  // previous component (w)eights
  protected double[] prev_m = null;  // previous component (m)eans
  protected double[] prev_v = null;  // previous component (v)ariances
  
  public SmoothGMM(int numberOfComponents, int bufferLength, double alpha) {
    super(numberOfComponents, bufferLength);
    this.alpha = alpha;
    prev_w = new double[k];
    Arrays.fill(prev_w, 0.0);
    prev_m = new double[k];
    for (int i = 0; i < k; i ++) {
      prev_m[i] = ((double)i) - ((double)k)/2.0;
    }
    prev_v = new double[k];
    Arrays.fill(prev_v, 0.0);
  }
  
  public void update(double x) {
    super.update(x);
    
    // M-step updte occured
    if (c == 0) {
      for (int i = 0; i < k; i ++) {
        // weights
        prev_w[i] = w[i] = (1.0 - alpha) * prev_w[i] + alpha * w[i];
        prev_m[i] = m[i] = (1.0 - alpha) * prev_m[i] + alpha * m[i];
        prev_v[i] = v[i] = (1.0 - alpha) * prev_v[i] + alpha * v[i];
      }
    }
  }

}
