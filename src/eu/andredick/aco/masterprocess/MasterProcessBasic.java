package eu.andredick.aco.masterprocess;

import eu.andredick.aco.ant.AbstractAnt;
import eu.andredick.aco.pheromoneassociation.AbstractPheromoneAssociation;
import eu.andredick.aco.termination.AbstractTerminationCriterion;

/**
 * <b>Masterprozess-Basic</b> - Grundausprägung der Komponente des Masterprozesses<br>
 * Kapitel 3.3.2, S. 26, Masterprozess<br>
 * <br>
 * Die Implementierung des Masterprozess bildet den übergeordneten Ablauf der ACO-Metaheuristik ab,<br>
 * indem die Initiirung und Evaporation des Pheromons (siehe {@link AbstractPheromoneAssociation})<br>
 * und die Population der Ameisen (siehe {@link AbstractAnt}) koordiniert wird.<br>
 * <br>
 * <b>Ablauf:</b><br>
 * 1 - Initiierung des Pheromons<br>
 * 2 - Konstruktion der Lösungen aller Ameisen<br>
 * 3 - Lokale Suche auf konstruierten Lösungen aller Ameisen<br>
 * 4 - Evaporation des Pheromons<br>
 * 5 - Markierung des Pheromons durch <b>alle</b> Ameisen<br>
 * 6 - Zurücksetzen der Ameisengedächtnisse<br>
 * 7 - Zurück zu 2., wenn Abbruchbedingungen nicht erfüllt.<br>
 * <br>
 * Ein Masterprozess wird im {@link eu.andredick.aco.algorithm.ACOAlgorithm} verwendet und dort gestartet.
 * <p><img src="{@docRoot}/images/Masterprocess-a.svg" alt=""></p>
 * <hr>
 * <p><img src="{@docRoot}/images/Masterprocess-b.svg" alt=""></p>
 */
public class MasterProcessBasic extends AbstractMasterProcess {

    /**
     * Konstruktor
     *
     * @param pheromoneStructure Pheromonassoziation mit dem zu lösnden AbstractProblem
     * @param ants               Population der Ameisen
     * @param termCriterion      Abbruchkriterium für die Iteration
     */
    public MasterProcessBasic(AbstractPheromoneAssociation pheromoneStructure, AbstractAnt[] ants, AbstractTerminationCriterion termCriterion) {
        super(pheromoneStructure, ants, termCriterion);
    }

    /**
     * <b>Logik des Masterprozeess-Basic</b><br>
     * <br>
     * <b>Ablauf:</b><br>
     * 1 - Initiierung des Pheromons<br>
     * 2 - Konstruktion der Lösungen aller Ameisen<br>
     * 3 - Lokale Suche auf konstruierten Lösungen aller Ameisen<br>
     * 4 - Evaporation des Pheromons<br>
     * 5 - Markierung des Pheromons durch alle Ameisen<br>
     * 6 - Zurücksetzen der Ameisengedächtnisse<br>
     * 7 - Zurück zu 2., wenn Abbruchbedingungen nicht erfüllt.<br>
     */
    @Override
    public void start() {

        // Initiierung der Pheromone
        this.pheromoneStructure.initPheromones();

        // Iteration bis Abbruchkriterum erfüllt
        for (int iteration = 0; termCriterion.checkTermination(iteration, statistics); iteration++) {

            // Iteration über alle Ameisen
            for (AbstractAnt ant : this.ants) {
                // Konstruiere eine Lösung
                ant.constructSolution();
                // Lokale Suche auf der konstruierten Lösung
                ant.localSearch();
                // Zielfunktionswert
                float value = ant.evaluateSolution();
                // Aktualisiere Statistiken
                this.statistics.setValue(iteration, value, ant.getSolution());
            }

            // Evaporation der Pheromone
            this.pheromoneStructure.evaporatePheromones();

            // Pheromon-Update durch alle Ameisen
            for (AbstractAnt a : this.ants) {
                a.markPheromone();
                a.resetAnt();
            }

            System.out.println("MasterProcessBasic... Interation: " + iteration);
        }

    }
}
