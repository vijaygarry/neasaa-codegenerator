package com.neasaa.codegenerator;

import com.neasaa.codegenerator.jdbc.CodeGenerator;

public class CodeGenerationCliParameter {
	
	
	private CodeGeneratorMode mode = CodeGeneratorMode.DB_TABLE_LIST;
	private String applicationConfigFilename = null;
	
	public CodeGeneratorMode getMode() {
		return this.mode;
	}
	
	public String getApplicationConfigFilename() {
		return this.applicationConfigFilename;
	}
	
	public static CodeGenerationCliParameter parseCommandLineParams (String aArgs[]) {
		CodeGenerationCliParameter cliParams = new CodeGenerationCliParameter();
		
		if(aArgs == null || aArgs.length == 0) {
			printUsage();
			System.exit(1);
		}
		
		cliParams.mode = CodeGeneratorMode.valueOf(aArgs[0]);
		if(cliParams.mode == null) {
			System.err.println ("Invalid mode " + aArgs[0] + ". Possible values are " + CodeGeneratorMode.values());
		}
		if(aArgs.length > 1) {
			cliParams.applicationConfigFilename = aArgs[1];
		}
		return cliParams;
	}
	
	public static void printUsage () {
		System.out.println ( CodeGenerator.class + " <mode> <applicationConfig>");
	}
}
