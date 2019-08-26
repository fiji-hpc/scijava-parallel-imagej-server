
package cz.it4i.parallel.imagej.server;

import net.imagej.Dataset;

import org.scijava.plugin.Plugin;

@Plugin(type = ParallelizationParadigmConverter.class)
public class DatasetImageJServerConverter extends
	AbstractDatasetImageJServerConverter<Dataset>
{
	public DatasetImageJServerConverter() {
		super(Dataset.class);
	}

	@Override
	protected AbstractDatasetImageJServerConverter<Dataset>
		createEmptyInstance()
	{
		return new DatasetImageJServerConverter();
	}
}
