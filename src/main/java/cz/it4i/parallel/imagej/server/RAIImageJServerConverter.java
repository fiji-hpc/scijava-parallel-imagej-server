
package cz.it4i.parallel.imagej.server;

import net.imagej.Dataset;

import org.scijava.plugin.Plugin;

@Plugin(type = ParallelizationParadigmConverter.class)
public class RAIImageJServerConverter extends
	AbstractDatasetImageJServerConverter<Dataset>

{
	public RAIImageJServerConverter() {
		super(Dataset.class);
	}

	@Override
	protected AbstractDatasetImageJServerConverter<Dataset>
		createEmptyInstance()
	{
		return new RAIImageJServerConverter();
	}
}
