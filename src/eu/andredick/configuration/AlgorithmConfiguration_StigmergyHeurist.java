package eu.andredick.configuration;

import eu.andredick.aco.algorithm.ACOAlgorithm;
import eu.andredick.aco.algorithm.ACOAnt;
import eu.andredick.aco.algorithm.AbstractAlgorithm;
import eu.andredick.aco.algorithm.AbstractAnt;
import eu.andredick.aco.combination.CombinationFactor;
import eu.andredick.aco.construct.AbstractConstructionStrategy;
import eu.andredick.aco.construct.ConstructionFromSubsets;
import eu.andredick.aco.heuristic.HeuristicInfoSet;
import eu.andredick.aco.heuristic.HeuristicRuleBestSubset;
import eu.andredick.aco.heuristic.HeuristicRuleWeights;
import eu.andredick.aco.localsearch.AbstractLocalSearchStrategy;
import eu.andredick.aco.localsearch.LocalSearchStrategyNone;
import eu.andredick.aco.masterprocess.AbstractMasterProcess;
import eu.andredick.aco.masterprocess.MasterProcessBasicParallel;
import eu.andredick.aco.nextstep.AbstractNextStepRule;
import eu.andredick.aco.nextstep.NextStepRuleOnSubsetsStochastic;
import eu.andredick.aco.pheromonassociation.PheromoneOnSubsets;
import eu.andredick.aco.pheromoneevaporation.AbstractPheromoneEvaporation;
import eu.andredick.aco.pheromoneevaporation.PheromoneEvaporation;
import eu.andredick.aco.pheromoneinit.AbstractPheromoneInit;
import eu.andredick.aco.pheromoneinit.PheromoneInit;
import eu.andredick.aco.pheromoneperception.AbstractPheromonePerception;
import eu.andredick.aco.pheromoneperception.PerceptionSimple;
import eu.andredick.aco.pheromoneupdate.AbstractPheromoneUpdate;
import eu.andredick.aco.pheromoneupdate.PheromoneUpdateOnSubsets;
import eu.andredick.aco.solutionquality.SolutionQualityMin;
import eu.andredick.aco.termination.AbstractTermCriterion;
import eu.andredick.aco.termination.TermCriterionNew;
import eu.andredick.scp.SCProblem;

public class AlgorithmConfiguration_StigmergyHeurist extends AbstractAlgorithmConfiguration {


    @Override
    public void prepareConfigParameters() {

        this.configName = "AlgorithmConfiguration_StigmergyHeurist";

        ConfigurationParameter<Float> phInitValue =
                new ConfigurationParameter<>("pheromonInitValue", 1f);
        this.addConfigurationParameter(phInitValue);
        //phInitValue.setExpressions(new Float[]{1f,10f,20f,30f,40f,50f,60f,70f,80f,90f});
        //phInitValue.setExpressions(new Float[]{50f, 100f, 150f, 200f});

        ConfigurationParameter<Float> alpha =
                new ConfigurationParameter<>("alpha", 1f);
        this.addConfigurationParameter(alpha);

        ConfigurationParameter<Float> beta =
                new ConfigurationParameter<>("beta", 2f);
        this.addConfigurationParameter(beta);
        //beta.setExpressions(new Float[]{1f,2f,3f,4f,5f});

        ConfigurationParameter<Float> evapFactor =
                new ConfigurationParameter<>("evaporationFactor", 0.5f);
        this.addConfigurationParameter(evapFactor);
        evapFactor.setExpressions(new Float[]{0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f});
        //evapFactor.setExpressions(new Float[]{0.1f,0.3f,0.5f,0.7f,0.9f});

        ConfigurationParameter<Integer> maxIterations =
                new ConfigurationParameter<>("maxIterations", 400);
        this.addConfigurationParameter(maxIterations);

        this.getParameter("antsize").setCurrentValue(10);

    }

    @Override
    public AbstractAlgorithm create(SCProblem problem) {

        PheromoneOnSubsets pheromoneStructure =
                new PheromoneOnSubsets(problem.getStructure());

        float phInitValue = this.getParameter("pheromonInitValue").getCurrentValue().floatValue();
        AbstractPheromoneInit pheromoneInitRule =
                new PheromoneInit(phInitValue);

        float evapFactor = this.getParameter("evaporationFactor").getCurrentValue().floatValue();
        AbstractPheromoneEvaporation evaporationRule =
                new PheromoneEvaporation(evapFactor);

        pheromoneStructure.setEvaporationRule(evaporationRule);
        pheromoneStructure.setPheromoneInitRule(pheromoneInitRule);

        AbstractPheromoneUpdate updateRule =
                new PheromoneUpdateOnSubsets(pheromoneStructure, new SolutionQualityMin());

        float alpha = this.getParameter("alpha").getCurrentValue().floatValue();
        float beta = this.getParameter("beta").getCurrentValue().floatValue();

        HeuristicInfoSet heuristicInfoSet = new HeuristicInfoSet();
        heuristicInfoSet.addRule(new HeuristicRuleWeights());
        heuristicInfoSet.addRule(new HeuristicRuleBestSubset());

        AbstractPheromonePerception perceptionRule = new PerceptionSimple();

        AbstractNextStepRule nextStepRule =
                new NextStepRuleOnSubsetsStochastic(pheromoneStructure, perceptionRule, heuristicInfoSet, new CombinationFactor(alpha, beta));

        AbstractConstructionStrategy constructionStrategy =
                new ConstructionFromSubsets(nextStepRule);

        AbstractLocalSearchStrategy localSearchStrategy =
                new LocalSearchStrategyNone();

        int antSize = this.getParameter("antsize").getCurrentValue().intValue();
        AbstractAnt[] ants = new AbstractAnt[antSize];
        for (int i = 0; i < ants.length; i++) {
            ants[i] = new ACOAnt(problem, updateRule, constructionStrategy, localSearchStrategy);
        }

        AbstractTermCriterion termCriterion =
                new TermCriterionNew(this.getParameter("maxIterations").getCurrentValue().intValue(), 300);

        AbstractMasterProcess masterProcess = new MasterProcessBasicParallel(pheromoneStructure, ants, termCriterion);

        return new ACOAlgorithm(masterProcess);
    }

}
