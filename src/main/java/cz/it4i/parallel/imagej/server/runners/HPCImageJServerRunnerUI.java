
package cz.it4i.parallel.imagej.server.runners;

import cz.it4i.parallel.ui.HPCImageJRunnerWithUI;

public class HPCImageJServerRunnerUI extends HPCImageJRunnerWithUI {

	public HPCImageJServerRunnerUI()
	{
		super(LocalImageJServerRunner.IMAGEJ_SERVER_PARAMETERS,
			Wait4ImageJServer::doIt, LocalImageJServerRunner.PORT_NUMBER);
	}

	@Override
	protected String getServerName() {
		return "ImageJ Server";
	}

}
