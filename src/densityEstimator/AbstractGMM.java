package densityEstimator;

import java.util.Arrays;

/**
 * This is an abstract implementation of interface MixtureModel which
 * implements all method specialized to Gaussian components except 
 * the update method. Consequently the concrete GMM learning is
 * implemented in the subclasses of this one.
 * 
 * @author Róbert Ormándi
 */
public abstract class AbstractGMM implements MixtureModel {
  private static final long serialVersionUID = -7538021417587298397L;
  
  private final static double ONE_PER_SQRT2PI = 1.0/Math.sqrt(2.0 *  Math.PI);
  private final static double DEFAULT_VARIANCE = 0.1;
  
  protected int k = 0;
  protected double[] w = null;  // component (w)eights
  protected double[] m = null;  // component (m)eans
  protected double[] v = null;  // component (v)ariances
  
  public abstract void update(double x);
  
  /**
   * This method sets the number of components and since the model is changed
   * initialzes the components.<br/>
   * <b>Take into account the method erases the model parameters permanently!</b>
   * 
   * @param k New number of components
   */
  private void init(int k) {
    // update the number of components
    this.k = k;
    
    // initialize component weights uniformly
    w = new double[k];
    Arrays.fill(w, 1.0/((double)k));
    
    // initialize parameters
    m = new double[k];
    for (int i = 0; i < k; i ++) {
      m[i] = ((double)i) - ((double)k)/2.0;
    }    
    v = new double[k];
    Arrays.fill(v, AbstractGMM.DEFAULT_VARIANCE);
  }
  
  /**
   * This method computes the weighted probability density function value at x in case of <i>i</i>th component.
   * The <i>i</i> component index has to be between 0 (inclusive) and numberOfComponenets (exclusive) otherwise
   * the method return NaN.
   *  
   * @param i index of component between 0 (inclusive) and numberOfComponenets (exclusive)
   * @param x point in which the wighted probability density function (pdf) will be evaluated 
   * @return weighted pdf function value or NaN (iff. the index <i>i</i> is out of bounds.) 
   */
  protected double computeComponentDensity(int i, double x) {
    if (0 <= i && i < k) {
      if (w[i] > 0.0) {
        double z = (x - m[i]) / v[i];
        z *= z;
        return w[i] * AbstractGMM.ONE_PER_SQRT2PI * (1.0/v[i]) * Math.exp(-0.5 * z);
      } else {
        return 0.0;
      }
    }
    return Double.NaN;
  }
  
  @Override
  public double computeDensityValue(double x) {
    double density = 0.0;
    
    // compute the value of density function at x using the current parameters
    for (int i = 0; i < k; i ++) {
      density += computeComponentDensity(i, x);
    }
    
    return density;
  }

  @Override
  public double[] getComponentWeights() {
    return w;
  }

  @Override
  public double[] getComponentMeans() {
    return m;
  }

  @Override
  public double[] getComponentVariances() {
    return v;
  }

  @Override
  public int getNumberOfComponents() {
    return k;
  }

  @Override
  public void setNumberOfComponents(int num) {
    this.init(num);
  }
}
