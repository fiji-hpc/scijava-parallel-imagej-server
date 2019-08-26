
package cz.it4i.parallel.imagej.server;

import java.util.Set;

import org.scijava.parallel.ParallelizationParadigm;
import org.scijava.plugin.SciJavaPlugin;

interface ParallelizationParadigmConverter extends SciJavaPlugin
{

	<T> T convert(Object src, Class<T> dest);

	Class<?> getOutputType();

	Set<Class<? extends ParallelizationParadigm>> getSupportedParadigms();

	ParallelizationParadigmConverter cloneForWorker(RemoteDataHandler worker);


}
