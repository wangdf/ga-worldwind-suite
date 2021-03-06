/*******************************************************************************
 * Copyright 2012 Geoscience Australia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package au.gov.ga.worldwind.animator.ui.tristate;

import java.util.ArrayList;
import java.util.List;

import au.gov.ga.worldwind.animator.ui.tristate.TriStateCheckBox.State;

/**
 * The default model implementation for the {@link TriStateCheckBox} component
 * 
 * @author James Navin (james.navin@ga.gov.au)
 */
public class DefaultTriStateCheckBoxModel implements TriStateCheckBoxModel
{

	/** The current state of the checkbox */
	private State currentState;
	
	/** The listeners to fire events to */
	private List<TriStateEventListener> listeners = new ArrayList<TriStateEventListener>();
	
	/**
	 * Initialises the state to {@link State#CHECKED}
	 */
	public DefaultTriStateCheckBoxModel()
	{
		currentState = State.CHECKED;
	}
	
	/**
	 * Initialises the state to the provided value
	 */
	public DefaultTriStateCheckBoxModel(State initialState)
	{
		currentState = initialState;
	}

	@Override
	public boolean isChecked()
	{
		return currentState == State.CHECKED;
	}
	
	@Override
	public boolean isUnchecked()
	{
		return currentState == State.UNCHECKED;
	}
	
	@Override
	public boolean isPartiallyChecked()
	{
		return currentState == State.PARTIAL;
	}
	
	@Override
	public void setCurrentState(State state)
	{
		currentState = state;
	}
	
	@Override
	public State getCurrentState()
	{
		return currentState;
	}
	
	@Override
	public void iterateState()
	{
		State oldState = this.currentState;
		State newState = oldState.nextState();
		
		this.currentState = newState;
		
		fireStateChangedEvent(oldState, newState);
	}

	/**
	 * Fires a state changed event to all registered listeners
	 */
	private void fireStateChangedEvent(State oldState, State newState)
	{
		for (int i = listeners.size() - 1; i >= 0; i--)
		{
			listeners.get(i).stateChanged(this, oldState, newState);
		}
	}
	
	/**
	 * Add an event listener to this model to detect state changes etc.
	 */
	public void addEventListener(TriStateEventListener listener)
	{
		if (listener == null)
		{
			return;
		}
		listeners.add(listener);
	}
	
	/**
	 * Remove the event listener from this model
	 */
	public void removeEventListener(TriStateEventListener listener)
	{
		if (listener == null)
		{
			return;
		}
		listeners.remove(listener);
	}
	
	
}
