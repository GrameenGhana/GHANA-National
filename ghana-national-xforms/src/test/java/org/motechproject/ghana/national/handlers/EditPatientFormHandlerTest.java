package org.motechproject.ghana.national.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ghana.national.bean.EditClientForm;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.model.MotechEvent;
import org.motechproject.mrs.model.*;
import org.motechproject.openmrs.advice.LoginAsAdmin;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EditPatientFormHandlerTest {

    @Mock
    PatientService mockPatientService;
    @Mock
    FacilityService mockFacilityService;
    @Mock
    StaffService mockStaffService;
    EditPatientFormHandler editPatientFormHandler;
    private EditClientForm editClientForm = new EditClientForm();


    @Before
    public void setUp() {
        initMocks(this);
        editPatientFormHandler = new EditPatientFormHandler();
        ReflectionTestUtils.setField(editPatientFormHandler, "patientService", mockPatientService);
        ReflectionTestUtils.setField(editPatientFormHandler, "facilityService", mockFacilityService);
        ReflectionTestUtils.setField(editPatientFormHandler, "staffService", mockStaffService);
    }


    @Test
    public void shouldCreateAnUpdatedPatientObject() throws ParentNotFoundException, PatientIdIncorrectFormatException, PatientIdNotUniqueException {

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String facilityId = "1";
        String facilityName = "facility";
        String country = "country";
        String region = "region";
        String district = "district";
        String state = "state";
        MRSFacility mrsFacility = new MRSFacility(facilityId, facilityName, country, region, district, state);
        Facility facility = new Facility(mrsFacility);
        facility.mrsFacilityId(facilityId);
        String updatedFacilityId = "2";
        MRSFacility mrsFacilityWherePatientWasEdited = new MRSFacility(updatedFacilityId, facilityName, country, region, district, state);
        Facility facilityWherePatientWasEdited = new Facility(mrsFacilityWherePatientWasEdited);
        facilityWherePatientWasEdited.mrsFacilityId(updatedFacilityId);
        String first = "first";
        String middle = "middle";
        String last = "last";
        String preferred = "preferred";
        Date dateOfBirth = new Date();
        String gender = "male";
        String address = "address";
        String patientId = "1000";
        Boolean birthDateEstimated = true;
        Date nhisExpDate = new Date();
        String nhisNumber = "123";
        String parentId = "123";
        String preferredName = "PreferredName";
        String sex = "M";
        String phoneNumber = "0123456789";
        String facilityIdWherePatientWasEdited = "999";
        String staffId = "654";
        String province = "province";


        populateFormWithValues(facilityId, first, middle, last, dateOfBirth, address, patientId, nhisExpDate, nhisNumber, parentId, preferredName, sex, phoneNumber, facilityIdWherePatientWasEdited, staffId);

        MRSPerson mrsPerson = new MRSPerson().firstName(first).middleName(middle).lastName(last).preferredName(preferred)
                .dateOfBirth(dateOfBirth).birthDateEstimated(birthDateEstimated).gender(gender).address(address);
        mrsPerson.addAttribute(new Attribute(PatientAttributes.NHIS_EXPIRY_DATE.toString(),nhisExpDate.toString()));
        mrsPerson.addAttribute(new Attribute(PatientAttributes.NHIS_NUMBER.toString(),nhisNumber));
        mrsPerson.addAttribute(new Attribute(PatientAttributes.PHONE_NUMBER.toString(),phoneNumber));
        final MRSPatient mrsPatient = new MRSPatient(patientId, mrsPerson, mrsFacility);
        final Patient patient = new Patient(mrsPatient);


        when(mockPatientService.getPatientByMotechId(patientId)).thenReturn(patient);
        when(mockFacilityService.getFacilityByMotechId(facilityId)).thenReturn(facility);
        when(mockFacilityService.getFacilityByMotechId(facilityIdWherePatientWasEdited)).thenReturn(facilityWherePatientWasEdited);
        MRSUser mrsUser = new MRSUser();
        mrsUser.person(mrsPerson);
        when(mockStaffService.searchStaff(staffId, "", "", "", "", "")).thenReturn(Arrays.asList(mrsUser));

        parameters.put(PatientRegistrationFormHandler.FORM_BEAN, editClientForm);
        MotechEvent event = new MotechEvent("subject", parameters);

        assertResult(facilityId, country, region, district, state, first, middle, last, dateOfBirth, address, patientId, nhisNumber, preferredName, sex, phoneNumber, event);
    }

    private void populateFormWithValues(String facilityId, String first, String middle, String last, Date dateOfBirth, String address, String patientId, Date nhisExpDate, String nhisNumber, String parentId, String preferredName, String sex, String phoneNumber, String facilityIdWherePatientWasEdited, String staffId) {
        editClientForm.setMotechId(patientId);
        editClientForm.setStaffId(staffId);
        editClientForm.setAddress(address);
        editClientForm.setDateOfBirth(dateOfBirth);
        editClientForm.setFacilityId(facilityId);
        editClientForm.setFirstName(first);
        editClientForm.setMiddleName(middle);
        editClientForm.setLastName(last);
        editClientForm.setNhisExpires(nhisExpDate);
        editClientForm.setNhis(nhisNumber);
        editClientForm.setMotherMotechId(parentId);
        editClientForm.setPrefferedName(preferredName);
        editClientForm.setSex(sex);
        editClientForm.setPhoneNumber(phoneNumber);
        editClientForm.setUpdatePatientFacilityId(facilityIdWherePatientWasEdited);
    }

    private void assertResult(String facilityId, String country, String region, String district, String state, String first, String middle, String last, Date dateOfBirth, String address, String patientId, String nhisNumber, String preferredName, String sex, String phoneNumber, MotechEvent event) throws ParentNotFoundException {
        ArgumentCaptor<Patient> patientArgumentCaptor = ArgumentCaptor.forClass(Patient.class);
        ArgumentCaptor<PatientType> patientTypeArgumentCaptor = ArgumentCaptor.forClass(PatientType.class);
        ArgumentCaptor<String> motherIdCaptor = ArgumentCaptor.forClass(String.class);
        doReturn(patientId).when(mockPatientService).updatePatient(patientArgumentCaptor.capture(), patientTypeArgumentCaptor.capture(), motherIdCaptor.capture());

        editPatientFormHandler.handleFormEvent(event);

        Patient savedPatient = patientArgumentCaptor.getValue();
        MRSPerson savedPerson = savedPatient.getMrsPatient().getPerson();
        assertThat(savedPerson.getAddress(), is(equalTo(address)));
        assertThat(savedPerson.getDateOfBirth(), is(equalTo(dateOfBirth)));
        assertThat(savedPatient.getMrsPatient().getFacility().getId(), is(equalTo(facilityId)));
        assertThat(savedPatient.getMrsPatient().getFacility().getCountry(), is(equalTo(country)));
        assertThat(savedPatient.getMrsPatient().getFacility().getCountyDistrict(), is(equalTo(district)));
        assertThat(savedPatient.getMrsPatient().getFacility().getRegion(), is(equalTo(region)));
        assertThat(savedPatient.getMrsPatient().getFacility().getStateProvince(), is(equalTo(state)));
        assertThat(savedPerson.getFirstName(), is(equalTo(first)));
        assertThat(savedPerson.getLastName(), is(equalTo(last)));
        assertThat(savedPerson.getMiddleName(), is(equalTo(middle)));
        assertThat(savedPatient.getMrsPatient().getMotechId(), is(equalTo(patientId)));
        assertThat(savedPerson.getPreferredName(), is(equalTo(preferredName)));
        assertThat(savedPerson.getGender(), is(equalTo(sex)));
        assertThat(((Attribute) selectUnique(savedPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.NHIS_NUMBER.getAttribute())))).value(), is(equalTo(nhisNumber)));
        assertThat(((Attribute) selectUnique(savedPerson.getAttributes(), having(on(Attribute.class).name(),
                equalTo(PatientAttributes.PHONE_NUMBER.getAttribute())))).value(), is(equalTo(phoneNumber)));

        assertThat(motherIdCaptor.getValue(), is(equalTo(editClientForm.getMotherMotechId())));
    }

    @Test
    public void shouldBeRegisteredAsAListenerForRegisterPatientEvent() throws NoSuchMethodException {
        String[] registeredEventSubject = editPatientFormHandler.getClass().getMethod("handleFormEvent", new Class[]{MotechEvent.class}).getAnnotation(MotechListener.class).subjects();
        assertThat(registeredEventSubject, is(equalTo(new String[]{"form.validation.successful.NurseDataEntry.editPatient"})));
    }

    @Test
    public void shouldRunAsAdminUser() throws NoSuchMethodException {
        assertThat(editPatientFormHandler.getClass().getMethod("handleFormEvent", new Class[]{MotechEvent.class}).getAnnotation(LoginAsAdmin.class), is(not(equalTo(null))));
    }


}
