/*
 * WBI Indicator Explorer
 *
 * Copyright 2015 Sebastian Nogara <snogaraleal@gmail.com>
 *
 * This file is part of WBI.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package data.tasks;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlUpdate;

import models.Indicator;

public class IndicatorUnloadTask implements Runnable {
    protected Indicator indicator;

    public IndicatorUnloadTask(Indicator indicator) {
        this.indicator = indicator;
    }

    @Override
    public void run() {
        SqlUpdate deletePoints = Ebean.createSqlUpdate(
            "DELETE FROM point " +
            "WHERE series_id IN (" +
                "SELECT id " +
                "FROM series " +
                "WHERE indicator_id = :indicator_id " +
            ")");
        deletePoints.setParameter("indicator_id", indicator.getId());
        deletePoints.execute();

        SqlUpdate deleteSeries = Ebean.createSqlUpdate(
            "DELETE FROM series " +
            "WHERE indicator_id = :indicator_id");
        deleteSeries.setParameter("indicator_id", indicator.getId());
        deleteSeries.execute();
    }
}
