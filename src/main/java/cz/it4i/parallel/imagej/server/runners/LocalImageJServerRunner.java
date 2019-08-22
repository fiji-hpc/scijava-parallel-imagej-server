
package cz.it4i.parallel.imagej.server.runners;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.it4i.parallel.runners.LocalImageJRunner;

public class LocalImageJServerRunner extends LocalImageJRunner {

	static final List<String> FSTPRPC_SERVER_PARAMETERS = Arrays.asList(
		"-Dimagej.legacy.modernOnlyCommands=true", "--", "--ij2", "--headless",
		"--fstrpcserver");

	public LocalImageJServerRunner() {
		super(FSTPRPC_SERVER_PARAMETERS, Wait4ImageJServer::doIt);
	}

	@Override
	public List<Integer> getPorts() {
		return Collections.singletonList(9090);
	}

	@Override
	protected List<String> getParameters() {
		return FSTPRPC_SERVER_PARAMETERS;
	}

}
