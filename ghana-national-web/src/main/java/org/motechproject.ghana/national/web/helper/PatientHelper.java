package org.motechproject.ghana.national.web.helper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientAttributes;
import org.motechproject.ghana.national.web.form.PatientForm;
import org.motechproject.mrs.model.Attribute;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.model.MRSPerson;
import org.openmrs.patient.UnallowedIdentifierException;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("patientControllerHelper")
public class PatientHelper {

    static DateFormat dateFormat = new SimpleDateFormat(Constants.PATTERN_YYYY_MM_DD);

    public Patient getPatientVO(PatientForm createPatientForm) throws UnallowedIdentifierException {
        List<Attribute> attributes = new ArrayList<Attribute>();
        setAttribute(attributes, createPatientForm.getPhoneNumber(), PatientAttributes.PHONE_NUMBER);
        setAttribute(attributes, createPatientForm.getNhisNumber(), PatientAttributes.NHIS_NUMBER);
        Date nhisExpirationDate = createPatientForm.getNhisExpirationDate();
        if (nhisExpirationDate != null) {
            setAttribute(attributes, new SimpleDateFormat(Constants.PATTERN_YYYY_MM_DD).format(nhisExpirationDate), PatientAttributes.NHIS_EXPIRY_DATE);
        }
        setAttribute(attributes, String.valueOf(createPatientForm.getInsured()), PatientAttributes.INSURED);

        MRSPerson mrsPerson = new MRSPerson().firstName(createPatientForm.getFirstName()).middleName(createPatientForm.getMiddleName())
                .lastName(createPatientForm.getLastName()).preferredName(createPatientForm.getPreferredName()).dateOfBirth(createPatientForm.getDateOfBirth())
                .birthDateEstimated(createPatientForm.getEstimatedDateOfBirth()).gender(createPatientForm.getSex()).address(createPatientForm.getAddress()).attributes(attributes);
        MRSPatient mrsPatient = new MRSPatient(createPatientForm.getMotechId(), mrsPerson, new MRSFacility(createPatientForm.getFacilityId()));

        return new Patient(mrsPatient);
    }

    private void setAttribute(List<Attribute> attributes, String attributeValue, PatientAttributes patientAttribute) {
        if (StringUtils.isNotEmpty(attributeValue)) {
            attributes.add(new Attribute(patientAttribute.getAttribute(), attributeValue));
        }
    }

    public PatientForm getPatientForm(Patient patientVO) throws ParseException {
        PatientForm createPatientForm = new PatientForm();
        MRSPerson mrsPerson = patientVO.mrsPatient().getPerson();
        MRSFacility mrsFacility = patientVO.mrsPatient().getFacility();
        createPatientForm.setAddress(mrsPerson.getAddress());
        createPatientForm.setDateOfBirth(mrsPerson.getDateOfBirth());
        createPatientForm.setEstimatedDateOfBirth(mrsPerson.getBirthDateEstimated());
        createPatientForm.setFacilityId(mrsFacility.getId());
        createPatientForm.setRegion(mrsFacility.getRegion());
        createPatientForm.setDistrict(mrsFacility.getCountyDistrict());
        createPatientForm.setSubDistrict(mrsFacility.getStateProvince());
        createPatientForm.setFirstName(mrsPerson.getFirstName());
        createPatientForm.setLastName(mrsPerson.getLastName());
        createPatientForm.setMiddleName(mrsPerson.getMiddleName());
        createPatientForm.setPreferredName(mrsPerson.getPreferredName());
        createPatientForm.setMotechId(patientVO.mrsPatient().getMotechId());
        createPatientForm.setSex(mrsPerson.getGender());

        String insured = mrsPerson.attrValue(PatientAttributes.INSURED.getAttribute());
        if (insured != null) {
            createPatientForm.setInsured(Boolean.valueOf(insured));
        }

        String phoneNumber = mrsPerson.attrValue(PatientAttributes.PHONE_NUMBER.getAttribute());
        if (phoneNumber != null) {
            createPatientForm.setPhoneNumber(phoneNumber);
        }

        String nhisExpiryDate = mrsPerson.attrValue(PatientAttributes.NHIS_EXPIRY_DATE.getAttribute());
        if (nhisExpiryDate != null) {
            createPatientForm.setNhisExpirationDate(dateFormat.parse(nhisExpiryDate));
        }

        String nhisNumber = mrsPerson.attrValue(PatientAttributes.NHIS_NUMBER.getAttribute());
        if (nhisNumber != null) {
            createPatientForm.setNhisNumber(nhisNumber);
        }
        return createPatientForm;
    }
}
