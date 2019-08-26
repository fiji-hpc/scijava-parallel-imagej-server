
package cz.it4i.parallel.imagej.server;

import net.imglib2.RandomAccessibleInterval;

import org.scijava.plugin.Plugin;

@SuppressWarnings("rawtypes")
@Plugin(type = ParallelizationParadigmConverter.class)
public class DatasetImageJServerConverter extends
	AbstractDatasetImageJServerConverter<RandomAccessibleInterval>

{
	public DatasetImageJServerConverter() {
		super(RandomAccessibleInterval.class);
	}

	@Override
	protected AbstractDatasetImageJServerConverter<RandomAccessibleInterval>
		createEmptyInstance()
	{
		return new DatasetImageJServerConverter();
	}
}
