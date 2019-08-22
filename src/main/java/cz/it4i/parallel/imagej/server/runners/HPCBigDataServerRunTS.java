package cz.it4i.parallel.imagej.server.runners;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.it4i.parallel.runners.ClusterJobLauncher.Job;
import cz.it4i.parallel.runners.HPCImageJRunner;

public class HPCBigDataServerRunTS {

	private static final Logger log = LoggerFactory.getLogger(
		HPCBigDataServerRunTS.class);

	private HPCImageJRunner runner;
	private String pathToServer;
	private String pathToDataSet;

	public HPCBigDataServerRunTS(HPCImageJRunner runner,
		String pathToServer, String pathToDataSet)
	{
		super();
		this.runner = runner;
		this.pathToServer = pathToServer;
		this.pathToDataSet = pathToDataSet;
	}

	public String run() {
		Job job = runner.getJob();
		int bdsPort = Math.max(Collections.max(runner.getPorts()), 8081) + 1;
		job.createTunnel(bdsPort, "localhost", bdsPort);
		job.runCommandOnNode(0, " -L " + bdsPort + ":localhost:" + bdsPort +
			" " + pathToServer +
			" " + pathToDataSet + " " +
			bdsPort).whenComplete((lines, exc) -> {
				if (exc != null) {
					log.error(exc.getMessage(), exc);
				}
				else {
					log.debug(String.join("\n", lines));
				}
			});
		WaitForHTTPServerRunTS.create("http://localhost:" + bdsPort + "/json/")
			.run();
		log.debug(
			"BigDataServer is running on node {} and is available local port {}", job
				.getNodes().get(0), bdsPort);
		return "http://localhost:" + bdsPort + "/data";
	}


}
