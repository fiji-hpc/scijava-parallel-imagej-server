package cz.it4i.parallel.imagej.server;

import org.scijava.Context;
import org.scijava.parallel.ParadigmManager;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import cz.it4i.parallel.imagej.server.runners.LocalImageJServerRunner;
import cz.it4i.parallel.paradigm_managers.LocalImageJRunnerSettings;
import cz.it4i.parallel.paradigm_managers.MultipleHostsParadigmManagerUsingRunner;
import cz.it4i.parallel.paradigm_managers.ServerRunner;

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
