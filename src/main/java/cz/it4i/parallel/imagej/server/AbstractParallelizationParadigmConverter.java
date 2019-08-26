
package cz.it4i.parallel.imagej.server;

import java.util.Set;

import org.scijava.parallel.ParallelizationParadigm;

abstract class AbstractParallelizationParadigmConverter implements
	ParallelizationParadigmConverter, Cloneable
{

	private Set<Class<? extends ParallelizationParadigm>> supportedParadigms;

	private Class<?> supportedParameterType;

	public AbstractParallelizationParadigmConverter(
		Set<Class<? extends ParallelizationParadigm>> supportedParadigms,
		Class<?> supportedType)
	{
		this.supportedParadigms = supportedParadigms;
		this.supportedParameterType = supportedType;
	}

	@Override
	public Set<Class<? extends ParallelizationParadigm>> getSupportedParadigms() {
		return supportedParadigms;
	}

	@Override
	public Class<?> getOutputType() {
		return supportedParameterType;
	}


}
