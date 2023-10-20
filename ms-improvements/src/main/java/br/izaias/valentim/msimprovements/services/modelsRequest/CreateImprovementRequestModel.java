package br.izaias.valentim.msimprovements.services.modelsRequest;

import br.izaias.valentim.msimprovements.entities.Improvement;

public class CreateImprovementRequestModel {
    private Improvement improvementToSave;
    private Long TimeSessionOfVotes;

    public Improvement getImprovementToSave() {
        return improvementToSave;
    }

    public void setImprovementToSave(Improvement improvementToSave) {
        this.improvementToSave = improvementToSave;
    }

    public Long getTimeSessionOfVotes() {
        return TimeSessionOfVotes;
    }

    public void setTimeSessionOfVotes(Long timeSessionOfVotes) {
        TimeSessionOfVotes = timeSessionOfVotes;
    }
}
