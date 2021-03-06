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
package au.gov.ga.worldwind.common.layers.delegate;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.util.TileKey;

/**
 * Instances of {@link ITileFactoryDelegate} are used when creating new
 * TextureTiles, and also for transforming the {@link TileKey}s when
 * loading/saving TextureTiles from/to the texture memory cache.
 * 
 * @param <TILE>
 *            Tile type to create from this factory.
 * @param <BOUNDS>
 *            Bounds type expected by the tile's constructor (eg, {@link Sector}
 *            ).
 * @param <LEVEL>
 *            Level type expected by the tile's constructor.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
public interface ITileFactoryDelegate<TILE extends IDelegatorTile, BOUNDS, LEVEL> extends IDelegate
{
	/**
	 * Create a new {@link TextureTile} with the given parameters.
	 * 
	 * @param bounds
	 * @param level
	 * @param row
	 * @param col
	 * @return New {@link TextureTile}
	 */
	TILE createTextureTile(BOUNDS bounds, LEVEL level, int row, int col);

	/**
	 * Transform a {@link TileKey}. This is useful when multiple layers have the
	 * same data cache name and therefore similar tiles will have duplicate
	 * {@link TileKey}s. Transforming the {@link TileKey} will ensure that they
	 * are different.
	 * 
	 * It would be nicer for the {@link TextureTile}s generated by the
	 * createTextureTile() function to override the getTileKey() function, but
	 * unfortunately it is a final method.
	 * 
	 * @param tileKey
	 * @return Transformed {@link TileKey}
	 */
	TileKey transformTileKey(TileKey tileKey);
}
