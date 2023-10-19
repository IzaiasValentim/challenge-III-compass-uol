package br.izaias.valentim.msimprovements.utils;

import br.izaias.valentim.msimprovements.entities.Improvement;
import br.izaias.valentim.msimprovements.services.ImprovementService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ManageSectionOfVotes {
    private final long timeDefaultToVote = 1000 * 60;

    private final ImprovementService improvementService;

    public ManageSectionOfVotes(@Lazy ImprovementService improvementService) {
        this.improvementService = improvementService;
    }

    @Async
    public void execute(Improvement improvementCreated, Long TimeSessionOfVotes) throws InterruptedException {
        if(TimeSessionOfVotes == 0)
            TimeSessionOfVotes = timeDefaultToVote;

        System.out.println("Starded: " + improvementCreated.getName());
        Thread.sleep(TimeSessionOfVotes);
        Improvement improvementClosedSection = improvementService.closeImprovementSessionOfVote(improvementCreated);

        System.out.println("Closed - processing the voutes!: " + improvementCreated.getName());

        improvementService.processingOfVotingSection(improvementClosedSection.getId());

        System.out.println("Processed!: " + improvementCreated.getName());

    }
}
