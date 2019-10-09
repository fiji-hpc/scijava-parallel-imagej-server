
package cz.it4i.parallel.imagej.server.runners;

import java.util.Arrays;
import java.util.List;

import cz.it4i.parallel.paradigm_managers.LocalImageJRunner;

public class LocalImageJServerRunner extends LocalImageJRunner {

	static final List<String> IMAGEJ_SERVER_PARAMETERS = Arrays.asList(
		"-Dimagej.legacy.modernOnlyCommands=true", "--", "--ij2", "--headless",
		"--server");

	static final int PORT_NUMBER = 8080;

	public LocalImageJServerRunner() {
		super(IMAGEJ_SERVER_PARAMETERS, Wait4ImageJServer::doIt, PORT_NUMBER);
	}

}
