package cz.it4i.parallel.imagej.server;


interface ParameterTypeProvider {
	
	String provideParameterTypeName(String commandName, String parameterName);
}
