package com.qpidhealth.qpid.search.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import com.qpidhealth.qpid.model.Patient;
import com.qpidhealth.qpid.search.services.PatientService;

public class PatientFileSearch implements PatientSearch {
	final protected static String PATIENT_SUMMARY = "Summary";
	final protected static String RESOURCE_PATH = "documents";

	/**
	 * Returns matching patient records
	 */
	public List<Patient> searchPatients(String query) {

		/*
		 * Finds matching patient file names by comparing patient file titles
		 * and contents
		 */
		List<String> matchingFiles = this.getMatchingPatientFiles(query);
		List<Patient> patients = new ArrayList<Patient>();

		/*
		 * Loads customer records from summary file.
		 */
		for (String fileName : matchingFiles) {
			Patient p = loadPatient(fileName);
			if (p != null) {
				patients.add(p);
			}
		}
		return patients;

	}

	/**
	 * Reads summary file and loads patient data. Method reads summary file line
	 * by line.
	 * 
	 * @param fileName:
	 *            Patient file to load.
	 * @return
	 */
	protected Patient loadPatient(String fileName) {

		/*
		 * Reads summary file
		 */
		String summary = this.loadResource(PatientFileSearch.PATIENT_SUMMARY);

		/*
		 * Divides the file by lines.
		 */
		String[] lines = summary.split(System.getProperty("line.separator"));

		/*
		 * Iterate over lines and matches the passed file name until record is
		 * found or EOF reached.
		 */
		boolean found = false;
		for (int i = 0; i < lines.length;) {

			Patient p = new Patient();
			if (this.hasData(lines, i)) {
				p.setId(Long.valueOf(lines[i]));
				++i;
			}

			if (this.hasData(lines, i)) {
				p.setName(lines[i]);
				++i;
			}
			List<String> docs = new ArrayList<String>();

			if (this.hasData(lines, i)) {
				String patientFileName = this.getResourceNameFromSummaryFile(lines[i]);
				docs.add(this.getPatientNoteFromSummaryFile(lines[i]) + loadResource(patientFileName));
				++i;
				/*
				 * Compares file name in Summary file with the passed file
				 */
				if (fileName.equalsIgnoreCase(patientFileName)) {
					found = true;

				}

			}

			if (this.hasData(lines, i)) {
				String patientFileName = this.getResourceNameFromSummaryFile(lines[i]);
				docs.add(this.getPatientNoteFromSummaryFile(lines[i])
						+ loadResource(this.getResourceNameFromSummaryFile(lines[i])));
				++i;

				/*
				 * Compares file name in Summary file with the passed file
				 */
				if (fileName.equalsIgnoreCase(patientFileName)) {
					found = true;

				}
			}

			i = i + 1;
			p.setDocuments(docs);
			if (found) {
				return p;
			}
		}

		return null;

	}

	/**
	 * 
	 * @param lines
	 * @param i
	 * @return
	 */
	protected boolean hasData(String[] lines, int i) {

		if (i >= lines.length) {
			return false;

		}
		String line = lines[i];

		if (line == null || line.trim().isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Gets patient note from summary file
	 * 
	 * @param line
	 * @return
	 */
	protected String getPatientNoteFromSummaryFile(String line) {
		String patientNote = "";
		int pos = line.lastIndexOf(":::");
		if (pos != -1) {
			return line.substring(0, pos + 3);
		}

		return patientNote;
	}

	/**
	 * Get patient file name.
	 * 
	 * @param line
	 * @return
	 */
	protected String getResourceNameFromSummaryFile(String line) {
		String resourceName = "";
		int pos = line.lastIndexOf(":::");
		if (pos != -1) {
			return line.substring(pos + 3);
		}

		return resourceName;
	}

	/**
	 * Loads a file
	 * 
	 * @param fileName
	 * @return
	 */
	protected String loadResource(String fileName) {
		ClassLoader classLoader = PatientService.class.getClassLoader();
		String contents = null;
		try {
			contents = IOUtils.toString(classLoader.getResourceAsStream(
					PatientFileSearch.RESOURCE_PATH + System.getProperty("file.separator") + fileName + ".txt"));
		} catch (IOException e) {
			// TODO: Add exceptions to logger
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return contents;
	}

	/**
	 * Loops over patient files in Resource directory and loads the file if
	 * found
	 * 
	 * @param query
	 * @return
	 */
	protected List<String> getMatchingPatientFiles(String query) {

		List<String> resourceNames = new ArrayList<String>();
		ClassLoader classLoader = PatientService.class.getClassLoader();
		try {

			File files = new File(classLoader.getResource(PatientFileSearch.RESOURCE_PATH).getPath());
			resourceNames = this.searchTitlesAndContents(files, query);

		} catch (FileNotFoundException e) {
			// TODO: Add exceptions to logger
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return resourceNames;

	}

	/**
	 * Searches patient file by comparing file titles and contents
	 * 
	 * @param file
	 * @param pattern
	 * @return
	 * @throws FileNotFoundException
	 */
	protected List<String> searchTitlesAndContents(File file, String pattern) throws FileNotFoundException {

		if (!file.isDirectory()) {
			throw new IllegalArgumentException("file has to be a directory");
		}

		List<String> result = new ArrayList<String>();

		File[] files = file.listFiles();

		if (files != null) {
			for (File currentFile : files) {
				Scanner scanner = new Scanner(currentFile);

				String fileName = currentFile.getName();
				/*
				 * Ignore summary file
				 */
				if (fileName.equalsIgnoreCase("summary.txt")) {
					continue;
				}
				if (fileName.contains(".txt")) {
					fileName = fileName.substring(0, fileName.indexOf(".txt"));
				}

				/*
				 * Match file title
				 */
				if (fileName.matches("(?i:.*" + pattern + ".*)")) {
					result.add(fileName);
				}
				/*
				 * Search File contents
				 */
				else if (scanner.findWithinHorizon(pattern, 0) != null) {
					result.add(fileName);
					scanner.close();
				}
			}
		}
		return result;
	}
}
