
package cz.it4i.parallel.imagej.server;

import static cz.it4i.common.FileRoutines.getSuffix;
import static cz.it4i.common.ObjectRoutines.castTo;
import static cz.it4i.parallel.InternalExceptionRoutines.runWithExceptionHandling;

import io.scif.io.ByteArrayHandle;
import io.scif.services.DatasetIOService;
import io.scif.services.LocationService;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import net.imagej.Dataset;
import net.imglib2.RandomAccessibleInterval;

import org.scijava.plugin.Parameter;

public class AbstractDatasetImageJServerConverter<T extends RandomAccessibleInterval<?>>
	extends AbstractParallelizationParadigmConverter<T> implements Closeable
{

	private static int SIZE_OF_CHUNK = 1024 * 1024;

	private static final String NAME_FOR_EXPORT = "export";

	private static final String DEFAULT_SUFFIX = ".tif";
	@Parameter
	private DatasetIOService ioService;

	@Parameter
	private LocationService locationService;

	private RemoteDataHandler parallelWorker;

	private Dataset workingDataSet;
	private Object workingDataSetID;

	public AbstractDatasetImageJServerConverter() {
		super(Collections.emptySet(), null);
	}

	public AbstractDatasetImageJServerConverter(Class<T> clazz) {
		super(Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			ImageJServerParadigm.class))), clazz);
	}

	@Override
	public ParallelizationParadigmConverter<T> cloneForWorker(
		RemoteDataHandler worker)
	{
		// Remark: better use ThreadLocal in this class, than
		// explicitly calling cloneForWorker.
		AbstractDatasetImageJServerConverter<T> result = createEmptyInstance();
		result.ioService = ioService;
		result.parallelWorker = worker;
		result.locationService = locationService;
		return result;
	}

	@Override
	public <S> S convert(Object src, Class<S> dest) {
		if (dest == Object.class) {
			return castTo(convert2Paradigm(src));
		}
		return castTo(convert2Local(src));
	}
	
	@Override
	public void close() throws IOException {
		// ignore this
	}

	protected AbstractDatasetImageJServerConverter<T> createEmptyInstance() {
		throw new UnsupportedOperationException();
	}

	private Object convert2Paradigm(Object input) {
		if (input instanceof Path) {
			throw new UnsupportedOperationException(
				"Using path instead of Dataset is not supported.");
		}
		else if (input instanceof Dataset) {
			workingDataSet = (Dataset) input;
			workingDataSetID = parallelWorker.importData(workingDataSet.getName(),
				is -> runWithExceptionHandling(() -> writeDataset2OutputStream(
					workingDataSet, is)));
			return workingDataSetID;
		}
		throw new IllegalArgumentException("cannot convert from " + input
			.getClass());
	}

	private Object convert2Local(Object input) {
		// Remark: The download shouldn't depend on how the upload happend before.
		// This connection between upload and download is artificial, and
		// makes the download rather unstable.
		String name = NAME_FOR_EXPORT + getSuffixForExport();
		PDatasetExporter datasetExporter = new PDatasetExporter();
		runWithExceptionHandling(() -> parallelWorker.exportData(input, name,
			os -> runWithExceptionHandling(() -> datasetExporter.readDataset(name,
				os))));
		parallelWorker.deleteData(input);
		if (workingDataSetID != null && !input.equals(workingDataSetID)) {
			parallelWorker.deleteData(workingDataSetID);
		}
		Dataset tempDataset = datasetExporter.getResult();
		if (workingDataSet != null && input.equals(workingDataSetID)) {
			tempDataset.copyInto(workingDataSet);
			return workingDataSet;
		}
		return tempDataset;

	}

	private String getSuffixForExport() {
		if (workingDataSet != null) {
			return getSuffix(workingDataSet.getName());
		}
		return DEFAULT_SUFFIX;
	}


	private void writeDataset2OutputStream(Dataset dataset,
		OutputStream os) throws IOException
	{
		String id = UUID.randomUUID().toString() + "_" + dataset.getName();
		ByteArrayHandle bh = new ByteArrayHandle();
		locationService.mapFile(id, bh);
		ioService.save(workingDataSet, id);
		int pointer = 0;
		byte[] data = bh.getBytes();
		while (pointer < bh.length()) {

			int toRead = (int) Math.min(SIZE_OF_CHUNK, bh.length() - pointer);
			os.write(data, pointer, toRead);
			pointer += toRead;
		}
		bh.close();
	}

	private class PDatasetExporter {

		Dataset result;

		void readDataset(String name, InputStream is) throws IOException {
			String id = UUID.randomUUID().toString() + "_" + name;
			ByteArrayHandle bh = new ByteArrayHandle();
			locationService.mapFile(id, bh);
			byte[] buffer = new byte[SIZE_OF_CHUNK];
			int readed;
			while (-1 != (readed = is.read(buffer))) {
				bh.write(buffer, 0, readed);
			}
			result = ioService.open(id);
			bh.close();
		}

		public Dataset getResult() {
			return result;
		}
	}
}
