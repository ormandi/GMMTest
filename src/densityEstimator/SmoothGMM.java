package densityEstimator;

import java.util.Arrays;
import java.util.Map;

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
  
  /**
   * This is a mandatory constructor which is used through the reflection based initalization in the simulator.  
   */
  public SmoothGMM(int numberOfComponents) {
    super(numberOfComponents);
    prev_w = new double[k];
    Arrays.fill(prev_w, 0.0);
    prev_m = new double[k];
    for (int i = 0; i < k; i ++) {
      prev_m[i] = ((double)i) - ((double)k)/2.0;
    }
    prev_v = new double[k];
    Arrays.fill(prev_v, 0.0);
  }
  
  @Override
  public Map<String,String> parseParameters(String params) {
    Map<String,String> p = super.parseParameters(params);    
    if (p.containsKey("alpha")) {
      alpha = Double.parseDouble(p.get("alpha"));
    } else {
      throw new RuntimeException("Parameter alpha=someDouble is mandatory for mixture model " + getClass().getCanonicalName() + ", please specify it at the command line!");
    }
    return p;
  }
  
  @Override
  public void update(double x) {
    super.update(x);
    
    // M-step updte occured
    if (c == 0) {
      for (int i = 0; i < k; i ++) {
        // weights
        w[i] = (1.0 - alpha) * prev_w[i] + alpha * w[i];
        prev_w[i] = w[i];
        m[i] = (1.0 - alpha) * prev_m[i] + alpha * m[i];
        prev_m[i] = m[i];
        v[i] = (1.0 - alpha) * prev_v[i] + alpha * v[i];
        prev_v[i] = v[i];
      }
    }
  }

}
