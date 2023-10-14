package br.izaias.valentim.msimprovements.utils;

import br.izaias.valentim.msimprovements.entities.Improvement;
import br.izaias.valentim.msimprovements.services.ImprovementService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SchedulingImprovementsVoute {
    //2 minutes to vote
    private final long timeToVote = 1000 * 15;

    private final ImprovementService service;

    public SchedulingImprovementsVoute(@Lazy ImprovementService service) {
        this.service = service;
    }

    @Async
    public void execute(Improvement improvement) throws InterruptedException {
        System.out.println("iniciado: " + improvement.getName());
        Thread.sleep(timeToVote);
        service.closeImprovementSessionOfVote(improvement);
        System.out.println("fechado: " + improvement.getName());
    }
}
