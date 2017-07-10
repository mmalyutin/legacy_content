package org.jboss.snowdrop.samples.sportsclub.domain.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Marius Bogoevici</a>
 */
@Entity
public class Invoice implements Serializable {

    private static final long serialVersionUID = 8291962316820082758L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    private Date issueDate;

    private TimeInterval billingPeriod;

    private BigDecimal amount;

    public long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public TimeInterval getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(TimeInterval billingPeriod) {
        this.billingPeriod = billingPeriod;
    }
}
