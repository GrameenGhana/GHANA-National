package org.motechproject.ghana.national.service;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.*;
import org.motechproject.ghana.national.factory.MotherVisitEncounterFactory;
import org.motechproject.ghana.national.factory.TTVaccinationVisitEncounterFactory;
import org.motechproject.ghana.national.mapper.ScheduleEnrollmentMapper;
import org.motechproject.ghana.national.mapper.TTVaccinationEnrollmentMapper;
import org.motechproject.ghana.national.repository.AllEncounters;
import org.motechproject.ghana.national.repository.AllObservations;
import org.motechproject.ghana.national.repository.AllSchedules;
import org.motechproject.ghana.national.service.request.ANCVisitRequest;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSUser;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentResponse;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.configuration.ScheduleNames.DELIVERY;
import static org.motechproject.ghana.national.domain.IPTVaccine.createFromANCVisit;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;

@Service
public class MotherVisitService {

    private AllEncounters allEncounters;
    private AllObservations allObservations;
    private AllSchedules allSchedules;
//    private AllAppointments allAppointments;
    MotherVisitEncounterFactory factory;

    @Autowired
    public MotherVisitService(AllEncounters allEncounters, AllObservations allObservations, AllSchedules allSchedules/*, AllAppointments allAppointments*/) {
        this.allEncounters = allEncounters;
        this.allObservations = allObservations;
        this.allSchedules = allSchedules;
//        this.allAppointments = allAppointments;
        factory = new MotherVisitEncounterFactory();
    }

    public MRSEncounter registerANCVisit(ANCVisitRequest ancVisitRequest) {
        Set<MRSObservation> mrsObservations = factory.createMRSObservations(ancVisitRequest);
        updateEDD(ancVisitRequest, mrsObservations);
        updateIPT(ancVisitRequest, mrsObservations);
//        updateANCVisit(ancVisitRequest);
        return allEncounters.persistEncounter(factory.createEncounter(ancVisitRequest, mrsObservations));
    }

//    private void updateANCVisit(ANCVisitRequest ancVisitRequest) {
//        allAppointments.fulfilVisit(ancVisitRequest.getPatient());
//        allAppointments.createVisit(ancVisitRequest.getPatient());
//    }

    private void updateIPT(ANCVisitRequest ancVisitRequest, Set<MRSObservation> mrsObservations) {
        IPTVaccine iptVaccine = createFromANCVisit(ancVisitRequest);
        if (iptVaccine != null) {
            mrsObservations.addAll(factory.createObservationsForIPT(iptVaccine));
            createIPTpSchedule(iptVaccine);
        }
    }

    private void updateEDD(ANCVisitRequest ancVisitRequest, Set<MRSObservation> mrsObservations) {
        Set<MRSObservation> eddObservations = allObservations.updateEDD(ancVisitRequest.getEstDeliveryDate(), ancVisitRequest.getPatient(), ancVisitRequest.getStaff().getId());
        if (CollectionUtils.isNotEmpty(eddObservations)) {
            mrsObservations.addAll(eddObservations);
            EnrollmentRequest enrollmentRequest = new ScheduleEnrollmentMapper().map(ancVisitRequest.getPatient(),
                    new PatientCare(DELIVERY, basedOnDeliveryDate(DateUtil.newDate(ancVisitRequest.getEstDeliveryDate())).dateOfConception()));
            allSchedules.enroll(enrollmentRequest);
        }
    }

    public void receivedTT(final TTVaccineDosage dosage, Patient patient, MRSUser staff, Facility facility, final LocalDate vaccinationDate) {
        TTVisit ttVisit = new TTVisit().dosage(dosage).facility(facility).patient(patient).staff(staff).date(vaccinationDate.toDate());
        Encounter encounter = new TTVaccinationVisitEncounterFactory().createEncounterForVisit(ttVisit);
        allEncounters.persistEncounter(encounter);
        final EnrollmentRequest enrollmentRequest = new TTVaccinationEnrollmentMapper().map(patient, vaccinationDate, dosage.getScheduleMilestoneName());
        allSchedules.enrollOrFulfill(enrollmentRequest);
    }

    private void createIPTpSchedule(IPTVaccine iptVaccine) {
        Patient patient = iptVaccine.getGivenTo();
        LocalDate expectedDeliveryDate = fetchLatestEDD(patient);
        EnrollmentResponse enrollmentResponse = allSchedules.enrollment(queryEnrollmentRequest(patient, ANC_IPT_VACCINE));
        if(enrollmentResponse == null) {
            allSchedules.enroll(enrollmentRequest(patient, patient.iptPatientCareEnrollOnRegistration(expectedDeliveryDate)));
        }
        allSchedules.fulfilCurrentMilestone(enrollmentRequest(patient, patient.iptPatientCareVisit()));
    }

    private LocalDate fetchLatestEDD(Patient patient) {
        MRSObservation eddObservation = allObservations.findObservation(patient.getMRSPatientId(), Concept.EDD.getName());
        return new LocalDate(eddObservation.getValue());
    }

    private EnrollmentRequest enrollmentRequest(Patient patient, PatientCare patientCare) {
        return new ScheduleEnrollmentMapper().map(patient, patientCare);
    }

    private EnrollmentRequest queryEnrollmentRequest(Patient patient, String programName) {
        return new ScheduleEnrollmentMapper().map(patient.getMRSPatientId(), programName);
    }
}