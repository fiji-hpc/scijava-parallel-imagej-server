
package cz.it4i.parallel.imagej.server;

import java.util.Set;

import org.scijava.parallel.ParallelizationParadigm;

public abstract class AbstractParallelizationParadigmConverter<O> implements
	ParallelizationParadigmConverter<O>,
	Cloneable
{

	private Set<Class<? extends ParallelizationParadigm>> supportedParadigms;

	private Class<O> supportedParameterType;

	public AbstractParallelizationParadigmConverter(
		Set<Class<? extends ParallelizationParadigm>> supportedParadigms,
		Class<O> supportedType)
	{
		this.supportedParadigms = supportedParadigms;
		this.supportedParameterType = supportedType;
	}

	@Override
	public Set<Class<? extends ParallelizationParadigm>> getSupportedParadigms() {
		return supportedParadigms;
	}

	@Override
	public Class<O> getOutputType() {
		return supportedParameterType;
	}


}
