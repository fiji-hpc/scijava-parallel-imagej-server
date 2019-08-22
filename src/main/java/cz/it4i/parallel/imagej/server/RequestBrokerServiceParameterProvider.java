package cz.it4i.parallel.imagej.server;

import static cz.it4i.parallel.persistence.PersistentParallelizationParadigmImpl.INPUTS;
import static cz.it4i.parallel.persistence.PersistentParallelizationParadigmImpl.MODULE_ID;
import static cz.it4i.parallel.persistence.PersistentParallelizationParadigmImpl.REQUEST_IDS;
import static cz.it4i.parallel.persistence.PersistentParallelizationParadigmImpl.RESULTS;

import com.google.common.collect.Streams;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.SciJavaService;
import org.scijava.service.Service;

import cz.it4i.parallel.plugins.RequestBrokerServiceCallCommand;
import cz.it4i.parallel.plugins.RequestBrokerServiceGetResultCommand;
import cz.it4i.parallel.plugins.RequestBrokerServicePurgeCommand;
import lombok.RequiredArgsConstructor;


class RequestBrokerServiceParameterProvider
{

	public interface RequestID2ProcessorHolder extends SciJavaService {
	
		public ParameterProcessor index(Object obj,
			ParameterProcessor parameterProcessor);
	
		public ParameterProcessor search(Object obj);
	}

	@Plugin(type = Service.class)
	public static class DefaultRequestID2ProcessorHolder extends AbstractService
		implements RequestID2ProcessorHolder
	{

		private final Map<Object, ParameterProcessor> requestID2processor =
			Collections.synchronizedMap(new WeakHashMap<>());

		@Override
		public ParameterProcessor index(Object obj,
			ParameterProcessor parameterProcessor)
		{
			return requestID2processor.put(obj, parameterProcessor);
		}

		@Override
		public ParameterProcessor search(Object obj) {
			return requestID2processor.get(obj);
		}
	}

	private final ParameterTypeProvider typeProvider;


	private final Map<Class<?>, ParallelizationParadigmConverter<?>> converters;


	@Parameter
	private RequestID2ProcessorHolder requestHolder;

	private RemoteDataHandler defaultWorker;

	private RemoteDataManager remoteDataManager;

	public RequestBrokerServiceParameterProvider(
		ParameterTypeProvider typeProvider,
		Map<Class<?>, ParallelizationParadigmConverter<?>> mappers,
		RemoteDataHandler defaultWorker)
	{
		this.typeProvider = typeProvider;
		this.converters = mappers;
		this.defaultWorker = defaultWorker;
		this.remoteDataManager = new RemoteDataManager();
	}

	public ParameterProcessor constructProvider(String command,
		RemoteDataHandler pw)
	{
		if (command.equals(
			RequestBrokerServiceCallCommand.class.getCanonicalName()))
		{
			return new PCallCommandProcessor(pw);
		}
		else if (command.equals(
			RequestBrokerServiceGetResultCommand.class.getCanonicalName()))
		{
			return new PGetResultProcessor();
		}
		else if (command.equals(RequestBrokerServicePurgeCommand.class
			.getCanonicalName()))
		{
			return new PPurgeProcessor();
		}
		return null;
	}

	private Serializable setIDForRemoteDataHandler(RemoteDataHandler handler,
		Serializable id)
	{
		remoteDataManager.setID(handler, id);
		return id;
	}

	@RequiredArgsConstructor
	private class PCallCommandProcessor implements ParameterProcessor {
	
		private final RemoteDataHandler worker;
		private List<DefaultParameterProcessor> processors;

		private List<RemoteDataHandler> workers;
		private String commandName;
	
		@Override
		public Map<String, Object> processInputs(final Map<String, Object> inputs) {
	
			commandName = inputs.get(MODULE_ID)
				.toString();
			@SuppressWarnings({ "unchecked" })
			List<Map<String, Object>> processing = (List<Map<String, Object>>) inputs
				.get(INPUTS);
			workers = IntStream.range(0, processing.size()).mapToObj(
				x -> remoteDataManager.createProxyDataHandler(worker)).collect(
					Collectors.toList());

			processors = workers.stream().map(w -> new DefaultParameterProcessor(
				typeProvider, commandName, w, converters)).collect(Collectors.toList());
	
			List<Map<String, Object>> processed = Streams.zip(processing.stream(),
				processors.stream(), (input, processor) -> processor.processInputs(
					input))
				.collect(Collectors.toList());
			Map<String, Object> result = new HashMap<>(inputs);
			result.put(INPUTS, processed);
			return result;
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Object> processOutputs(Map<String, Object> outputs) {
			List<Serializable> ids = (List<Serializable>) outputs.get(REQUEST_IDS);
			Streams.zip(Streams.zip(workers.stream(), ids.stream(),
				RequestBrokerServiceParameterProvider.this::setIDForRemoteDataHandler),
				processors.stream(), requestHolder::index).count();
			outputs.put(REQUEST_IDS, ids.stream().map(
				id -> new InternalCompletableFutureID(commandName, id)).collect(
					Collectors.toList()));
			return outputs;
		}
	
		@Override
		public void close() {
			// do nothing
		}
	}

	private class PGetResultProcessor implements ParameterProcessor {
	
		private List<InternalCompletableFutureID> requestIDs;

		@SuppressWarnings("unchecked")
		@Override
		public Map<String, Object> processInputs(Map<String, Object> inputs) {
			requestIDs = (List<InternalCompletableFutureID>) inputs.get(REQUEST_IDS);
			inputs.put(REQUEST_IDS, requestIDs.stream().map(pId -> pId.id).collect(
				Collectors.toList()));
			return inputs;
		}
	
		@Override
		public Map<String, Object> processOutputs(Map<String, Object> outputs) {
			Stream<ParameterProcessor> processors = requestIDs.stream().map(
				this::getParameterProcessor);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> results = (List<Map<String, Object>>) outputs
				.get(RESULTS);
			outputs = new HashMap<>(outputs);
			results = Streams.zip(processors, results.stream(), this::processOutput)
				.collect(Collectors.toList());
			outputs.put(RESULTS, results);
			return outputs;
		}
	
		private ParameterProcessor getParameterProcessor(InternalCompletableFutureID id) {
			ParameterProcessor result = requestHolder.search(id.id);
			if (result == null) {
					result = new DefaultParameterProcessor(typeProvider,
					id.getCommandName(), remoteDataManager.createProxyDataHandler(
						defaultWorker, id.id),
						converters);
			}
			return result;
		}

		private Map<String, Object> processOutput(ParameterProcessor proc,
			Map<String, Object> output)
		{
			return proc.processOutputs(output);
		}

		@Override
		public void close() {
			// do nothing
		}
	
	}

	private class PPurgeProcessor implements ParameterProcessor {

		@Override
		public Map<String, Object> processInputs(final Map<String, Object> inputs) {
			@SuppressWarnings("unchecked")
			List<InternalCompletableFutureID> ids = (List<InternalCompletableFutureID>) inputs.get(
				REQUEST_IDS);
			inputs.put(REQUEST_IDS, ids.stream().map(pId -> pId.id).map(
				this::purgeObjects)
				.collect(Collectors
				.toList()));
			return inputs;
		}

		@Override
		public Map<String, Object> processOutputs(Map<String, Object> outputs) {
			return outputs;
		}

		@Override
		public void close() {
			// do nothing
		}

		Serializable purgeObjects(Serializable id) {
			remoteDataManager.purged(id);
			return id;
		}
	}
}
