/*
 * @(#)VersionManagement.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package CH.ifa.draw.util;

import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 * Collection of utility methods that are useful for dealing with version information
 * retrieved from reading jar files or package loaded by the class manager. Some
 * methods also help comparing version information. The method getJHotDrawVersion()
 * can be used to retrieve the current version of JHotDraw as loaded by the class manager.
 *
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class VersionManagement {
	public static String JHOTDRAW_COMPONENT = "CH.ifa.draw/";
	public static String JHOTDRAW_JAR = "jhotdraw.jar";
	
	public static Package[] packages = {
			Package.getPackage("CH.ifa.draw.applet"),
			Package.getPackage("CH.ifa.draw.application"),
			Package.getPackage("CH.ifa.draw.contrib"),
			Package.getPackage("CH.ifa.draw.figures"),
			Package.getPackage("CH.ifa.draw.framework"),
			Package.getPackage("CH.ifa.draw.standard"),
			Package.getPackage("CH.ifa.draw.util")
		};

	/**
	 * Return the version of the main package of the framework. A version number is
	 * available if there is a corresponding version entry in the JHotDraw jar file
	 * for the framework package.
	 */	
	public static String getJHotDrawVersion() {
		// look for the framework main package
		Package pack = packages[4];
		return pack.getSpecificationVersion();
	}

	/**
	 *
	 */
	public static String getPackageVersion(final Package lookupPackage) {
		if (lookupPackage == null) {
			return null;
		}
		
		String specVersion = lookupPackage.getSpecificationVersion();
		if (specVersion != null) {
			return specVersion;
		}
		else {
			// search in parent package
			String normalizedPackageName = normalizePackageName(lookupPackage.getName());
			String nextPackageName = getNextPackage(normalizedPackageName);
			return getPackageVersion(Package.getPackage(nextPackageName));
		}
	}

	/**
	 * Check whether a given application version is compatible with the version
	 * of JHotDraw currently loaded in the Java VM. A version number is
	 * available if there is a corresponding version entry in the JHotDraw jar file
	 * for the framework package.
	 */	
	public static boolean isCompatibleVersion(String compareVersionString) {
//		Package pack = VersionManagement.class.getPackage();
		Package pack = packages[4];
		if (compareVersionString == null) {
			return pack.getSpecificationVersion() == null;
		}
		else {
			return pack.isCompatibleWith(compareVersionString);
		}
	}
  public void setPositions(boolean check) {
    int max_additional_bytes = 0, additional_bytes = 0;
    int index = 0, count = 0;
    int[] pos = new int[length];

    /* Pass 0: Sanity checks
     */
    if(check) {
      for(InstructionHandle ih=start; ih != null; ih = ih.next) {
        Instruction i = ih.instruction;

        if(i instanceof BranchInstruction) { // target instruction within list?
          Instruction inst = ((BranchInstruction)i).getTarget().instruction;
          if(!contains(inst))
            throw new ClassGenException("Branch target of " +
                                        Constants.OPCODE_NAMES[i.opcode] + ":" +
                                        inst + " not in instruction list");

          if(i instanceof Select) {
            InstructionHandle[] targets = ((Select)i).getTargets();

            for(int j=0; j < targets.length; j++) {
              inst = targets[j].instruction;
              if(!contains(inst))
                throw new ClassGenException("Branch target of " +
                                            Constants.OPCODE_NAMES[i.opcode] + ":" +
                                            inst + " not in instruction list");
            }
          }

          if(!(ih instanceof BranchHandle))
            throw new ClassGenException("Branch instruction " +
                                        Constants.OPCODE_NAMES[i.opcode] + ":" +
                                        inst + " not contained in BranchHandle.");

        }
      }
    }

    /* Pass 1: Set position numbers and sum up the maximum number of bytes an
     * instruction may be shifted.
     */
    for(InstructionHandle ih=start; ih != null; ih = ih.next) {
      Instruction i = ih.instruction;

      ih.setPosition(index);
      pos[count++] = index;

      /* Get an estimate about how many additional bytes may be added, because
       * BranchInstructions may have variable length depending on the target
       * offset (short vs. int) or alignment issues (TABLESWITCH and
       * LOOKUPSWITCH).
       */
      switch(i.getOpcode()) {
      case Constants.JSR: case Constants.GOTO:
        max_additional_bytes += 2;
        break;

      case Constants.TABLESWITCH: case Constants.LOOKUPSWITCH:
        max_additional_bytes += 3;
        break;
      }

      index += i.getLength();
    }

    /* Pass 2: Expand the variable-length (Branch)Instructions depending on
     * the target offset (short or int) and ensure that branch targets are
     * within this list.
     */
    for(InstructionHandle ih=start; ih != null; ih = ih.next)
      additional_bytes += ih.updatePosition(additional_bytes, max_additional_bytes);

    /* Pass 3: Update position numbers (which may have changed due to the
     * preceding expansions), like pass 1.
     */
    index=count=0;
    for(InstructionHandle ih=start; ih != null; ih = ih.next) {
      Instruction i = ih.instruction;

      ih.setPosition(index);
      pos[count++] = index;
      index += i.getLength();
    }

    byte_positions = new int[count]; // Trim to proper size
    System.arraycopy(pos, 0, byte_positions, 0, count);
  }

	/**
	 * Read the version information from a file with a given file name. The file
	 * must be a jar manifest file and all its entries are searched for package names
	 * and their specification version information. All information is collected
	 * in a map for later lookup of package names and their versions.
	 *
	 * @param versionFileName name of the jar file containing version information
	 */
	public static String readVersionFromFile(String applicationName, String versionFileName) {
		try {
			FileInputStream fileInput = new FileInputStream(versionFileName);
			Manifest manifest = new Manifest();
			manifest.read(fileInput);

			Map entries = manifest.getEntries();
			// Now write out the pre-entry attributes
			Iterator entryIterator = entries.entrySet().iterator();
			while (entryIterator.hasNext()) {
				Map.Entry currentEntry = (Map.Entry)entryIterator.next();
				String packageName = currentEntry.getKey().toString();
				packageName = normalizePackageName(packageName);
				Attributes attributes = (Attributes)currentEntry.getValue();
				String packageSpecVersion = attributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
				packageSpecVersion = extractVersionInfo(packageSpecVersion);
				return packageSpecVersion;
			}
		}
		catch (IOException exception) {
			exception.printStackTrace();
		}
		
		// no version found
		return null;
	}

	/**
	 * Get the super package of a package specifier. The super package is the package
	 * specifier without the latest package name, e.g. the next package for "org.jhotdraw.tools"
	 * would be "org.jhotdraw".
	 *
	 * @param searchPackage package specifier
	 * @return next package if one is available, null otherwise
	 */
	protected static String getNextPackage(String searchPackage) {
		if (searchPackage == null) {
			return null;
		}
	
		int foundNextPackage = searchPackage.lastIndexOf('.');
		if (foundNextPackage > 0) {
			return searchPackage.substring(0, foundNextPackage);
		}
		else {
			return null;
		}
	}
	
	/**
	 * A package name is normalized by replacing all path delimiters by "." to retrieve
	 * a valid standardized package specifier used in package declarations in Java source
	 * files.
	 *
	 * @param toBeNormalized package name to be normalized
	 * @return normalized package name
	 */
	public static String normalizePackageName(String toBeNormalized) {
		// first, replace the standard package delimiter used in jars
		String replaced = toBeNormalized.replace('/', '.');
		// then, replace the default path separator in case this one was used as well
		replaced = replaced.replace(File.pathSeparatorChar, '.');
		if (replaced.endsWith(".")) {
			int lastSeparator = replaced.lastIndexOf('.');
			return replaced.substring(0, lastSeparator);
		}
		else {
			return replaced;
		}
	}

	/**
	 * Get the version information specified in a jar manifest file without
	 * any leading or trailing "\"".
	 *
	 * @param versionString a version string with possibly leading or trailing "\""
	 * @return stripped version information
	 */
	public static String extractVersionInfo(String versionString) {
		// guarding conditions
		if (versionString == null) {
			return null;
		}
		if (versionString.length() == 0) {
			return "";
		}
		
		int startIndex = versionString.indexOf("\"");
		if (startIndex < 0) {
			startIndex = 0;
		}
		else {
			// start from next character
			startIndex++;
		}
		
		int endIndex = versionString.lastIndexOf("\"");
		if (endIndex < 0) {
			endIndex = versionString.length();
		}
		
		return versionString.substring(startIndex, endIndex);
	}
}
