package com.qpidhealth.qpid.search.manager;

import java.util.List;

import com.qpidhealth.qpid.model.Patient;

public interface PatientSearch {
	
	public List<Patient> searchPatients(String query);

}
