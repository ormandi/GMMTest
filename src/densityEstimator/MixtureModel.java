package densityEstimator;

import java.io.Serializable;
import java.util.Map;

/**
 * This is a general representation of any mixture model
 * which cab be learnt online.<br/>
 * The online learning behavior is
 * implemented via update method which improves the current model
 * based on the next observation. Moreover the interface supports
 * the evaluation of the probability denstity functioin directly
 * through the computeDensityValue function and makes the 
 * parameters available via get methods.
 * 
 * @author Róbert Ormándi
 */
public interface MixtureModel extends Serializable {
  /**
   * It updates the parameters of the current model based on
   * the sample x. This implements the online learning capability of
   * the mixture model.
   *  
   * @param x next observation which is used to improve the model
   */
  public void update(double x);
  
  /**
   * The method computes the value of probability density function (pdf) at x
   * based on the current parameter values.
   * 
   * @param x value in which the pdf function will be evaluated
   * @return value of pdf defined by the current parameter values. (The return value 
   * allways greater than 0, but it is NOT bounded by one i.e. it can be arbitrary large.)
   */
  public double computeDensityValue(double x);
  
  /**
   * It returns the component weights of the model as a double array.
   * 
   * @return component weights
   */
  public double[] getComponentWeights();
  
  /**
   * The method returns the mean parameters of the components as an array of doubles
   * 
   * @return mean values
   */
  public double[] getComponentMeans();
  
  /**
   * This method gives back the variance parameters of the components. Be sure, it returns the sigma values 
   * instead of sigma squares!
   * 
   * @return variance values
   */
  public double[] getComponentVariances();
  
  /**
   * It returns the number of components.
   * 
   * @return the number of components in the current mixture model
   */
  public int getNumberOfComponents();
  
  /**
   * This method sets the number of components and - it is very important - initializes the model,
   * since we cannot adapt -at the moment, but it is an interesting question - an existing model to a
   * new component number.<br/>
   * Take into account that this method will erase the current model parameters!!!
   *   
   * @param num nex component number
   */
  public void setNumberOfComponents(int num);
  
  /**
   * This method parses and sets the parameters of the Mixture Model from a string which contains comma separeted 
   * param, value pairs in form parmName=value.
   * This should be called before the first update!
   * 
   * @param params 
   * @return it returns the parsed parameterName-value pairs as a string map. This solution helps to avoid performing more 
   * paramter parsing process e.g. in the inheritance hierarchy. 
   * 
   */
  public Map<String,String> parseParameters(String params);
}
