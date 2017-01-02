package it.unina.android.ripper.installer.console;

import java.io.File;

public class GuiRipperInstallerMain {
	
	public static void main(String[] args) {
		
		System.out.println("Android Ripper Installer");
		
		GuiRipperInstaller guiRipperInstaller = null;
		
		if (args.length > 0) {
			
			if (new File(args[0]).exists()) {
				System.out.println("Using configuration file : '"+args[0]+"'!");
				guiRipperInstaller = new GuiRipperInstaller(args[0]);
			} else {
				System.out.println("Configuration file '"+args[0]+"' does not exist!");
				System.exit(0);
			}
		} else {
			System.out.println("Using default configuration file 'ripper.properties'!");
			guiRipperInstaller = new GuiRipperInstaller();
		}
	
		if (guiRipperInstaller != null) {
			System.out.println("Starting installation...");
			guiRipperInstaller.install();
		} else {
			System.out.println("Initialization error!");
		}
		
	}
	
}
