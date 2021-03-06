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
package au.gov.ga.worldwind.tiler.ribbon.definition;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import au.gov.ga.worldwind.tiler.ribbon.RibbonTilingContext;
import au.gov.ga.worldwind.tiler.util.Util;

/**
 * Creates layer definition files for the tiled ribbons
 * <p/>
 * Customised element creators can be registered using the {@link #registerElementCreator} methods to override default behaviour
 */
public class LayerDefinitionCreator 
{
	private PrintStream stream;
	
	private Map<String, LayerDefinitionElementCreator> elementCreators = new HashMap<String, LayerDefinitionElementCreator>();
	
	public LayerDefinitionCreator()
	{
		registerElementCreator(new DefaultDisplayNameElementCreator());
		registerElementCreator(new DefaultServiceElementCreator());
		registerElementCreator(new DefaultDelegateElementCreator());
		registerElementCreator(new DefaultLastUpdateElementCreator());
		registerElementCreator(new DefaultDatasetNameElementCreator());
		registerElementCreator(new DefaultDataCacheElementCreator());
		registerElementCreator(new DefaultImageFormatElementCreator());
		registerElementCreator(new DefaultAvailableFormatsElementCreator());
		registerElementCreator(new DefaultFormatSuffixElementCreator());
		registerElementCreator(new DefaultNumLevelsElementCreator());
		registerElementCreator(new DefaultTileSizeElementCreator());
		registerElementCreator(new DefaultFullSizeElementCreator());
		registerElementCreator(new DefaultPathElementCreator());
		registerElementCreator(new DefaultCurtainTopElementCreator());
		registerElementCreator(new DefaultCurtainBottomElementCreator());
		registerElementCreator(new DefaultFollowTerrainElementCreator());
		registerElementCreator(new DefaultSubsegmentsElementCreator());
		registerElementCreator(new DefaultUseTransparentElementCreator());
		registerElementCreator(new DefaultForceLevelZeroLoadsElementCreator());
		registerElementCreator(new DefaultRetainLevelZeroElementCreator());
		registerElementCreator(new DefaultTextureFormatElementCreator());
		registerElementCreator(new DefaultUseMipMapsElementCreator());
		registerElementCreator(new DefaultDetailHintElementCreator());
	}
	
	public void createDefinition(RibbonTilingContext context) throws Exception
	{
		stream = new PrintStream(context.getLayerDefinitionFile());
	
		replaceElementCreatorsFromContext(context);
		
		writePreamble();
		writeLayerElement(context);
	}

	private void replaceElementCreatorsFromContext(RibbonTilingContext context)
	{
		for (String creatorClass : context.getElementCreatorClasses())
		{
			registerElementCreator(creatorClass);
		}
	}

	private void writePreamble() {
		stream.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}
	
	private void writeLayerElement(RibbonTilingContext context) {
		stream.println("<Layer version=\"1\" layerType=\"CurtainImageLayer\">");
		writeElement("DisplayName", context);
		writeElement("Service", context);
		writeElement("Delegates", context);
		writeElement("LastUpdate", context);
		writeElement("DatasetName", context);
		writeElement("DataCacheName", context);
		writeElement("ImageFormat", context);
		writeElement("AvailableImageFormats", context);
		writeElement("FormatSuffix", context);
		writeElement("NumLevels", context);
		writeElement("TileSize", context);
		writeElement("FullSize", context);
		writeElement("Path", context);
		writeElement("CurtainTop", context);
		writeElement("CurtainBottom", context);
		writeElement("FollowTerrain", context);
		writeElement("Subsegments", context);
		writeElement("UseTransparentTextures", context);
		writeElement("ForceLevelZeroLoads", context);
		writeElement("RetainLevelZeroTiles", context);
		writeElement("TextureFormat", context);
		writeElement("UseMipMaps", context);
		writeElement("DetailHint", context);
		stream.println("</Layer>");
	}
	
	private void writeElement(String elementName, RibbonTilingContext context)
	{
		LayerDefinitionElementCreator elementCreator = elementCreators.get(elementName);
		if (elementCreator == null)
		{
			return;
		}
		stream.println(elementCreator.getElementString(1, context));
	}
	
	public void registerElementCreator(LayerDefinitionElementCreator elementCreator)
	{
		if (elementCreator == null)
		{
			return;
		}
		
		elementCreators.put(elementCreator.getElementName(), elementCreator);
	}
	
	public void registerElementCreator(Class<? extends LayerDefinitionElementCreator> clazz)
	{
		if (clazz == null)
		{
			return;
		}
		
		try
		{
			LayerDefinitionElementCreator instance = clazz.newInstance();
			registerElementCreator(instance);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void registerElementCreator(String className)
	{
		if (Util.isBlank(className))
		{
			return;
		}
		
		try
		{
			@SuppressWarnings("unchecked")
			Class<? extends LayerDefinitionElementCreator> clazz = (Class<? extends LayerDefinitionElementCreator>) Class.forName(className);
			registerElementCreator(clazz);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
