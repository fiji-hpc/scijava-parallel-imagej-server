
package cz.it4i.parallel.imagej.server;

import java.util.Map;

public interface ParameterProcessor extends AutoCloseable {

	Map<String, Object> processInputs(Map<String, Object> inputs);

	Map<String, Object> processOutputs(Map<String, Object> inputs);

	@Override
	void close();
}
