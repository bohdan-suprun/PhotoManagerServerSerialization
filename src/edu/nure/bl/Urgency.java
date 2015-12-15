package edu.nure.bl;

import edu.nure.bl.constraints.MoreOrEq;
import edu.nure.bl.constraints.ValidationException;
import edu.nure.bl.constraints.Validator;
import edu.nure.db.entity.basic.AbstractEntity;
import edu.nure.db.primarykey.IntegerPrimaryKey;
import edu.nure.db.primarykey.PrimaryKey;
import edu.nure.exceptions.DBException;
import edu.nure.util.ResponseBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by bod on 17.09.15.
 */
public class Urgency extends AbstractEntity {

    private static final long serialVersionUID = -6846444701940860681L;
    private int term;
    private double factor;
    private static final int TO_MS = 60000;

    public int getTerm() {
        return term;
    }

    private void setTerm(int term) throws ValidationException {
        Date now = new Date();
        Date inTerm = new Date(now.getTime() + term * TO_MS);
        if (inTerm.after(now)) {
            this.term = term;
        } else throw new ValidationException("Bad term");
    }

    public double getFactor() {
        return factor;
    }

    private void setFactor(double factor) throws ValidationException {
        this.factor = (Double) Validator.validate(factor, new MoreOrEq<Double>(0.0));
    }

    public Urgency() {

    }

    public Urgency(int term, double factor) throws ValidationException {
        setTerm(term);
        setFactor(factor);
    }

    @Override
    public void parseResultSet(ResultSet rs) throws ValidationException, DBException {
        try {
            setTerm(rs.getInt("Term"));
            setFactor(rs.getFloat("Factor"));
        } catch (SQLException ex) {
            throw new DBException(ex.getMessage());
        }
    }

    public Urgency(ResponseBuilder req) throws ValidationException {
        try {
            setTerm(req.getIntParameter("term"));
            setFactor(req.getDoubleParameter("factor"));
        } catch (NumberFormatException ex) {
            throw new ValidationException();
        }
    }
//    @Override
//    public String toXML() {
//        return "<urgency term=\"" + term + "\" factor=\"" + factor + "\"/>";
//    }

    @Override
    public String toQuery() {
        return "term=" + term +
                "&factor=" + factor;
    }

    public String[] getFields() {
        return new String[]{"Term", "Factor"};
    }

    @Override
    public Object[] getValues() {
        return new Object[]{getTerm(), getFactor()};
    }

    @Override
    public String entityName() {
        return "URGENCY";
    }

    @Override
    public PrimaryKey getPrimaryKey() {
        return new IntegerPrimaryKey("Term", getTerm());
    }
}
