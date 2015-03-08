package models;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import rpc.shared.data.Type;

@SuppressWarnings("serial")
@Entity
public class Point extends Model {
    public static Finder<Point> objects =
        new Finder<Point>(Point.class);

    @ManyToOne
    protected Series series;

    protected int year;

    protected double value;

    public Point() {
        super();
    }

    public Point(Series series, int year, double value) {
        this();

        this.series = series;
        this.year = year;
        this.value = value;
    }

    public Series getSeries() {
        return series;
    }

    public int getYear() {
        return year;
    }

    public double getValue() {
        return value;
    }

    public static final String FIELD_YEAR = "year";
    public static final String FIELD_VALUE = "value";

    @Override
    public Object get(String field) {
        if (field == FIELD_YEAR) return year;
        if (field == FIELD_VALUE) return value;
        return super.get(field);
    }

    @Override
    public void set(String field, Object value) {
        super.set(field, value);
        if (field == FIELD_YEAR) year = (Integer) value;
        if (field == FIELD_VALUE) this.value = (Double) value;
    }

    private static Map<String, Type> fields;

    @Override
    public Map<String, Type> fields() {
        if (fields == null) {
            fields = super.fields();
            fields.put(FIELD_YEAR, Type.get(Integer.class));
            fields.put(FIELD_VALUE, Type.get(Double.class));
        }
        return fields;
    }
}
