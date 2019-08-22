package cz.it4i.parallel.imagej.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;

class RemoteDataManager {

	private Map<Serializable, Collection<Runnable>> id2commandMap =
		Collections
		.synchronizedMap(new HashMap<>());
	
	public RemoteDataHandler createProxyDataHandler(RemoteDataHandler handler) {
		return createProxyDataHandler(handler, null);
	}

	public RemoteDataHandler createProxyDataHandler(RemoteDataHandler handler,
		Serializable id)
	{
		return new PRemoteDataHandler(handler, id);
	}

	public void purged(Serializable id) {
		Collection<Runnable> purgeCommands = id2commandMap.remove(id);
		if (purgeCommands != null) {
			purgeCommands.forEach(Runnable::run);
		}
	}

	public void registerPurgeCommand(Serializable id,
		Runnable purgeCommand)
	{
		id2commandMap.computeIfAbsent(id, x -> new LinkedList<>()).add(
			purgeCommand);
	}

	public void setID(RemoteDataHandler handler, Serializable id) {
		if (handler instanceof PRemoteDataHandler) {
			PRemoteDataHandler pDataHandler = (PRemoteDataHandler) handler;
			pDataHandler.id = id;
		}
		else {
			throw new IllegalArgumentException("handler: " + handler +
				"was not created by RemoteDataManager");
		}
	}

	@AllArgsConstructor
	private class PRemoteDataHandler implements RemoteDataHandler {

		RemoteDataHandler handler;

		Serializable id;


		@Override
		public void exportData(Object dataset, String filePath,
			Consumer<InputStream> isConsumer) throws IOException
		{
			handler.exportData(dataset, filePath, isConsumer);

		}

		@Override
		public Object importData(String fileName,
			Consumer<OutputStream> osConsumer)
		{
			return handler.importData(fileName, osConsumer);
		}

		@Override
		public void deleteData(Object ds) {
			RemoteDataManager.this.registerPurgeCommand(id, () -> handler.deleteData(ds));
		}
	}
}
