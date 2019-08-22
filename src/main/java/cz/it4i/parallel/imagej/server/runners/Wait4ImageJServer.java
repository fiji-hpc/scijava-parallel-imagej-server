/*******************************************************************************
 * IT4Innovations - National Supercomputing Center
 * Copyright (c) 2017 - 2019 All Right Reserved, https://www.it4i.cz
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this project.
 ******************************************************************************/
package cz.it4i.parallel.imagej.server.runners;


final class Wait4ImageJServer {

	private Wait4ImageJServer() {}

	static void doIt(int port) {
		WaitForHTTPServerRunTS.create("http://localhost:" + port + "/modules")
			.run();
	}
}
