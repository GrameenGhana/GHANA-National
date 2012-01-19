package org.motechproject.functional.data;

import org.motechproject.functional.util.DataGenerator;
import org.motechproject.ghana.national.domain.mobilemidwife.*;

public class TestMobileMidwifeEnrollment {
    private String staffId;
    private String facilityId;
    private Boolean consent;
    private ServiceType serviceType;
    private PhoneOwnership phoneOwnership;
    private String phoneNumber;
    private Medium medium;
    private Language language;
    private LearnedFrom learnedFrom;
    private ReasonToJoin reasonToJoin;
    private MessageStartWeek messageStartWeek;

    public static TestMobileMidwifeEnrollment with(String staffId, String facilityId){
        TestMobileMidwifeEnrollment testMobileMidwifeEnrollment = new TestMobileMidwifeEnrollment();
        testMobileMidwifeEnrollment.staffId = staffId;
        testMobileMidwifeEnrollment.facilityId = facilityId;
        testMobileMidwifeEnrollment.consent = Boolean.TRUE;
        testMobileMidwifeEnrollment.serviceType = ServiceType.PREGNANCY;
        testMobileMidwifeEnrollment.phoneNumber = new DataGenerator().randomPhoneNumber();
        testMobileMidwifeEnrollment.phoneOwnership = PhoneOwnership.PERSONAL;
        testMobileMidwifeEnrollment.medium = Medium.SMS;
        testMobileMidwifeEnrollment.language = Language.EN;
        testMobileMidwifeEnrollment.learnedFrom = LearnedFrom.MOTECH_FIELD_AGENT;
        testMobileMidwifeEnrollment.reasonToJoin = ReasonToJoin.KNOW_MORE_PREGNANCY_CHILDBIRTH;
        testMobileMidwifeEnrollment.messageStartWeek = new MessageStartWeek("10", "Pregnancy-week 10", ServiceType.PREGNANCY);
        return testMobileMidwifeEnrollment;
    }

    public String staffId() {
        return staffId;
    }

    public String facilityId() {
        return facilityId;
    }

    public Boolean consent() {
        return consent;
    }

    public String serviceType() {
        return serviceType.getValue();
    }

    public String phoneOwnership() {
        return phoneOwnership.getValue();
    }

    public String phoneNumber() {
        return phoneNumber;
    }

    public String medium() {
        return medium.getValue();
    }

    public String language() {
        return language.getValue();
    }

    public String learnedFrom() {
        return learnedFrom.getValue();
    }

    public String reasonToJoin() {
        return reasonToJoin.getValue();
    }

    public String messageStartWeek() {
        return messageStartWeek.getValue();
    }

    public TestMobileMidwifeEnrollment withServiceType(ServiceType serviceType){
        this.serviceType = serviceType;
        return this;
    }

    public TestMobileMidwifeEnrollment withMessageStartWeek(MessageStartWeek messageStartWeek){
        this.messageStartWeek = messageStartWeek;
        return this;
    }

    public TestMobileMidwifeEnrollment staffId(String staffId) {
        this.staffId = staffId;
        return this;
    }

    public TestMobileMidwifeEnrollment facilityId(String facilityId) {
        this.facilityId = facilityId;
        return this;
    }

    public TestMobileMidwifeEnrollment consent(Boolean consent) {
        this.consent = consent;
        return this;
    }

    public TestMobileMidwifeEnrollment phoneOwnership(String phoneOwnership) {
        this.phoneOwnership = PhoneOwnership.valueOf(phoneOwnership);
        return this;
    }

    public TestMobileMidwifeEnrollment phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public TestMobileMidwifeEnrollment medium(String medium) {
        this.medium = Medium.valueOf(medium);
        return this;
    }

    public TestMobileMidwifeEnrollment language(String language) {
        this.language = Language.valueOf(language);
        return this;
    }

    public TestMobileMidwifeEnrollment learnedFrom(String learnedFrom) {
        this.learnedFrom = LearnedFrom.valueOf(learnedFrom);
        return this;
    }

    public TestMobileMidwifeEnrollment reasonToJoin(String reasonToJoin) {
        this.reasonToJoin = ReasonToJoin.valueOf(reasonToJoin);
        return this;
    }

    public TestMobileMidwifeEnrollment serviceType(String serviceType) {
        this.serviceType = ServiceType.valueOf(serviceType);
        return this;
    }

    public TestMobileMidwifeEnrollment messageStartWeek(MessageStartWeek messageStartWeek) {
        this.messageStartWeek = messageStartWeek;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestMobileMidwifeEnrollment)) return false;

        TestMobileMidwifeEnrollment that = (TestMobileMidwifeEnrollment) o;

        if (consent != null ? !consent.equals(that.consent) : that.consent != null) return false;
        if (facilityId != null ? !facilityId.equals(that.facilityId) : that.facilityId != null) return false;
        if (language != that.language) return false;
        if (learnedFrom != that.learnedFrom) return false;
        if (medium != that.medium) return false;
        if (messageStartWeek != null ? !messageStartWeek.equals(that.messageStartWeek) : that.messageStartWeek != null)
            return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null) return false;
        if (phoneOwnership != that.phoneOwnership) return false;
        if (reasonToJoin != that.reasonToJoin) return false;
        if (serviceType != that.serviceType) return false;
        if (staffId != null ? !staffId.equals(that.staffId) : that.staffId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = staffId != null ? staffId.hashCode() : 0;
        result = 31 * result + (facilityId != null ? facilityId.hashCode() : 0);
        result = 31 * result + (consent != null ? consent.hashCode() : 0);
        result = 31 * result + (serviceType != null ? serviceType.hashCode() : 0);
        result = 31 * result + (phoneOwnership != null ? phoneOwnership.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (medium != null ? medium.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        result = 31 * result + (learnedFrom != null ? learnedFrom.hashCode() : 0);
        result = 31 * result + (reasonToJoin != null ? reasonToJoin.hashCode() : 0);
        result = 31 * result + (messageStartWeek != null ? messageStartWeek.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestMobileMidwifeEnrollment{" +
                "staffId='" + staffId + '\'' +
                ", facilityId='" + facilityId + '\'' +
                ", consent=" + consent +
                ", serviceType=" + serviceType +
                ", phoneOwnership=" + phoneOwnership +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", medium=" + medium +
                ", language=" + language +
                ", learnedFrom=" + learnedFrom +
                ", reasonToJoin=" + reasonToJoin +
                ", messageStartWeek=" + messageStartWeek +
                '}';
    }
}