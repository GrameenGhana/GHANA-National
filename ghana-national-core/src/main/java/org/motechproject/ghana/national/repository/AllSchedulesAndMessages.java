package org.motechproject.ghana.national.repository;

import org.joda.time.LocalDate;
import org.motechproject.ghana.national.domain.AggregationMessageIdentifier;
import org.motechproject.ghana.national.messagegateway.service.MessageGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllSchedulesAndMessages {

    private AllSchedules allSchedules;
    private MessageGateway messageGateway;

    @Autowired
    public AllSchedulesAndMessages(AllSchedules allSchedules, MessageGateway messageGateway){
        this.allSchedules = allSchedules;
        this.messageGateway = messageGateway;
    }

    public boolean safeFulfilCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate) {
        messageGateway.delete(new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier());
        return allSchedules.safeFulfilCurrentMilestone(externalId, scheduleName, fulfillmentDate);
    }

    public void fulfilCurrentMilestone(String externalId, String scheduleName, LocalDate fulfillmentDate) {
        messageGateway.delete(new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier());
        allSchedules.fulfilCurrentMilestone(externalId, scheduleName, fulfillmentDate);
    }

    public void unEnroll(String externalId, List<String> scheduleNames) {
        for (String scheduleName : scheduleNames) {
            messageGateway.delete(new AggregationMessageIdentifier(externalId, scheduleName).getIdentifier());
        }
        allSchedules.unEnroll(externalId, scheduleNames);
    }

}