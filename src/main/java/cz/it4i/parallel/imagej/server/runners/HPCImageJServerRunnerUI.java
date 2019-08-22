
package cz.it4i.parallel.imagej.server.runners;

import cz.it4i.parallel.ui.HPCImageJRunnerWithUI;

public class HPCImageJServerRunnerUI extends HPCImageJRunnerWithUI {

	public HPCImageJServerRunnerUI()
	{
		super(LocalImageJServerRunner.FSTPRPC_SERVER_PARAMETERS,
			Wait4ImageJServer::doIt, 8080);
	}

	@Override
	protected String getServerName() {
		return "ImageJ Server";
	}

}
