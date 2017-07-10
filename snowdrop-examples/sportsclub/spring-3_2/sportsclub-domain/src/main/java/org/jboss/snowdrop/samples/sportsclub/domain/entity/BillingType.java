package org.jboss.snowdrop.samples.sportsclub.domain.entity;

import java.io.Serializable;

/**
 * @author Marius Bogoevici</a>
 */
public enum BillingType implements Serializable {
    MONTHLY(12),

    SEMIMONTHLY(24),

    BIWEEKLY(26),

    WEEKLY(52);

    private int periods;

    BillingType(int periods) {
        this.periods = periods;
    }

    public int periodsPerYear() {
        return periods;
    }
}
