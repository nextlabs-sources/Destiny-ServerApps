/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 29, 2016
 *
 */
package com.nextlabs.destiny.console.policy.pql.helpers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bluejungle.framework.expressions.BooleanOp;
import com.bluejungle.framework.expressions.CompositePredicate;
import com.bluejungle.framework.expressions.Constant;
import com.bluejungle.framework.expressions.DefaultPredicateVisitor;
import com.bluejungle.framework.expressions.ICompositePredicate;
import com.bluejungle.framework.expressions.IExpression;
import com.bluejungle.framework.expressions.IPredicate;
import com.bluejungle.framework.expressions.IPredicateVisitor;
import com.bluejungle.framework.expressions.IRelation;
import com.bluejungle.framework.expressions.PredicateConstants;
import com.bluejungle.framework.expressions.Relation;
import com.bluejungle.framework.expressions.RelationOp;
import com.bluejungle.pf.destiny.formatter.DomainObjectFormatter;
import com.bluejungle.pf.destiny.parser.DefaultPQLVisitor;
import com.bluejungle.pf.destiny.parser.DomainObjectBuilder;
import com.bluejungle.pf.destiny.parser.DomainObjectDescriptor;
import com.bluejungle.pf.destiny.parser.IPQLVisitor;
import com.bluejungle.pf.destiny.parser.PQLException;
import com.bluejungle.pf.domain.destiny.environment.HeartbeatAttribute;
import com.bluejungle.pf.domain.destiny.environment.RemoteAccessAttribute;
import com.bluejungle.pf.domain.destiny.environment.TimeAttribute;
import com.bluejungle.pf.domain.destiny.policy.IDPolicy;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyEnvironmentConfigDTO;
import com.nextlabs.destiny.console.dto.policymgmt.PolicyScheduleConfigDTO;
import com.nextlabs.destiny.console.exceptions.ConsoleException;

/**
 * 
 * Policy Schedule, Environment, recurrence and condition expresion predicate
 * helper to convert to PQL vice versa
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
public class ConditionPredicateHelper {

    private static final Logger log = LoggerFactory
            .getLogger(ConditionPredicateHelper.class);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static Pattern timezonePattern = Pattern.compile("^.*[AP]M\\s(.*)$");

    public static PolicyScheduleConfigDTO getScheduleConfig(IPredicate conditionPredicate) {
        IPredicate predicate = getUIPredicateFromV3(conditionPredicate);
        
        final PolicyScheduleConfigDTO scheduleConfig = new PolicyScheduleConfigDTO();

        predicate.accept(new DefaultPredicateVisitor() {
            @Override
            public void visit(IRelation relation) {
                IExpression lhs = relation.getLHS();
                
                if (lhs instanceof TimeAttribute) {
                    TimeAttribute timeAttr = (TimeAttribute) lhs;
                    String attributeName = timeAttr.getName();
                    RelationOp operator = relation.getOp();
                    String value = ((Constant) relation.getRHS()).toString().replaceAll("\"", "");

                    if ("identity".equals(attributeName)
                            && operator.getName().equals(">=")) {
                        scheduleConfig.setStartDateTime(trimDateTimeTimezone(value));
                        scheduleConfig.setTimezone(getTimezone(value));
                    } else if ("identity".equals(attributeName)
                            && operator.getName().equals("<=")) {
                        scheduleConfig.setEndDateTime(trimDateTimeTimezone(value));
                    } else if ("time".equals(attributeName)
                            && operator.getName().equals(">=")) {
                        scheduleConfig.setRecurrenceStartTime(getPartialTime(value));
                    } else if ("time".equals(attributeName)
                            && operator.getName().equals("<=")) {
                        scheduleConfig.setRecurrenceEndTime(getPartialTime(value));
                    } else if ("day_in_month".equals(attributeName)) {
                        scheduleConfig
                                .setRecurrenceDayInMonth(Long.parseLong(value));
                    } else if ("date".equals(attributeName)) {
                        scheduleConfig
                                .setRecurrenceDateOfMonth(Long.parseLong(value));
                    } else if ("weekday".equals(attributeName)) {
                        value = trimWeekdayTimezone(value.toLowerCase());
                        switch (value) {
                            case "sunday":
                                scheduleConfig.setSunday(true);
                                break;
                            case "monday":
                                scheduleConfig.setMonday(true);
                                break;
                            case "tuesday":
                                scheduleConfig.setTuesday(true);
                                break;
                            case "wednesday":
                                scheduleConfig.setWednesday(true);
                                break;
                            case "thursday":
                                scheduleConfig.setThursday(true);
                                break;
                            case "friday":
                                scheduleConfig.setFriday(true);
                                break;
                            case "saturday":
                                scheduleConfig.setSaturday(true);
                                break;
                        }
                    }
                }
            }
            
            private String trimWeekdayTimezone(String weekday) {
                int indexOfFirstSpace = weekday.indexOf(" ");
                if (indexOfFirstSpace > -1) {
                    return weekday.substring(0, indexOfFirstSpace);
                }
                return weekday;
            }

            private String trimDateTimeTimezone(String timeInstance) {
                Matcher matcher = timezonePattern.matcher(timeInstance);
                if (matcher.find()) {
                    String timezone = matcher.group(1);
                    return timeInstance.substring(0, timeInstance.indexOf(timezone));
                }
                return timeInstance;
            }

            private String getPartialTime(String partialTimeValue) {
                String[] timeInstance = partialTimeValue.split("\\s");
                String[] timeComponents = timeInstance[0].split(":");
                LocalTime msSinceMidnight = LocalTime.of(Integer.parseInt(timeComponents[0]),
                        Integer.parseInt(timeComponents[1]),
                        Integer.parseInt(timeComponents[2]));
                if (timeInstance.length > 1) {
                    if ("pm".equalsIgnoreCase(timeInstance[1]) && msSinceMidnight.getHour() < 12) {
                        msSinceMidnight = msSinceMidnight.plusHours(12L);
                    } else if ("am".equalsIgnoreCase(timeInstance[1]) && msSinceMidnight.getHour() == 12) {
                        msSinceMidnight = msSinceMidnight.plusHours(-12L);
                    }
                }
                return msSinceMidnight.format(formatter);
            }

            private String getTimezone(String timeInstance) {
                Matcher matcher = timezonePattern.matcher(timeInstance);
                if (matcher.find()) {
                    return matcher.group(1).trim();
                }
                return null;
            }
        }, IPredicateVisitor.PREORDER);
        
        if (scheduleConfig.getStartDateTime() == null || scheduleConfig.getEndDateTime() == null) {
            return null;
        } else {
            return scheduleConfig;
        }
    }

    public static PolicyEnvironmentConfigDTO getEnvConfig(IPredicate condition) {
        IPredicate predicate = getUIPredicateFromV3(condition);
        
        final PolicyEnvironmentConfigDTO envConfig = new PolicyEnvironmentConfigDTO();

        predicate.accept(new DefaultPredicateVisitor() {
            @Override
            public void visit(IRelation relation) {
                IExpression lhs = relation.getLHS();

                if (lhs instanceof RemoteAccessAttribute) {
                    int remoteAccess = Integer.valueOf(relation.getRHS().toString());
                    envConfig.setRemoteAccess(remoteAccess);
                } else if (lhs instanceof HeartbeatAttribute) {
                    int hearbeat = Integer.valueOf(relation.getRHS().toString());
                    envConfig.setTimeSinceLastHBSecs(hearbeat);
                }
            }
        }, IPredicateVisitor.PREORDER);

        return envConfig;
    }
    
    public static String getFreeTypeExpressionPQL(IPredicate condition) {
        IPredicate expressionPred = getFreeTypePredicateFromV3(condition);

        DomainObjectFormatter formatter = new DomainObjectFormatter();
        formatter.formatRef(expressionPred);

        String pql = formatter.getPQL();

        if (pql.equals(PredicateConstants.TRUE.getName())) {
            pql = "";
        }
        
        return pql;
    }

    /**
     * Takes the various conditions from policyDTO (remote/local, time since last
     * heartbeat, policy effective duration, and the freeType "Expression") and
     * converts them into a predicate.
     *
     * This is more complex than it might seem. We need to be able to take the predicate
     * created and undo the operation. The problem is that "Expression" can contain anything,
     * including things that look like the other conditions.
     *
     * We do this structurally. We'll put all the "UI" conditions on one branch and the freeType
     * expression on another.
     *
     *     AND
     *    /   \
     *   UI    FF
     *
     */
    public static IPredicate buildConditionPredicate(PolicyDTO policyDTO) throws PQLException {
        CompositePredicate uiPredicate = new CompositePredicate(BooleanOp.AND);

        // create schedule attributes
        scheduleConfig(policyDTO, uiPredicate);

        Relation remoteAccess = null;
        Relation timeSinceLastHB = null;

        // create environment attributes
        PolicyEnvironmentConfigDTO envConfig = policyDTO.getEnvironmentConfig();
        if (envConfig != null) {
            if (envConfig.getRemoteAccess() >= 0) {
                IExpression lhs = RemoteAccessAttribute.REMOTE_ACCESS;
                Constant value = Constant.build(envConfig.getRemoteAccess());
                remoteAccess = new Relation(RelationOp.EQUALS, lhs, value);
            }

            if (envConfig.getTimeSinceLastHBSecs() >= 0) {
                IExpression lhs = HeartbeatAttribute.TIME_SINCE_LAST_HEARTBEAT;
                Constant value = Constant.build(envConfig.getTimeSinceLastHBSecs());
                timeSinceLastHB = new Relation(RelationOp.GREATER_THAN, lhs, value);
            }
        }

        if (remoteAccess != null) {
            uiPredicate.addPredicate(remoteAccess);
        }

        if (timeSinceLastHB != null) {
            uiPredicate.addPredicate(timeSinceLastHB);
        }

        // Whatever was typed in the "Expression" box
        IPredicate freeTypePredicate = getFreeTypeConditionPredicate(policyDTO);

        // We now have a "UI" predicate and a free form predicate. The only minor complication
        // is that the UI predicate might be empty or might contain just a single predicate,
        // so we eliminate the cruft
        IPredicate finalUIPredicate;
        
        if (uiPredicate.predicateCount() == 0) {
            finalUIPredicate = PredicateConstants.TRUE;
        } else if (uiPredicate.predicateCount() == 1) {
            finalUIPredicate = uiPredicate.predicateAt(0);
        } else {
            finalUIPredicate = uiPredicate;
        }

        if (finalUIPredicate == null) {
            return buildV3PredicateFromFreeType(freeTypePredicate);
        } else {
            return buildV3Structure(finalUIPredicate, freeTypePredicate);
        }
    }

    /**
     * Convert the free form condition (i.e. the thing you type in the "Expression" box) into
     * a predicate. The easiest (?) way to do this is build a fake policy, parse it, and then
     * grab the parsed result.
     */
    private static IPredicate getFreeTypeConditionPredicate(PolicyDTO policyDTO) throws PQLException {
        final AtomicReference<IPredicate> atomicFreeTypePredicate = new AtomicReference<IPredicate>(PredicateConstants.TRUE);
        String freeTypeExpressionString = policyDTO.getExpression();
        if (StringUtils.isNotEmpty(freeTypeExpressionString)) {
            DomainObjectBuilder dob = new DomainObjectBuilder("POLICY \"dummy\" WHERE " +
                                                              freeTypeExpressionString);

            IPQLVisitor visitor = new DefaultPQLVisitor() {
                @Override
                public void visitPolicy(DomainObjectDescriptor descriptor, IDPolicy policy) {
                    atomicFreeTypePredicate.set(policy.getConditions());
                }
            };

            dob.processInternalPQL(visitor);
        }

        return atomicFreeTypePredicate.get();
    }
    
    private static void scheduleConfig(PolicyDTO policyDTO, CompositePredicate extraConditionPredicate) {
        PolicyScheduleConfigDTO scheduleConfig = policyDTO.getScheduleConfig();
        if (scheduleConfig != null) {
            if (StringUtils.isNotEmpty(scheduleConfig.getStartDateTime())) {
                IExpression lhs = TimeAttribute.IDENTITY;
                Constant value = Constant.build(getTimezoneExpression(scheduleConfig.getStartDateTime(), scheduleConfig.getTimezone()));
                Relation startTime = new Relation(RelationOp.GREATER_THAN_EQUALS, lhs, value);
                extraConditionPredicate.addPredicate(startTime);
            }

            if (StringUtils.isNotEmpty(scheduleConfig.getEndDateTime())) {
                IExpression lhs = TimeAttribute.IDENTITY;
                Constant value = Constant.build(getTimezoneExpression(scheduleConfig.getEndDateTime(), scheduleConfig.getTimezone()));
                Relation endTime = new Relation(RelationOp.LESS_THAN_EQUALS, lhs, value);
                extraConditionPredicate.addPredicate(endTime);
            }

            if (StringUtils
                    .isNotEmpty(scheduleConfig.getRecurrenceStartTime())) {
                IExpression lhs = TimeAttribute.TIME;
                Constant value = Constant.build(getTimezoneExpression(scheduleConfig.getRecurrenceStartTime(), scheduleConfig.getTimezone()));
                Relation recurrentStart = new Relation(RelationOp.GREATER_THAN_EQUALS, lhs, value);
                extraConditionPredicate.addPredicate(recurrentStart);
            }

            if (StringUtils.isNotEmpty(scheduleConfig.getRecurrenceEndTime())) {
                IExpression lhs = TimeAttribute.TIME;
                Constant value = Constant.build(getTimezoneExpression(scheduleConfig.getRecurrenceEndTime(), scheduleConfig.getTimezone()));
                Relation recurrentEnd = new Relation(RelationOp.LESS_THAN_EQUALS, lhs, value);
                extraConditionPredicate.addPredicate(recurrentEnd);
            }

            if (scheduleConfig.getRecurrenceDateOfMonth() > 0) {
                IExpression lhs = TimeAttribute.DATE;
                Constant value = Constant.build(scheduleConfig.getRecurrenceDateOfMonth());
                Relation dateOfMonth = new Relation(RelationOp.EQUALS, lhs, value);
                extraConditionPredicate.addPredicate(dateOfMonth);
            }

            List<IPredicate> weekdayPredicates = getSelectedWeekdays(scheduleConfig);

            if (scheduleConfig.getRecurrenceDayInMonth() > 0) {
                IExpression lhs = TimeAttribute.DOWIM;
                Constant value = Constant.build(scheduleConfig.getRecurrenceDayInMonth());
                Relation dayInMonth = new Relation(RelationOp.EQUALS, lhs, value);
                extraConditionPredicate.addPredicate(dayInMonth);

                // get selected weekday
                if (!weekdayPredicates.isEmpty()) {
                    extraConditionPredicate.addPredicate(weekdayPredicates.get(0));
                }
			} else if (!weekdayPredicates.isEmpty()) {
				if (weekdayPredicates.size() == 1) {
					extraConditionPredicate.addPredicate(weekdayPredicates.get(0));
				} else {
					CompositePredicate weekdaysPredicate = new CompositePredicate(BooleanOp.OR);
					for (IPredicate predicate : weekdayPredicates) {
						weekdaysPredicate.addPredicate(predicate);
					}
					extraConditionPredicate.addPredicate(weekdaysPredicate);
				}
			}
        }
    }

    private static String getTimezoneExpression(String timeInstance, String timezone) {
        if (timezone != null) {
            return String.format("%s %s", timeInstance, timezone);
        }
        return timeInstance;
    }

    private static List<IPredicate> getSelectedWeekdays(
            PolicyScheduleConfigDTO scheduleConfig) {
        List<IPredicate> weekdayPredicates = new ArrayList<>();
        if (scheduleConfig.isSunday()) {
            IExpression lhs = TimeAttribute.WEEKDAY;
            Constant value = Constant.build(getTimezoneExpression("sunday", scheduleConfig.getTimezone()));
            Relation sunday = new Relation(RelationOp.EQUALS, lhs, value);
            weekdayPredicates.add(sunday);
        }

        if (scheduleConfig.isMonday()) {
            IExpression lhs = TimeAttribute.WEEKDAY;
            Constant value = Constant.build(getTimezoneExpression("monday", scheduleConfig.getTimezone()));
            Relation monday = new Relation(RelationOp.EQUALS, lhs, value);
            weekdayPredicates.add(monday);
        }

        if (scheduleConfig.isTuesday()) {
            IExpression lhs = TimeAttribute.WEEKDAY;
            Constant value = Constant.build(getTimezoneExpression("tuesday", scheduleConfig.getTimezone()));
            Relation tuesday = new Relation(RelationOp.EQUALS, lhs, value);
            weekdayPredicates.add(tuesday);
        }

        if (scheduleConfig.isWednesday()) {
            IExpression lhs = TimeAttribute.WEEKDAY;
            Constant value = Constant.build(getTimezoneExpression("wednesday", scheduleConfig.getTimezone()));
            Relation wednesday = new Relation(RelationOp.EQUALS, lhs, value);
            weekdayPredicates.add(wednesday);
        }

        if (scheduleConfig.isThursday()) {
            IExpression lhs = TimeAttribute.WEEKDAY;
            Constant value = Constant.build(getTimezoneExpression("thursday", scheduleConfig.getTimezone()));
            Relation thursday = new Relation(RelationOp.EQUALS, lhs, value);
            weekdayPredicates.add(thursday);
        }

        if (scheduleConfig.isFriday()) {
            IExpression lhs = TimeAttribute.WEEKDAY;
            Constant value = Constant.build(getTimezoneExpression("friday", scheduleConfig.getTimezone()));
            Relation friday = new Relation(RelationOp.EQUALS, lhs, value);
            weekdayPredicates.add(friday);
        }

        if (scheduleConfig.isSaturday()) {
            IExpression lhs = TimeAttribute.WEEKDAY;
            Constant value = Constant.build(getTimezoneExpression("saturday", scheduleConfig.getTimezone()));
            Relation saturday = new Relation(RelationOp.EQUALS, lhs, value);
            weekdayPredicates.add(saturday);
        }
        return weekdayPredicates;
    }

    public static boolean validateExpression(String expressionString) {
        try {
            DomainObjectBuilder dob = new DomainObjectBuilder(
                    "POLICY \"dummy\" WHERE " + expressionString);
            final AtomicReference<IPredicate> aPredicate = new AtomicReference<IPredicate>();

            IPQLVisitor visitor = new DefaultPQLVisitor() {

                @Override
                public void visitPolicy(DomainObjectDescriptor descriptor,
                        IDPolicy policy) {
                    // I only care the first one
                    if (aPredicate.get() == null) {
                        aPredicate.set(policy.getConditions());
                    }
                }
            };

            dob.processInternalPQL(visitor);
            IPredicate predicate = aPredicate.get();
            if (predicate == null) {
                return false;
            }
            return true;
        } catch (Exception e) {
            log.info("Error encountered in condition expression parsing",
                    e.getMessage());
            return false;
        }
    }

    /*
     * A note on V1 and V2.
     *
     * The condition predicate includes some pre-defined conditions
     * (time-of-day, remote access) and a free-form
     * expression. Unfortunately, there's no immediate way to
     * determine if 'environment.day_of_week = "tuesday"' (for
     * example) is actually part of the time-of-day-in-the-GUI or
     * something the user wrote in the free-form expression.
     *
     * Our solution is to use the structure of the predicate.
     *
     * Originally we had a V1 structure, which hasn't existed in a
     * long time and isn't that interesting. We can assume that we
     * will only deal with the V2 structure. The V2 structure looked
     * like this:
     *
     *         AND
     *         / \
     *      TRUE AND
     *           / \
     *       cond   free-form
     * 
     *
     * You could tell if you were dealing with V2, because the left
     * hand side of the top "AND" was TRUE.
     *
     * Unfortunately, we didn't build it correctly here. We can't
     * merely start doing it correctly, because we won't be able to
     * correctly interpret older bits of PQL.
     *
     * Solution? V3. Yeah, I hate it too. V3 will look like this:
     *
     *       OR
     *      /  \
     *   FALSE  AND
     *          / \
     *       cond  free-form
     *
     * Easy to identify. If we see a condition with a top level "AND"
     * then it's V2 and we should convert. If it's top level "OR" then
     * it's V3. If it's just TRUE then we can identify that, too.
     */
       
    private static class ConditionWalker {
        IPredicate v1Root = null;
        IPredicate freeTypeRoot = null;

        void walk(IPredicate predicate) {
            if (predicate instanceof ICompositePredicate) {
                ICompositePredicate cp1 = (ICompositePredicate) predicate;
                if (cp1.getOp() == BooleanOp.AND && cp1.predicateCount() == 2) {
                    walkCp1(cp1);
                }
            }

            if (v1Root == null) {
                v1Root = predicate;
            }
        }

        void walkCp1(ICompositePredicate cp1) {
            IPredicate lhs = cp1.predicateAt(0);
            IPredicate rhs = cp1.predicateAt(1);
            if (lhs instanceof PredicateConstants
                    && ((PredicateConstants) lhs) == PredicateConstants.TRUE) {
                if (rhs instanceof ICompositePredicate) {
                    ICompositePredicate cp2 = (ICompositePredicate) rhs;
                    walkCp2(cp2);
                }
            }
        }

        void walkCp2(ICompositePredicate cp2) {
            if (cp2.getOp() == BooleanOp.AND && cp2.predicateCount() == 2) {
                walkV1Root(cp2.predicateAt(0));
                walkFreeTypeRoot(cp2.predicateAt(1));
            }
        }

        void walkV1Root(IPredicate predicate) {
            v1Root = predicate;
        }

        void walkFreeTypeRoot(IPredicate predicate) {
            freeTypeRoot = predicate;
        }

    }

    /**
     * 
     * @param predicate, could be v1 or v2 condition
     * @return the UI (also called "v1") structure
     */
    static IPredicate getUIPredicateFromV2(IPredicate predicate) {
        ConditionWalker walker = new ConditionWalker();
        walker.walk(predicate);
        return walker.v1Root;
    }

    /**
     * 
     * @param predicate, could be v1 or v2 condition
     * @return the free type structure
     */
    static IPredicate getFreeTypePredicateFromV2(IPredicate predicate) {
        ConditionWalker walker = new ConditionWalker();
        walker.walk(predicate);
        return walker.freeTypeRoot;
    }

    public static String getFreeTypeConditionString(IPredicate predicate) {
        IPredicate freeTypeRoot = getFreeTypePredicateFromV3(predicate);
        if (freeTypeRoot == null || freeTypeRoot == PredicateConstants.TRUE) {
            return null;
        }

        DomainObjectFormatter dof = new DomainObjectFormatter();
        dof.formatRef(freeTypeRoot);

        return dof.getPQL();
    }

    private static boolean isV3(IPredicate pred) {
        return (pred instanceof CompositePredicate &&
                ((CompositePredicate)pred).getOp() == BooleanOp.OR);
    }
    
    private static IPredicate buildV3FromCondition(IPredicate condition) {
        if (isV3(condition)) {
            return condition;
        }

        return buildV3Structure(getUIPredicateFromV2(condition),
                                getFreeTypePredicateFromV2(condition));
    }
    
    private static ICompositePredicate buildV3PredicateFromFreeType(IPredicate freeTypePredicate) {
        return buildV3Structure(PredicateConstants.TRUE, freeTypePredicate);
    }

    private static ICompositePredicate buildV3Structure(IPredicate uiPredicate, IPredicate freeTypeExpression) {
        /*
         * The new condition type will be OR (root)
         *                                / \
         *                             FALSE AND
         *                                  / \
         *                                 ui freeTypeExpression
         */
        CompositePredicate root = new CompositePredicate(BooleanOp.OR, PredicateConstants.FALSE);
        root.addPredicate(new CompositePredicate(BooleanOp.AND, Arrays.asList(uiPredicate, freeTypeExpression)));

        return root;
    }

    private static ICompositePredicate getSubtreeFromV3(IPredicate predicate) {
        // Ensure this is a V3 predicate
        ICompositePredicate compositePredicate = (ICompositePredicate)buildV3FromCondition(predicate);

        // The structure should be well defined, but we'll check anyway rather than
        // assuming.
        if (compositePredicate.predicateCount() != 2) {
            return null;
        }
        
        IPredicate rhs = compositePredicate.predicateAt(1);

        if (!(rhs instanceof CompositePredicate)) {
            return null;
        }

        ICompositePredicate compositeRHS = (ICompositePredicate)rhs;

        if (compositeRHS.predicateCount() != 2) {
            return null;
        }

        return compositeRHS;
    }
    
    private static IPredicate getUIPredicateFromV3(IPredicate predicate) {
        ICompositePredicate subTree = getSubtreeFromV3(predicate);

        if (subTree == null) {
            return PredicateConstants.TRUE;
        }

        return subTree.predicateAt(0);
    }

    private static IPredicate getFreeTypePredicateFromV3(IPredicate predicate) {
        ICompositePredicate subTree = getSubtreeFromV3(predicate);

        if (subTree == null) {
            return PredicateConstants.TRUE;
        }

        return subTree.predicateAt(1);
    }
    
}
