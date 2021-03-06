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
package au.gov.ga.worldwind.common.render.fastshape;

/**
 * Helper class that stores a vertex index and it's distance from the eye, used
 * for sorting triangles.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
public class IndexAndDistance implements Comparable<IndexAndDistance>
{
	public final double distance;
	public final int index;

	public IndexAndDistance(double distance, int index)
	{
		this.distance = distance;
		this.index = index;
	}

	@Override
	public int compareTo(IndexAndDistance o)
	{
		return -Double.compare(distance, o.distance);
	}
}
