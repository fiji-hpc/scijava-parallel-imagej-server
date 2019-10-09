
package cz.it4i.parallel.imagej.server.runners;

import java.util.Collections;
import java.util.List;

import net.imagej.server.ImageJServerService;

import org.scijava.parallel.Status;
import org.scijava.plugin.Parameter;

import cz.it4i.parallel.paradigm_managers.RunnerSettings;
import cz.it4i.parallel.paradigm_managers.ServerRunner;

public class InProcessImageJServerRunner implements
	ServerRunner<RunnerSettings>
{

	@Parameter
	private ImageJServerService server;

	private Status status = Status.NON_ACTIVE;


	public boolean isInitialized() {
		return server != null;
	}

	@Override
	public InProcessImageJServerRunner init(RunnerSettings settings) {
		return this;
	}

	@Override
	public void start() {
		server.start();
		getPorts().parallelStream().forEach(Wait4ImageJServer::doIt);
		status = Status.ACTIVE;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public List<Integer> getPorts() {
		return Collections.singletonList(8080);
	}

	@Override
	public List<Integer> getNCores() {
		return Collections.singletonList(Runtime.getRuntime()
			.availableProcessors());
	}

	@Override
	public void letShutdownOnClose() {
		// it is always shutdowned during close
	}

	@Override
	public void close() {
		shutdown();
	}

	private void shutdown() {
		status = Status.NON_ACTIVE;
		server.dispose();
	}

}
