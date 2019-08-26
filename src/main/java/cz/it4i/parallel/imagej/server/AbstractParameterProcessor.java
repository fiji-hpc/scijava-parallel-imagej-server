
package cz.it4i.parallel.imagej.server;

import static cz.it4i.parallel.InternalExceptionRoutines.runWithExceptionHandling;
import static cz.it4i.parallel.InternalExceptionRoutines.supplyWithExceptionHandling;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

abstract class AbstractParameterProcessor implements ParameterProcessor {

	private Map<String, PAppliedConversion> appliedConversions = new HashMap<>();

	private String commandName;

	private ParameterTypeProvider typeProvider;

	private RemoteDataHandler worker;

	public AbstractParameterProcessor(ParameterTypeProvider typeProvider,
		String commandName, RemoteDataHandler worker)
	{
		this.commandName = commandName;
		this.typeProvider = typeProvider;
		this.worker = worker;

	}

	@Override
	public Map<String, Object> processInputs(Map<String, Object> inputs) {
		Map<String, Object> result = new HashMap<>();
		for (Entry<String, Object> entry : inputs.entrySet()) {
			result.put(entry.getKey(), doInputConversion(entry));
		}
		return result;
	}

	@Override
	public Map<String, Object> processOutputs(Map<String, Object> inputs) {
		Map<String, Object> result = new HashMap<>();
		for (Entry<String, Object> entry : inputs.entrySet()) {
			result.put(entry.getKey(), doOutputConversion(entry));
		}
		return result;
	}

	@Override
	public void close() {
		for (PAppliedConversion conversion : appliedConversions.values()) {
			if (conversion.conversion instanceof Closeable) {
				Closeable closeable = (Closeable) conversion.conversion;
				runWithExceptionHandling(closeable::close);
			}
		}
	}

	protected String getCommandName() {
		return commandName;
	}

	protected abstract <T> ParallelizationParadigmConverter<T> construcConverter(
		Class<T> expectedType, RemoteDataHandler servingWorker);

	private String getParameterTypeName(String parameter) {
		return typeProvider.provideParameterTypeName(commandName, parameter);
	}

	private Object doInputConversion(Entry<String, Object> parameter) {
		String typeName = getParameterTypeName(parameter.getKey());
		ParallelizationParadigmConverter<?> convertor = construcConverter(
			supplyWithExceptionHandling(() -> Class.forName(
				typeName)), worker);
		Object value = parameter.getValue();
		if (convertor != null) {
			appliedConversions.put(parameter.getKey(), new PAppliedConversion(value
				.getClass(), convertor));
			value = convertor.convert(value, Object.class);

		}
		return value;
	}

	private Object doOutputConversion(Entry<String, Object> parameter) {
		Object value = parameter.getValue();
		PAppliedConversion appliedConversion = appliedConversions.get(parameter
			.getKey());
		if (appliedConversion != null) {
			value = appliedConversion.conversion.convert(value,
				appliedConversion.srcType);
		}
		else {
			String typeName = getParameterTypeName(parameter.getKey());
			Class<?> type = supplyWithExceptionHandling(() -> Class.forName(
				typeName));
			ParallelizationParadigmConverter<?> convertor = construcConverter(type,
				worker);
			if (convertor != null) {
				value = convertor.convert(value, type);
			}
		}
		return value;
	}

	private class PAppliedConversion {

		final Class<?> srcType;
		final ParallelizationParadigmConverter<?> conversion;

		public PAppliedConversion(Class<?> srctype,
			ParallelizationParadigmConverter<?> conversion)
		{
			super();
			this.srcType = srctype;
			this.conversion = conversion;
		}

	}
}
