package cz.it4i.parallel.imagej.server.runners;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import cz.it4i.parallel.Routines;

class WaitForHTTPServerRunTS {

	static WaitForHTTPServerRunTS create(final String urlStr) {
		return new WaitForHTTPServerRunTS(urlStr);
	}

	final private String urlStr;

	private long waitTimeout = 200;

	private WaitForHTTPServerRunTS(final String url)
	{
		super();
		this.urlStr = url;
	}

	WaitForHTTPServerRunTS timeout(long val) {
		this.waitTimeout = val;
		return this;
	}

	void run() {
		while (true) {
			try {
				if (checkURL()) {
					break;
				}
				Routines.runWithExceptionHandling(() -> Thread.sleep(waitTimeout));
			}
			catch (IOException e) {
				// ignore waiting for start
			}
		}
	}

	private boolean checkURL() throws IOException {
		HttpURLConnection hc;
		hc = (HttpURLConnection) new URL(urlStr).openConnection();
		hc.setRequestMethod("GET");
		hc.connect();
		hc.disconnect();
		return hc.getResponseCode() == 200;
	}

}
