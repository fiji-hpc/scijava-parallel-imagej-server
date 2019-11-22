package cz.it4i.parallel.imagej.server;


import org.scijava.Context;
import org.scijava.parallel.ParadigmManager;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import cz.it4i.parallel.imagej.server.runners.HPCImageJServerRunnerUI;
import cz.it4i.parallel.paradigm_managers.HPCImageJRunner;
import cz.it4i.parallel.paradigm_managers.HPCSettings;
import cz.it4i.parallel.paradigm_managers.MultipleHostsParadigmManagerUsingRunner;
import cz.it4i.parallel.paradigm_managers.ParadigmProfileUsingRunner;
import cz.it4i.parallel.paradigm_managers.ParadigmProfileWithSettings;
import cz.it4i.parallel.paradigm_managers.ServerRunner;
import cz.it4i.parallel.paradigm_managers.ui.HPCImageJRunnerWithUI;
import cz.it4i.parallel.paradigm_managers.ui.HavingOwnerWindow;
import javafx.stage.Window;


@Plugin(type = ParadigmManager.class)
public class HPCImageJServerParadigmProfileManager extends
	MultipleHostsParadigmManagerUsingRunner<ImageJServerParadigm, HPCSettings>
	implements HavingOwnerWindow<Window>
{
	
	@Parameter
	private Context context;

	private Window ownerWindow;

	@Override
	public Class<Window> getType() {
		return Window.class;
	}

	@Override
	public void setOwner(Window parent) {
		ownerWindow = parent;
	}

	@Override
	public Class<ImageJServerParadigm> getSupportedParadigmType() {
		return ImageJServerParadigm.class;
	}

	@Override
	protected boolean editSettings(
		ParadigmProfileWithSettings<HPCSettings> typedProfile)
	{
		Boolean correct = false;
		
		HPCSettings settings = typedProfile.getSettings();
		correct = super.editSettings(typedProfile);
		if (settings != null) {
			typedProfile.getSettings().setJobID(settings.getJobID());
		}
		
		return correct;
	}

	@Override
	protected Class<? extends ServerRunner<HPCSettings>> getTypeOfRunner() {
		return HPCImageJServerRunnerUI.class;
	}

	@Override
	protected void initParadigm(
		ParadigmProfileUsingRunner<HPCSettings> typedProfile,
		ImageJServerParadigm paradigm)
	{
		super.initParadigm(typedProfile, paradigm);
		typedProfile.getSettings().setJobID(
			((HPCImageJRunner) typedProfile.getAssociatedRunner()).getJob()
				.getID());
	}

	@Override
	protected void initRunner(ServerRunner<?> runner) {
		HPCImageJRunnerWithUI typedRunner =
			(HPCImageJRunnerWithUI) runner;
		typedRunner.initOwnerWindow(ownerWindow);
	}
}
