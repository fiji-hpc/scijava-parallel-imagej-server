package cz.it4i.parallel.imagej.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

public interface RemoteDataHandler {

	void exportData(Object dataset, String filePath,
		Consumer<InputStream> isConsumer) throws IOException;

	Object importData(String fileName, Consumer<OutputStream> osConsumer);

	void deleteData(Object ds);

}
