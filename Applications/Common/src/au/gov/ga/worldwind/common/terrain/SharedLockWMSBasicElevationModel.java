package au.gov.ga.worldwind.common.terrain;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.terrain.BasicElevationModel;
import gov.nasa.worldwind.util.BufferWrapper;
import gov.nasa.worldwind.util.DataConfigurationUtils;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.Tile;
import gov.nasa.worldwind.util.WWIO;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import org.w3c.dom.Element;

import au.gov.ga.worldwind.common.layers.tiled.image.delegate.FileLockSharer;

public class SharedLockWMSBasicElevationModel extends BoundedWMSBasicElevationModel
{
	protected final Object fileLock;

	public SharedLockWMSBasicElevationModel(Element domElement, AVList params)
	{
		super(domElement, params);

		fileLock = FileLockSharer.getLock(getLevels().getFirstLevel().getCacheName());
	}

	@Override
	protected void downloadElevations(Tile tile,
			BasicElevationModel.DownloadPostProcessor postProcessor)
	{
		if (postProcessor == null)
			postProcessor = new DownloadPostProcessor(tile, this);

		super.downloadElevations(tile, postProcessor);
	}

	/**
	 * Extension to superclass' DownloadPostProcessor which returns this class'
	 * fileLock instead of the superclass'.
	 * 
	 * @author Michael de Hoog
	 */
	protected static class DownloadPostProcessor extends BasicElevationModel.DownloadPostProcessor
	{
		private final SharedLockWMSBasicElevationModel em;

		public DownloadPostProcessor(Tile tile, SharedLockWMSBasicElevationModel em)
		{
			super(tile, em);
			this.em = em;
		}

		@Override
		protected Object getFileLock()
		{
			return em.fileLock;
		}
	}

	/* ***************************************************************************************************
	 * Below here is copied from BasicElevationModel, with some modifications to use the shared fileLock *
	 *************************************************************************************************** */

	@Override
	protected BufferWrapper readElevations(URL url) throws IOException
	{
		try
		{
			ByteBuffer byteBuffer;
			synchronized (this.fileLock)
			{
				byteBuffer = WWIO.readURLContentToBuffer(url);
			}

			// Setup parameters to instruct BufferWrapper on how to interpret the ByteBuffer.
			AVList bufferParams = new AVListImpl();
			bufferParams.setValue(AVKey.DATA_TYPE, this.getElevationDataPixelType());
			bufferParams.setValue(AVKey.BYTE_ORDER, this.getElevationDataByteOrder());
			return BufferWrapper.wrap(byteBuffer, bufferParams);
		}
		catch (java.io.IOException e)
		{
			Logging.logger().log(java.util.logging.Level.SEVERE,
					"ElevationModel.ExceptionReadingElevationFile", url.toString());
			throw e;
		}
	}

	@Override
	protected void writeConfigurationParams(AVList params, FileStore fileStore)
	{
		// Determine what the configuration file name should be based on the configuration parameters. Assume an XML
		// configuration document type, and append the XML file suffix.
		String fileName = DataConfigurationUtils.getDataConfigFilename(params, ".xml");
		if (fileName == null)
		{
			String message = Logging.getMessage("nullValue.FilePathIsNull");
			Logging.logger().severe(message);
			throw new WWRuntimeException(message);
		}

		// Check if this component needs to write a configuration file. This happens outside of the synchronized block
		// to improve multithreaded performance for the common case: the configuration file already exists, this just
		// need to check that it's there and return. If the file exists but is expired, do not remove it -  this
		// removes the file inside the synchronized block below.
		if (!this.needsConfigurationFile(fileStore, fileName, params, false))
			return;

		synchronized (this.fileLock)
		{
			// Check again if the component needs to write a configuration file, potentially removing any existing file
			// which has expired. This additional check is necessary because the file could have been created by
			// another thread while we were waiting for the lock.
			if (!this.needsConfigurationFile(fileStore, fileName, params, true))
				return;

			this.doWriteConfigurationParams(fileStore, fileName, params);
		}
	}
}