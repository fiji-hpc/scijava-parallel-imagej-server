package cz.it4i.parallel.imagej.server;

import org.scijava.Context;
import org.scijava.parallel.ParadigmManager;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import cz.it4i.parallel.imagej.server.runners.LocalImageJServerRunner;
import cz.it4i.parallel.runners.LocalImageJRunnerSettings;
import cz.it4i.parallel.runners.MultipleHostsParadigmManagerUsingRunner;
import cz.it4i.parallel.runners.ServerRunner;

@Plugin(type = ParadigmManager.class)
public class LocalImageJServerParadigmProfileManager extends
	MultipleHostsParadigmManagerUsingRunner<ImageJServerParadigm, LocalImageJRunnerSettings>
{
	@Parameter
	private Context context;

	@Override
	public Class<ImageJServerParadigm> getSupportedParadigmType() {
		return ImageJServerParadigm.class;
	}
	
	@Override
	protected Class<? extends ServerRunner<LocalImageJRunnerSettings>>
		getTypeOfRunner()
	{
		return LocalImageJServerRunner.class;
	}

}
