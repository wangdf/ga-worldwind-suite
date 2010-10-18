package au.gov.ga.worldwind.animator.layers.camerapath;

import gov.nasa.worldwind.geom.Position;
import au.gov.ga.worldwind.animator.animation.Animation;
import au.gov.ga.worldwind.animator.animation.AnimationContext;
import au.gov.ga.worldwind.animator.animation.KeyFrame;

/**
 * An {@link AbstractCameraPositionPath} that draws the current animation's eye position along with nodes representing key frames.
 */
class EyePositionPath extends AbstractCameraPositionPath
{
	
	public EyePositionPath(Animation animation)
	{
		super(animation);
	}

	@Override
	protected Position getPathPositionAtFrame(AnimationContext context, int frame)
	{
		return getAnimation().getCamera().getEyePositionAtFrame(context, frame);
	}

	@Override
	protected boolean isPathFrame(KeyFrame keyFrame)
	{
		return keyFrame.hasValueForParameter(getAnimation().getCamera().getEyeLat()) || 
			   keyFrame.hasValueForParameter(getAnimation().getCamera().getEyeLon()) || 
			   keyFrame.hasValueForParameter(getAnimation().getCamera().getEyeElevation());
	}
	
}