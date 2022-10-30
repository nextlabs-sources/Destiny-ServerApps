package com.nextlabs.authentication.services.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluejungle.framework.domain.IHasId;
import com.bluejungle.framework.expressions.IArguments;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.IExpression;
import com.bluejungle.framework.expressions.IExpressionVisitor;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.IPredicateReference;
import com.bluejungle.framework.expressions.IRelation;
import com.bluejungle.framework.expressions.PredicateConstants;
import com.bluejungle.framework.expressions.Predicates;
import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.pf.destiny.parser.DefaultPQLVisitor;
import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.DomainObjectDescriptor;
import com.bluejungle.pf.destiny.parser.IHasPQL;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.common.IDSpec;
import com.bluejungle.pf.domain.destiny.common.IDSpecRef;
import com.bluejungle.pf.domain.destiny.common.SpecBase;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.bluejungle.pf.domain.epicenter.common.SpecType;
import com.nextlabs.authentication.exceptions.ReferenceResolutionException;
import com.nextlabs.authentication.models.DevelopmentEntity;

/**
 * Delegation rule reference resolver implementation.
 *
 * @author Sachindra Dasun
 */
public class DelegationRuleReferenceResolver {

    private static final Logger logger = LoggerFactory.getLogger(DelegationRuleReferenceResolver.class);

    private final Map<Long, IParsedEntity> daComponentMap = new TreeMap<>();
    private final Map<Long, IParsedEntity> daRuleMap = new TreeMap<>();

    private DelegationRuleReferenceResolver() {
    }


    /**
     * Create delegation reference resolver
     *
     * @param policies   delegation policies
     * @param components delegation components
     * @return created delegation reference resolver
     * @throws PQLException if an error occurred
     */
    public static DelegationRuleReferenceResolver create(List<DevelopmentEntity> policies, List<DevelopmentEntity> components) throws PQLException {
        DelegationRuleReferenceResolver resolver = new DelegationRuleReferenceResolver();
        resolver.loadRuleReferenceData(policies, components);
        return resolver;
    }

    private void loadRuleReferenceData(List<DevelopmentEntity> policies, List<DevelopmentEntity> components) throws PQLException {
        daComponentMap.clear();
        daRuleMap.clear();
        loadDelegationRules(policies);
        loadDelegationComponents(components);
    }

    private void loadDelegationRules(List<DevelopmentEntity> policies) throws PQLException {
        for (final DevelopmentEntity entity : policies) {
            DomainObjectBuilder.processInternalPQL(entity.getPql(), new DefaultPQLVisitor() {
                @Override
                public void visitPolicy(DomainObjectDescriptor descriptor, final IDPolicy policy) {
                    policy.setAccessPolicy(null);
                    IParsedEntity parsed = new IParsedEntity() {
                        // Flag indicates the entity has been resolved.
                        private boolean resolved = false;

                        public IHasId getParsed() {
                            return policy;
                        }

                        public IHasPQL getUnparsed() {
                            return entity;
                        }

                        public String getName() {
                            return "POLICY " + policy.getName();
                        }

                        public void resolveDependencies(Set<Long> currentlyBeingResolved) {
                            if (resolved) {
                                return;
                            }
                            if (!currentlyBeingResolved.isEmpty()) {
                                throw new IllegalStateException("Policies cannot be resolved as dependents of other entities.");
                            }
                            Predicates.ITransformer transformer = new ReferenceResolvingTransformer(currentlyBeingResolved);
                            Optional.ofNullable(policy.getTarget().getActionPred()).ifPresent(predicate ->
                                    policy.getTarget().setActionPred(Optional.ofNullable( Predicates.transform(predicate, transformer))
                                            .orElse(PredicateConstants.TRUE)));
                            Optional.ofNullable(policy.getTarget().getFromResourcePred()).ifPresent(predicate ->
                                    policy.getTarget().setFromResourcePred(Optional.ofNullable(Predicates.transform(predicate, transformer))
                                            .orElse(PredicateConstants.TRUE)));
                            Optional.ofNullable(policy.getTarget().getToResourcePred()).ifPresent(predicate ->
                                    policy.getTarget().setToResourcePred(Optional.ofNullable(Predicates.transform(predicate, transformer))
                                            .orElse(PredicateConstants.TRUE)));
                            Optional.ofNullable(policy.getTarget().getSubjectPred()).ifPresent(predicate ->
                                    policy.getTarget().setSubjectPred(Optional.ofNullable(Predicates.transform(predicate, transformer))
                                            .orElse(PredicateConstants.TRUE)));
                            Optional.ofNullable(policy.getTarget().getToSubjectPred()).ifPresent(predicate ->
                                    policy.getTarget().setToSubjectPred(Optional.ofNullable(Predicates.transform(predicate, transformer))
                                            .orElse(PredicateConstants.TRUE)));
                            Optional.ofNullable(policy.getDeploymentTarget()).ifPresent(predicate ->
                                    policy.setDeploymentTarget(Optional.ofNullable(Predicates.transform(predicate, transformer))
                                            .orElse(PredicateConstants.TRUE)));
                            resolved = true;
                        }
                    };
                    daRuleMap.put(entity.getId(), parsed);
                }
            });
        }
    }

    private void loadDelegationComponents(List<DevelopmentEntity> components) throws PQLException {
        for (final DevelopmentEntity entity : components) {
            DomainObjectBuilder.processInternalPQL(entity.getPql(), new DefaultPQLVisitor() {

                @Override
                public void visitComponent(DomainObjectDescriptor dod, IPredicate predicate) {
                    final SpecBase spec = new SpecBase(null, SpecType.ILLEGAL, dod.getId(), dod.getName(),
                            dod.getDescription(), dod.getStatus(), predicate, dod.isHidden());
                    IParsedEntity parsed = new IParsedEntity() {
                        // Flag indicates the entity has been resolved.
                        private boolean resolved = false;

                        public IHasId getParsed() {
                            return spec;
                        }

                        public IHasPQL getUnparsed() {
                            return entity;
                        }

                        public String getName() {
                            return spec.getSpecType() + " " + spec.getName();
                        }

                        public void resolveDependencies(Set<Long> currentlyBeingResolved) {
                            if (resolved) {
                                return;
                            }
                            if (currentlyBeingResolved.contains(spec.getId())) {
                                throw new IllegalStateException("Detected circular reference while resolving " + spec.getName());
                            }
                            currentlyBeingResolved.add(spec.getId());
                            Predicates.ITransformer transformer = new ReferenceResolvingTransformer(currentlyBeingResolved);
                            Optional.ofNullable(spec.getPredicate()).ifPresent(predicate ->
                                    spec.setPredicate(Optional.ofNullable(Predicates.transform(predicate, transformer))
                                    .orElse(PredicateConstants.TRUE)));
                            currentlyBeingResolved.remove(spec.getId());
                            resolved = true;
                        }
                    };
                    daComponentMap.put(entity.getId(), parsed);
                }
            });
        }
    }

    public List<IDPolicy> resolve() throws ReferenceResolutionException {
        long[] policyIds = new long[daRuleMap.size()];
        IDPolicy[] parsedPolicies = new IDPolicy[daRuleMap.size()];
        int i = 0;
        for (Map.Entry<Long, IParsedEntity> entry : daRuleMap.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalStateException("Policy with null ID is detected.");
            }
            // Policy IDs are expected to be in sorted order because they come from a sorted map.
            policyIds[i] = entry.getKey();
            assert policyIds[i] == 0 || policyIds[i] > policyIds[i - 1];
            IParsedEntity parsed = entry.getValue();
            try {
                parsed.resolveDependencies(new HashSet<>());
                parsedPolicies[i++] = (IDPolicy) parsed.getParsed();
            } catch (ReferenceResolutionException e) {
                logger.error("Policy is not consistent: " + parsed.getName() + " references unknown ID:" + e.getUnresolvedId());
                throw e;
            }
        }
        return Arrays.asList(parsedPolicies);
    }

    /**
     * We use anonymous instances of this interface to store information
     * about policies and specs.
     */
    public interface IParsedEntity {

        /**
         * The parsed entity - a policy or a spec.
         *
         * @return the corresponding parsed entity, which is a policy or a spec.
         */
        IHasId getParsed();

        /**
         * The corresponding unparsed entity - a <code>DeploymentEntity</code> object.
         *
         * @return the corresponding <code>DeploymentEntity</code> object.
         */
        IHasPQL getUnparsed();

        /**
         * The name of the parsed (and unparsed) entity.
         *
         * @return name of the parsed (and unparsed) entity.
         */
        String getName();

        /**
         * Resolve the dependencies for this object. Specs simply resolve their dependencies
         * by ID, while policies resolve dependencies of each of their predicates.
         *
         * @param currentlyBeingResolved a <code>Set</code> containing IDs (of type <code>Long</code>)
         *                               of the objects that are currently being resolved. This <code>Set</code> is used for
         *                               detecting circular references.
         * @throws ReferenceResolutionException when a reference cannot be resolved.
         */
        void resolveDependencies(Set<Long> currentlyBeingResolved);

    }

    /**
     * Instances of this expression class hold references to predicate.
     */
    public static class PredicateReferenceExpression implements IExpression {

        /**
         * The referenced predicate.
         */
        private final IPredicate predicate;

        /**
         * Builds a reference expression for the specific predicate.
         *
         * @param predicate the predicate to which this reference is pointing.
         */
        public PredicateReferenceExpression(IPredicate predicate) {
            if (predicate == null) {
                throw new NullPointerException("predicate");
            }
            this.predicate = predicate;
        }

        public IEvalValue evaluate(IArguments arg) {
            throw new UnsupportedOperationException("IExpression#evaluate(IArguments)");
        }

        public IRelation buildRelation(RelationOp op, IExpression rhs) {
            throw new UnsupportedOperationException("IExpression#buildRelation(RelationOp, IExpression)");
        }

        public void acceptVisitor(IExpressionVisitor visitor, IExpressionVisitor.Order order) {
            visitor.visit(this);
        }

        /**
         * Returns the referenced predicate.
         *
         * @return the referenced predicate.
         */
        public IPredicate getReferencedPredicate() {
            return predicate;
        }

    }

    /**
     * This transformer passes through all non-reference predicates,
     * and replaces all predicate references with the referenced targets.
     */
    class ReferenceResolvingTransformer extends Predicates.DefaultTransformer {

        /**
         * This <code>Set</code> contains IDs of entities currently being resolved.
         */
        private final Set<Long> currentlyBeingResolved;

        /**
         * The constructor supplies the set of entities being resolved.
         *
         * @param currentlyBeingResolved the <code>Set</code> of IDs of entities that are currently being resolved.
         */
        public ReferenceResolvingTransformer(Set<Long> currentlyBeingResolved) {
            this.currentlyBeingResolved = currentlyBeingResolved;
        }

        public IPredicate transformReference(IPredicateReference predicateReference) {
            if (predicateReference instanceof IDSpecRef) {
                IParsedEntity referenced = resolve((IDSpecRef) predicateReference);
                referenced.resolveDependencies(currentlyBeingResolved);
                IHasId spec = referenced.getParsed();
                assert spec != null; // spec comes from the parser; parser does not return nulls.
                if (!(spec instanceof IDSpec)) {
                    throw new IllegalStateException("Unexpected reference: " + spec.getClass());
                }
                return ((IDSpec) spec).getPredicate();
            } else {
                return super.transformReference(predicateReference);
            }
        }

        public IPredicate transformRelation(IRelation rel) {
            return rel;
        }

        /**
         * Resolves spec references; throws exceptions if the reference cannot be resolved.
         *
         * @param ref the reference to resolve.
         * @return the corresponding <code>IParsedEntity</code>.
         */
        private IParsedEntity resolve(IDSpecRef ref) {
            if (ref.isReferenceByName()) {
                throw new IllegalStateException("Reference by name " + ref.getPrintableReference() + " is unexpected.");
            }
            IParsedEntity referenced = daComponentMap.get(ref.getReferencedID());
            if (referenced == null) {
                throw new ReferenceResolutionException(ref.getReferencedID());
            }
            return referenced;
        }

    }

}
