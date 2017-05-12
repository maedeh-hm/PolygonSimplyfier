package pl.luwi.series.reducer;


import java.util.List;

import static java.util.Arrays.asList;
import static pl.luwi.series.reducer.PointImpl.p;


/**
 * Created by maedeh.hesabgar on 12/05/2017.
 */
public class MainClass {

    public static void main(String[] args){


        List myList = asList(p(43.048096837000053, -81.129290106999917), p(43.044616697000038, -81.127565861999926), p(43.044120331000045, -81.127319941999929), p(43.043049488000065, -81.126737318999915));

        System.out.println(myList);

        Double epsilon = EpsilonHelper.avg(EpsilonHelper.deviations(myList));
        List<Point> reduced1 = SeriesReducer.reduce(myList, epsilon);
        System.out.println(reduced1);
    }

}
