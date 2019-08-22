package cz.it4i.parallel.imagej.server;


public interface ParameterTypeProvider {
	
	String provideParameterTypeName(String commandName, String parameterName);
}
