/**
 * 
 */
package au.gov.ga.worldwind.animator.animation;

import gov.nasa.worldwind.Restorable;

import java.io.Serializable;
import java.util.Collection;

import au.gov.ga.worldwind.animator.animation.parameter.Parameter;
import au.gov.ga.worldwind.animator.util.Nameable;

/**
 * An interface for objects that can be animated within an {@link Animation}
 *
 * @author James Navin (james.navin@ga.gov.au)
 */
public interface Animatable extends Serializable, Restorable, Nameable
{
	/**
	 * Apply this object's behaviour/changes to the 'world' for the given frame
	 * 
	 * @param animationContext The context in which the animation is executing
	 * @param frame The current frame of the animation
	 */
	void apply(AnimationContext animationContext, int frame);
	
	/**
	 * @return The collection of all parameters associated with this animatable object
	 */
	Collection<Parameter> getParameters();
	
	/**
	 * @return The collection of all <em>enabled</em> parameters associated with this animatable object
	 */
	Collection<Parameter> getEnabledParameters();
}