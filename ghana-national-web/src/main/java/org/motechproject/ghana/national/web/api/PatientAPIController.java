/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.motechproject.ghana.national.web.api;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringUtils;
import org.motechproject.ghana.national.bean.RegisterClientForm;
import org.motechproject.ghana.national.domain.Constants;
import org.motechproject.ghana.national.domain.Facility;
import org.motechproject.ghana.national.domain.Patient;
import org.motechproject.ghana.national.domain.PatientType;
import org.motechproject.ghana.national.domain.RegistrationType;
import org.motechproject.ghana.national.exception.ParentNotFoundException;
import org.motechproject.ghana.national.exception.PatientIdIncorrectFormatException;
import org.motechproject.ghana.national.exception.PatientIdNotUniqueException;
import org.motechproject.ghana.national.exception.StaffNotFoundException;
import org.motechproject.ghana.national.exception.XFormHandlerException;
import org.motechproject.ghana.national.handlers.PatientRegistrationFormHandler;
import org.motechproject.ghana.national.service.FacilityService;
import org.motechproject.ghana.national.service.PatientService;
import org.motechproject.ghana.national.service.StaffService;
import org.motechproject.ghana.national.validator.DependentValidator;
import org.motechproject.ghana.national.validator.RegisterClientFormValidator;
import org.motechproject.ghana.national.validator.patient.PatientValidator;
import static org.motechproject.ghana.national.web.PatientController.NEW_PATIENT_URL;
import static org.motechproject.ghana.national.web.PatientController.SUCCESS;
import org.motechproject.ghana.national.web.form.PatientForm;
import org.motechproject.ghana.national.web.helper.FacilityHelper;
import org.motechproject.ghana.national.web.helper.PatientHelper;
import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.openmrs.omod.validator.MotechIdVerhoeffValidator;
import org.motechproject.util.DateUtil;
import org.openmrs.api.context.Context;
import org.openmrs.patient.UnallowedIdentifierException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author liman
 */
@Controller
public class PatientAPIController {

    @Autowired
    private FacilityHelper facilityHelper;
    @Autowired
    private PatientService patientService;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private RegisterClientFormValidator registerClientFormValidator;
    @Autowired
    private StaffService staffService;
    @Autowired
    private MotechIdVerhoeffValidator motechIdVerhoeffValidator;
    @Autowired
    private PatientHelper patientHelper;
    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/registerPatient", method = RequestMethod.POST)
    public @ResponseBody
    String registerPatient(@RequestParam(value = "staffId", required = true) String staffId,
            @RequestParam(value = "motechId") String motechId,
            @RequestParam(value = "facilityId", required = true) String facilityId,
            @RequestParam(value = "regMode", required = true) String regMode,
            @RequestParam(value = "clientType", required = true) String clientType,
            @RequestParam(value = "firstName", required = true) String firstName,
            @RequestParam(value = "middleName") String middleName,
            @RequestParam(value = "lastName", required = true) String lastName,
            @RequestParam(value = "dob", required = true) String dob,
            @RequestParam(value = "sex", required = true) String sex,
            @RequestParam(value = "estimatedDob", required = true) String estimatedDob,
            @RequestParam(value = "nhisNumber") String nhisNumber,
            @RequestParam(value = "nhisExpiryDate") String nhisExpiryDate,
            @RequestParam(value = "region", required = true) String region,
            @RequestParam(value = "address", required = true) String address,
            @RequestParam(value = "phoneNumber", required = true) String phoneNumber,
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password) throws Exception {

        System.out.println("API Request :: Registeration of Patient ---  " + firstName + " " + middleName + " " + lastName);

        try {
            Context.openSession();
            Context.authenticate(username, password);

            JsonObject result = new JsonObject();

            PatientForm patient = new PatientForm();
            patient.setStaffId(staffId);
            patient.setFacilityId(facilityId);

            //Patient Type
            if (clientType.equalsIgnoreCase("PREGNANT_MOTHER")) {
                patient.setTypeOfPatient(PatientType.PREGNANT_MOTHER);
            }

            if (clientType.equalsIgnoreCase("CHILD_UNDER_FIVE")) {
                patient.setTypeOfPatient(PatientType.CHILD_UNDER_FIVE);
            }

            if (clientType.equalsIgnoreCase("OTHER")) {
                patient.setTypeOfPatient(PatientType.OTHER);
            }

            if (clientType.equalsIgnoreCase("MOTHER_OF_INFANT")) {
                patient.setTypeOfPatient(PatientType.MOTHER_OF_INFANT);
            }

            patient.setFirstName(firstName);
            patient.setMiddleName(middleName);
            patient.setLastName(lastName);
            patient.setSex(sex);

            //RegMode
            if (regMode.equalsIgnoreCase("AUTO_GENERATED_ID")) {
                patient.setRegistrationMode(RegistrationType.AUTO_GENERATE_ID);
            } else if (regMode.equalsIgnoreCase("USE_PREPRINTED_ID")) {
                patient.setRegistrationMode(RegistrationType.USE_PREPRINTED_ID);
            }

            SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
            patient.setDateOfBirth(formatter.parse(dob));

            //Estimated Dob
            if (estimatedDob.equalsIgnoreCase("TRUE") || estimatedDob.equalsIgnoreCase("T")) {
                patient.setEstimatedDateOfBirth(Boolean.TRUE);
            }

            if (estimatedDob.equalsIgnoreCase("FALSE") || estimatedDob.equalsIgnoreCase("F")) {
                patient.setEstimatedDateOfBirth(Boolean.FALSE);
            }

            patient.setNhisNumber(nhisNumber);
            patient.setNhisExpirationDate(formatter.parse(nhisExpiryDate));
            patient.setRegion(region);
            patient.setAddress(address);
            patient.setPhoneNumber(phoneNumber);

            Facility facility = facilityService.getFacility(patient.getFacilityId());
            Patient patientInDb = null;
            if (patient.getMotechId() != null) {
                patientInDb = patientService.getPatientByMotechId(patient.getMotechId());
            }

            PatientValidator validators = registerClientFormValidator.patientValidator(patient, patient.getRegistrationMode(), patient.getDateOfBirth(), patient.getSex(), patient.getTypeOfPatient(), patient.getParentId());
            List<FormError> formErrors = new DependentValidator().validate(patientInDb, Collections.<FormBean>emptyList(), Collections.<FormBean>emptyList(), validators);

            if (formErrors.isEmpty()) {
                try {
                    String staff = patient.getStaffId();
                    validateStaffId(staff);
                    if (patient.getRegistrationMode().equals(RegistrationType.USE_PREPRINTED_ID)) {
                        if (!motechIdVerhoeffValidator.isValid(patient.getMotechId())) {
                            throw new UnallowedIdentifierException("User Id is not allowed");
                        }
                    }
                    Patient patientFound = patientService.registerPatient(patientHelper.getPatientVO(patient, facility), staff, DateUtil.today().toDate());
                    if (StringUtils.isNotEmpty(patientFound.getMotechId())) {

                        result.addProperty("rc", "00");
                        result.addProperty("msg", "Patient created successfully , Motech Id is " + patientFound.getMotechId());

                    }
                } catch (ParentNotFoundException e) {

                    result.addProperty("rc", "01");
                    result.addProperty("msg", messageSource.getMessage("patient_parent_not_found", null, Locale.getDefault()));

                } catch (PatientIdNotUniqueException e) {

                    result.addProperty("rc", "02");
                    result.addProperty("msg", messageSource.getMessage("patient_id_duplicate", null, Locale.getDefault()));

                } catch (PatientIdIncorrectFormatException e) {

                    result.addProperty("rc", "03");
                    result.addProperty("msg", messageSource.getMessage("patient_id_incorrect", null, Locale.getDefault()));

                } catch (UnallowedIdentifierException e) {

                    result.addProperty("rc", "04");
                    result.addProperty("msg", messageSource.getMessage("patient_id_incorrect", null, Locale.getDefault()));

                } catch (StaffNotFoundException e) {

                    result.addProperty("rc", "05");
                    result.addProperty("msg", messageSource.getMessage("staff_id_not_found", null, Locale.getDefault()));

                } catch (NullPointerException e) {

                    result.addProperty("rc", "09");
                    result.addProperty("msg", "Server Error");
                }

                return result.toString();
            } else {
                // formErrors
                result.addProperty("rc", "10");
                result.addProperty("msg", formErrors.toString());
                return result.toString();
            }

        } finally {
            Context.closeSession();
        }

    }

    private void validateStaffId(String staffId) throws StaffNotFoundException {
        if (StringUtils.isNotEmpty(staffId) && (staffService.getUserByEmailIdOrMotechId(staffId) == null)) {
            throw new StaffNotFoundException();
        }
    }

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/registerP", method = RequestMethod.GET)
    public @ResponseBody
    String registerP(@RequestParam(value = "staffId", required = true) String staffId,
            @RequestParam(value = "motechId") String motechId,
            @RequestParam(value = "facilityId", required = true) String facilityId,
            @RequestParam(value = "regMode", required = true) String regMode,
            @RequestParam(value = "clientType", required = true) String clientType,
            @RequestParam(value = "firstName", required = true) String firstName,
            @RequestParam(value = "middleName", required = true) String middleName,
            @RequestParam(value = "lastName", required = true) String lastName,
            @RequestParam(value = "dob", required = true) String dob,
            @RequestParam(value = "sex", required = true) String sex,
            @RequestParam(value = "address", required = true) String address,
            @RequestParam(value = "estimatedDob", required = true) String estimatedDob,
            @RequestParam(value = "phoneNumber", required = true) String phoneNumber,
            @RequestParam(value = "nhisNumber") String nhisNumber,
            @RequestParam(value = "nhisExpiryDate") String nhisExpiryDate,
            @RequestParam(value = "insured") String insured,
            @RequestParam(value = "username", required = true) String username,
            @RequestParam(value = "password", required = true) String password) throws Exception {

        System.out.println("Testing api.........");

        PatientRegistrationFormHandler formhandler = new PatientRegistrationFormHandler();

        try {
            Context.openSession();
            Context.authenticate(username, password);

            RegisterClientForm client = new RegisterClientForm();
            client.setFacilityId(facilityId);
            client.setMotechId(motechId);
            client.setFirstName(firstName);
            client.setMiddleName(middleName);
            client.setLastName(lastName);

            SimpleDateFormat formatter = new SimpleDateFormat(Constants.PATTERN_YYYY_MM_DD);
            client.setDateOfBirth(formatter.parse(dob));

            //Estimated Dob
            if (estimatedDob.equalsIgnoreCase("TRUE") || estimatedDob.equalsIgnoreCase("T")) {
                client.setEstimatedBirthDate(Boolean.TRUE);
            }

            if (estimatedDob.equalsIgnoreCase("FALSE") || estimatedDob.equalsIgnoreCase("F")) {
                client.setEstimatedBirthDate(Boolean.FALSE);
            }
            
            //Patient Type
            if (clientType.equalsIgnoreCase("PREGNANT_MOTHER")) {
                client.setRegistrantType(PatientType.PREGNANT_MOTHER);
            }

            if (clientType.equalsIgnoreCase("CHILD_UNDER_FIVE")) {
                client.setRegistrantType(PatientType.CHILD_UNDER_FIVE);
            }

            if (clientType.equalsIgnoreCase("OTHER")) {
                client.setRegistrantType(PatientType.OTHER);
            }

            if (clientType.equalsIgnoreCase("MOTHER_OF_INFANT")) {
                client.setRegistrantType(PatientType.MOTHER_OF_INFANT);
            }
            
            //RegMode
            if (regMode.equalsIgnoreCase("AUTO_GENERATED_ID")) {
                client.setRegistrationMode(RegistrationType.AUTO_GENERATE_ID);
            } else if (regMode.equalsIgnoreCase("USE_PREPRINTED_ID")) {
                client.setRegistrationMode(RegistrationType.USE_PREPRINTED_ID);
            }


            client.setSex(sex);
            client.setAddress(address);
            client.setPhoneNumber(phoneNumber);

            //logic for insured
            if (insured.equalsIgnoreCase("TRUE") || estimatedDob.equalsIgnoreCase("T")) {
                client.setInsured(Boolean.TRUE);
                
                client.setNhis(nhisNumber);
                client.setNhisExpires(formatter.parse(nhisExpiryDate));
            }

            if (insured.equalsIgnoreCase("FALSE") || estimatedDob.equalsIgnoreCase("F")) {
                client.setInsured(Boolean.FALSE);
            }
            

            formhandler.handleFormEvent(client);

            return "Done:" + client.getMotechId();

        } catch (Exception e) {
            return e.getMessage();

        } finally {
            Context.closeSession();
        }

    }
}
