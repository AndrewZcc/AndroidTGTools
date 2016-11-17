
package com.evancharlton.mileage.charts;

import com.evancharlton.mileage.dao.Fillup;

public abstract class LongitudeChart extends LineChart {
    @Override
    protected ChartGenerator createChartGenerator() {
        return new LineChartGenerator(this, getVehicle(), new String[] {
                Fillup.DATE,
                Fillup.LONGITUDE
        });
    }
}
