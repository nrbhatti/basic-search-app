package com.qpidhealth.qpid.search.services;

import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import com.qpidhealth.qpid.model.Patient;
import com.qpidhealth.qpid.search.manager.PatientFileSearch;
import com.qpidhealth.qpid.search.manager.PatientSearch;

import static javax.ejb.LockType.READ;

@Path("/patients")
@Singleton
@Lock(READ)
public class PatientService {
	
    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Patient> searchPatients(@QueryParam("query") String query) {
        PatientSearch patientSearch = new PatientFileSearch();
    	List<Patient> patients = patientSearch.searchPatients(query);
        return patients;
    }

}
