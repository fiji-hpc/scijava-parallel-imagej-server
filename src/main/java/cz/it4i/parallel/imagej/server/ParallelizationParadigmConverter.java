
package cz.it4i.parallel.imagej.server;

import java.util.Set;

import org.scijava.parallel.ParallelizationParadigm;
import org.scijava.plugin.SciJavaPlugin;

public interface ParallelizationParadigmConverter<O> extends SciJavaPlugin
{

	<T> T convert(Object src, Class<T> dest);

	Class<O> getOutputType();

	Set<Class<? extends ParallelizationParadigm>> getSupportedParadigms();

	ParallelizationParadigmConverter<O> cloneForWorker(RemoteDataHandler worker);


}
