package org.jboss.snowdrop.samples.sportsclub.domain.repository.criteria;

/**
 * @author Marius Bogoevici</a>
 */
public class PersonSearchCriteria extends RangeCriteria {

    private String name;

    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}