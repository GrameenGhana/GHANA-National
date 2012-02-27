package org.motechproject.ghana.national.factory;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.Concept;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Encounter;
import org.motechproject.ghana.national.domain.IPTVaccine;
import org.motechproject.ghana.national.service.request.ANCVisitRequest;
import org.motechproject.mrs.model.MRSConcept;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.util.DateUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.motechproject.ghana.national.domain.Concept.*;
import static org.motechproject.ghana.national.domain.EncounterType.ANC_VISIT;

public class MotherVisitEncounterFactory extends BaseObservationFactory {


    public Encounter createEncounter(ANCVisitRequest ancVisitRequest, Set<MRSObservation> mrsObservations) {
        return new Encounter(ancVisitRequest.getPatient().getMrsPatient(), ancVisitRequest.getStaff(),
                ancVisitRequest.getFacility(), ANC_VISIT, ancVisitRequest.getDate(), mrsObservations);
    }

    public Set<MRSObservation> createMRSObservations(ANCVisitRequest ancVisitRequest) {
        Set<MRSObservation> mrsObservations = new HashSet<MRSObservation>();
        Date registrationDate = ancVisitRequest.getDate() == null ? DateUtil.today().toDate() : ancVisitRequest.getDate();

        setObservation(mrsObservations, registrationDate, ANC_PNC_LOCATION.getName(), toInteger(ancVisitRequest.getLocation()));
        setObservation(mrsObservations, registrationDate, COMMUNITY.getName(), ancVisitRequest.getCommunity());
        setObservation(mrsObservations, registrationDate, COMMENTS.getName(), ancVisitRequest.getComments());
        setObservation(mrsObservations, registrationDate, DEWORMER.getName(), toBoolean(ancVisitRequest.getDewormer()));
        setObservation(mrsObservations, registrationDate, DIASTOLIC_BLOOD_PRESSURE.getName(), ancVisitRequest.getBpDiastolic());
        setObservation(mrsObservations, registrationDate, FHR.getName(), ancVisitRequest.getFhr());
        setObservation(mrsObservations, registrationDate, FHT.getName(), ancVisitRequest.getFht());
        setObservation(mrsObservations, registrationDate, HEMOGLOBIN.getName(), ancVisitRequest.getHemoglobin());
        setObservation(mrsObservations, registrationDate, HIV_TEST_RESULT.getName(), ancVisitRequest.getHivTestResult());
        setObservation(mrsObservations, registrationDate, HIV_PRE_TEST_COUNSELING.getName(), toBoolean(ancVisitRequest.getPreTestCounseled()));
        setObservation(mrsObservations, registrationDate, HIV_POST_TEST_COUNSELING.getName(), toBoolean(ancVisitRequest.getPostTestCounseled()));
        setObservation(mrsObservations, registrationDate, HOUSE.getName(), ancVisitRequest.getHouse());
        setObservation(mrsObservations, registrationDate, INSECTICIDE_TREATED_NET_USAGE.getName(), toBoolean(ancVisitRequest.getItnUse()));
        setObservation(mrsObservations, registrationDate, MALE_INVOLVEMENT.getName(), ancVisitRequest.getMaleInvolved());
        setObservation(mrsObservations, registrationDate, NEXT_ANC_DATE.getName(), ancVisitRequest.getNextANCDate());
        setObservation(mrsObservations, registrationDate, PMTCT.getName(), toBoolean(ancVisitRequest.getPmtct()));
        setObservation(mrsObservations, registrationDate, PMTCT_TREATMENT.getName(), toBoolean(ancVisitRequest.getPmtctTreament()));
        setObservation(mrsObservations, registrationDate, REFERRED.getName(), toBoolean(ancVisitRequest.getReferred()));
        setObservation(mrsObservations, registrationDate, SERIAL_NUMBER.getName(), ancVisitRequest.getSerialNumber());
        setObservation(mrsObservations, registrationDate, SYSTOLIC_BLOOD_PRESSURE.getName(), ancVisitRequest.getBpSystolic());
        setObservation(mrsObservations, registrationDate, TT.getName(), toInteger(ancVisitRequest.getTtdose()));
        setObservation(mrsObservations, registrationDate, URINE_GLUCOSE_TEST.getName(), getConceptForTest(ancVisitRequest.getUrineTestGlucosePositive()));
        setObservation(mrsObservations, registrationDate, URINE_PROTEIN_TEST.getName(), getConceptForTest(ancVisitRequest.getUrineTestProteinPositive()));
        setObservation(mrsObservations, registrationDate, VDRL.getName(), getConceptReactionResult(ancVisitRequest.getVdrlReactive()));
        setObservation(mrsObservations, registrationDate, VDRL_TREATMENT.getName(), toBoolean(ancVisitRequest.getVdrlTreatment()));
        setObservation(mrsObservations, registrationDate, VISIT_NUMBER.getName(), toInteger(ancVisitRequest.getVisitNumber()));
        setObservation(mrsObservations, registrationDate, WEIGHT_KG.getName(), ancVisitRequest.getWeight());
        return mrsObservations;
    }

    public Set<MRSObservation> createObservationsForIPT(final IPTVaccine iptVaccine) {
        final LocalDate observationDate = iptVaccine.getVaccinationDate();
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        setObservation(observations, observationDate.toDate(), Concept.IPT.getName(), iptVaccine.getIptDose());
        setObservation(observations, observationDate.toDate(), Concept.IPT_REACTION.getName(), new MRSConcept(iptVaccine.getIptReactionConceptName()));
        return observations;
    }

    private MRSConcept getConceptReactionResult(String reading) {
        if (reading == null) return null;
        return (reading.equals(Constants.OBSERVATION_YES)) ? new MRSConcept(REACTIVE.getName()) : new MRSConcept(NON_REACTIVE.getName());
    }

    private MRSConcept getConceptForTest(String reading) {
        if (reading == null) return null;
        MRSConcept concept = null;
        switch (Integer.valueOf(reading)) {
            case 0:
                concept = new MRSConcept(NEGATIVE.getName());
                break;
            case 1:
                concept = new MRSConcept(POSITIVE.getName());
                break;
            case 2:
                concept = new MRSConcept(TRACE.getName());
                break;
        }
        return concept;
    }
}
