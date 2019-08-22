
package cz.it4i.parallel.imagej.server;

import java.util.Map;

public class DefaultParameterProcessor extends AbstractParameterProcessor {

	private Map<Class<?>, ParallelizationParadigmConverter<?>> converters;

	public DefaultParameterProcessor(ParameterTypeProvider typeProvider,
		String commandName, RemoteDataHandler servingWorker,
		Map<Class<?>, ParallelizationParadigmConverter<?>> converters)
	{
		super(typeProvider, commandName, servingWorker);
		this.converters = converters;
	}

	@Override
	protected <T> ParallelizationParadigmConverter<T> construcConverter(
		Class<T> expectedType,
		RemoteDataHandler servingWorker)
	{
		@SuppressWarnings("unchecked")
		ParallelizationParadigmConverter<T> result =
			(ParallelizationParadigmConverter<T>) converters.get(expectedType);
		if (result != null) {
			return result.cloneForWorker(servingWorker);
		}
		return null;
	}

}
