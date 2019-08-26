
package cz.it4i.parallel.imagej.server;

import net.imglib2.RandomAccessibleInterval;

import org.scijava.plugin.Plugin;

@Plugin(type = ParallelizationParadigmConverter.class)
public class RAIImageJServerConverter extends
	AbstractDatasetImageJServerConverter<RandomAccessibleInterval<?>>
{
	public RAIImageJServerConverter() {
		super(RandomAccessibleInterval.class);
	}

	@Override
	protected AbstractDatasetImageJServerConverter<RandomAccessibleInterval<?>>
		createEmptyInstance()
	{
		return new RAIImageJServerConverter();
	}
}
