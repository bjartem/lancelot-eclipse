package no.nr.lancelot.rulebook;

import java.util.Set;

public interface IRulebook {
    Set<Rule> findViolations(final MethodIdea methodIdea);
}