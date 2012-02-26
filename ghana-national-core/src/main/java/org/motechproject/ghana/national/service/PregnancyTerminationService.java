package org.motechproject.ghana.national.service;

import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllFacilities;
import org.motechproject.ghana.national.repository.AllPatients;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.service.request.PregnancyTerminationRequest;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.configuration.ScheduleNames.DELIVERY;
import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION_VISIT;
import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.PREG_TERM_VISIT;
import static org.motechproject.ghana.national.tools.Utility.safePareInteger;

@Service
public class PregnancyTerminationService {

    public static final String PREGNANCY_TERMINATION = "Pregnancy Termination";
    public static final String OTHER_CAUSE_OF_DEATH = "OTHER";

    private AllPatients allPatients;
    private AllEncounters allEncounters;
    private AllFacilities allFacilities;
    private AllSchedules allSchedules;

    @Autowired
    public PregnancyTerminationService(AllPatients allPatients, AllEncounters allEncounters, AllFacilities allFacilities,
                                       AllSchedules allSchedules) {
        this.allPatients = allPatients;
        this.allEncounters = allEncounters;
        this.allFacilities = allFacilities;
        this.allSchedules = allSchedules;
    }

    public void terminatePregnancy(PregnancyTerminationRequest request) {
        Patient patient = allPatients.getPatientByMotechId(request.getMotechId());
        MRSPatient mrsPatient = patient.getMrsPatient();
        Facility facility = allFacilities.getFacilityByMotechId(request.getFacilityId());
        allEncounters.persistEncounter(mrsPatient, request.getStaffId(), facility.getMrsFacilityId(), PREG_TERM_VISIT.value(), request.getTerminationDate(), prepareObservations(request));
        if (request.isDead()) {
            allPatients.deceasePatient(request.getTerminationDate(), request.getMotechId(), OTHER_CAUSE_OF_DEATH, PREGNANCY_TERMINATION);
            allSchedules.unEnroll(patient, TT_VACCINATION_VISIT);
        }

        allSchedules.unEnroll(patient, DELIVERY);
    }

    private Set<MRSObservation> prepareObservations(PregnancyTerminationRequest request) {
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();

        for (String complication : request.getComplications()) {
            addObservation(request.getTerminationDate(), mrsObservations, TERMINATION_COMPLICATION.getName(), safePareInteger(complication));
        }
        addObservation(request.getTerminationDate(), mrsObservations, PREGNANCY_STATUS.getName(), false);
        addObservation(request.getTerminationDate(), mrsObservations, TERMINATION_TYPE.getName(), safePareInteger(request.getTerminationType()));
        String terminationProcedure = request.getTerminationProcedure();
        if (!terminationProcedure.equals(Constants.NOT_APPLICABLE))
            addObservation(request.getTerminationDate(), mrsObservations, TERMINATION_PROCEDURE.getName(), safePareInteger(terminationProcedure));
        addObservation(request.getTerminationDate(), mrsObservations, MATERNAL_DEATH.getName(), request.isDead());
        addObservation(request.getTerminationDate(), mrsObservations, REFERRED.getName(), request.isReferred());
        addObservation(request.getTerminationDate(), mrsObservations, COMMENTS.getName(), request.getComments());
        addObservation(request.getTerminationDate(), mrsObservations, POST_ABORTION_FP_ACCEPTED.getName(), request.getPostAbortionFPAccepted());
        addObservation(request.getTerminationDate(), mrsObservations, POST_ABORTION_FP_COUNSELING.getName(), request.getPostAbortionFPCounselling());
        return mrsObservations;
    }

    private <T> void addObservation(Date observationDate, Set<MRSObservation> mrsObservations, String observationType, T observationValue) {
        if (observationValue != null)
            mrsObservations.add(new MRSObservation<T>(observationDate, observationType, observationValue));
    }
}
