package org.motechproject.ghana.national.tools.seed.data;

import org.motechproject.decisiontree.model.*;
import org.motechproject.decisiontree.repository.AllTrees;
import org.motechproject.ghana.national.domain.IVRClipManager;
import org.motechproject.ghana.national.domain.ivr.AudioPrompts;
import org.motechproject.ghana.national.domain.ivr.MotechIdValidationTransition;
import org.motechproject.ghana.national.domain.mobilemidwife.Language;
import org.motechproject.ghana.national.tools.seed.Seed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.motechproject.ghana.national.domain.ivr.AudioPrompts.*;
import static org.motechproject.ghana.national.domain.mobilemidwife.Language.*;

@Component("decisionTreeSeed")
public class DecisionTreeSeed extends Seed {
    public static final String NINE_DIGITS = "000000000";
    @Autowired
    AllTrees allTrees;
    @Autowired
    private IVRClipManager ivrClipManager;

    @Override
    protected void load() {
        allTrees.removeAll();
        Tree tree = new Tree();
        tree.setName("mm");
        tree.setRootNode(prompt(LANGUAGE_PROMPT, EN).setTransitions(chooseLanguageTransitions()));
        allTrees.addOrReplace(tree);
    }


    private Map<String, ITransition> chooseLanguageTransitions(){
        Map<String, ITransition> transitions = new HashMap<String, ITransition>();
        transitions.put("1", new Transition().setDestinationNode(prompt(OPTIONS_PROMPT, EN).setTransitions(chooseActionTransition(EN))));
        transitions.put("2", new Transition().setDestinationNode(prompt(OPTIONS_PROMPT, KAS).setTransitions(chooseActionTransition(KAS))));
        transitions.put("3", new Transition().setDestinationNode(prompt(OPTIONS_PROMPT, NAN).setTransitions(chooseActionTransition(NAN))));
        transitions.put("4", new Transition().setDestinationNode(prompt(OPTIONS_PROMPT, FAN).setTransitions(chooseActionTransition(FAN))));
        transitions.put("*", customerCareTransition());
        return transitions;
    }

    private Map<String, ITransition> chooseActionTransition(Language language){
        Map<String, ITransition> transitions = new HashMap<String, ITransition>();
        transitions.put("1", new Transition().setDestinationNode(prompt(MOTECHID_PROMPT, language).setTransitions(validateMotechIdTransition(language))));
        transitions.put("0", customerCareTransition());
        transitions.put("2", customerCareTransition());
        transitions.put("*", customerCareTransition());
        return transitions;
    }

    private Map<String, ITransition> validateMotechIdTransition(Language language) {
        Map<String, ITransition> transitions = new HashMap<String, ITransition>();
        transitions.put("*", new MotechIdValidationTransition());
        return transitions;
    }

    private Node prompt(AudioPrompts clipName, Language language) {
        return new Node().addPrompts(audioPromptFor(clipName, language));
    }


    private Transition customerCareTransition() {
        return new Transition().setDestinationNode(new Node().addPrompts(new TextToSpeechPrompt().setMessage("Redirecting to Customer Care")));
    }

    private AudioPrompt audioPromptFor(AudioPrompts prompt, Language language) {
        return new AudioPrompt().setAudioFileUrl(ivrClipManager.urlFor(prompt.value(), language));
    }
}
