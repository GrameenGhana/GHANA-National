package org.motechproject.ghana.national.tools.seed.data.source;

import org.motechproject.ghana.national.tools.seed.data.domain.UpcomingSchedule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OldGhanaScheduleSource extends BaseSeedSource {

    public List<UpcomingSchedule> getUpcomingTTSchedules() {
        return querySchedulesFromDb("select patient_id, concept_id as type, care_name , min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'TT' and care_name <> 'TT1' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingIPTSchedules() {
        return querySchedulesFromDb("select patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'IPTI' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingIPTPSchedules() {
        return querySchedulesFromDb("select patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'IPT' order by patient_id");
    }

    public List<UpcomingSchedule> getEDDSchedules() {
        return querySchedulesFromDb("select patient_id, encounter_type as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_encounter where voided = 0 and group_name = 'EDD' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingBCGSchedules() {
        return querySchedulesFromDb("select patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'BCG' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingMeaslesSchedules() {
        return querySchedulesFromDb("select patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'Measles' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingPentaSchedules() {
        return querySchedulesFromDb("select patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'Penta' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingYellowFeverSchedules() {
        return querySchedulesFromDb("select patient_id, concept_id as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_obs where voided = 0 and group_name = 'YellowFever' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingPNCMotherSchedules() {
        return querySchedulesFromDb("select patient_id, encounter_type as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_encounter where voided = 0 and group_name = 'PNC(mother)' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingPNCBabySchedules() {
        return querySchedulesFromDb("select patient_id, encounter_type as type, care_name, min_datetime, due_datetime, late_datetime, max_datetime, voided from motechmodule_expected_encounter where voided = 0 and group_name = 'PNC(baby)' order by patient_id");
    }

    public List<UpcomingSchedule> getUpcomingANCVisitSchedules() {
        return querySchedulesFromDb("SELECT mi.patient_id, mi.encounter_type as type, mi.care_name, mi.min_datetime, mi.due_datetime, mi.late_datetime, mi.max_datetime, mi.voided FROM (SELECT MAX(motechmodule_expected_encounter_id) AS mid FROM motechmodule_expected_encounter WHERE group_name = 'ANC' and voided = 0 GROUP BY patient_id) mo JOIN motechmodule_expected_encounter mi ON mi.motechmodule_expected_encounter_id = mo.mid");
    }

    private List<UpcomingSchedule> querySchedulesFromDb(String query) {
        return jdbcTemplate.query(query,
                new UpcomingScheduleRowMapper());
    }
}
