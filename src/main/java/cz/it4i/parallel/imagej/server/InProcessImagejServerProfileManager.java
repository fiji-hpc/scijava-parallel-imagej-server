package cz.it4i.parallel.imagej.server;

import org.scijava.Context;
import org.scijava.parallel.ParadigmManager;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import cz.it4i.parallel.imagej.server.runners.InProcessImageJServerRunner;
import cz.it4i.parallel.paradigm_managers.MultipleHostsParadigmManagerUsingRunner;
import cz.it4i.parallel.paradigm_managers.ParadigmProfileWithSettings;
import cz.it4i.parallel.paradigm_managers.RunnerSettings;
import cz.it4i.parallel.paradigm_managers.ServerRunner;

@Plugin(type = ParadigmManager.class)
public class InProcessImagejServerProfileManager extends
	MultipleHostsParadigmManagerUsingRunner<ImageJServerParadigm, RunnerSettings>
{

	@Parameter
	private Context context;

	@Override
	public Class<ImageJServerParadigm> getSupportedParadigmType() {
		return ImageJServerParadigm.class;
	}


	@Override
	protected boolean editSettings(
		ParadigmProfileWithSettings<RunnerSettings> typedProfile)
	{
		// It does not have any setting so it should always return true:
		return true;
	}
	
	@Override
	protected Class<InProcessImageJServerRunner> getTypeOfRunner() {
		return InProcessImageJServerRunner.class;
	}

	@Override
	protected void initRunner(ServerRunner<?> runner) {
		if (runner instanceof InProcessImageJServerRunner) {
			InProcessImageJServerRunner typedRunner =
				(InProcessImageJServerRunner) runner;
			if (!typedRunner.isInitialized()) {
				context.inject(typedRunner);
			}
		}

	}

	@Override
	public String toString() {
		return "Inprocess ImageJ Server";
	}
}
