package com.qpidhealth.qpid.search.manager;

import static org.junit.Assert.*;

import org.junit.Test;

public class PatientFileSearchTest {

	@Test
	public void testMatchingPatient() {
		PatientFileSearch pfs = new PatientFileSearch();
		// Must come back with some results or fail
		assertNotEquals(0, pfs.getMatchingPatientFiles("Sam_1").size());
	}

	@Test
	public void testSummaryFileExists() {
		PatientFileSearch pfs = new PatientFileSearch();
		assertNotNull(pfs.loadResource(PatientFileSearch.PATIENT_SUMMARY));

	}

}
