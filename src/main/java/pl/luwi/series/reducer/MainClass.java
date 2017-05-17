package pl.luwi.series.reducer;


import de.micromata.opengis.kml.v_2_2_0.*;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import static pl.luwi.series.reducer.PointImpl.p;



/**
 * Created by maedeh.hesabgar on 12/05/2017.
 */
public class MainClass {

    public static void main(String[] args){


        Kml kml = Kml.unmarshal(new File("C:\\Users\\maedeh.hesabgar\\Desktop\\london\\london\\ca_on_neighbourhood_london_stoneybrook.kml"));
        final Placemark placemark = (Placemark) kml.getFeature();
        final Polygon polygon = (Polygon) placemark.getGeometry();
        final Boundary outerBoundaryIs = polygon.getOuterBoundaryIs();
        LinearRing linearRing = outerBoundaryIs.getLinearRing();
        List<Coordinate> coordinates = linearRing.getCoordinates();
        System.out.print(coordinates.size());

        List<Point> myPoints= new ArrayList<Point>();
        for(Coordinate coordinate : coordinates) {
            if(coordinate != null) {
                System.out.println("Longitude: " +  coordinate.getLongitude());
                System.out.println("Latitude : " +  coordinate.getLatitude());
                System.out.println("");
                myPoints.add(p(coordinate.getLatitude(),coordinate.getLongitude()));
            }
        }

        myPoints.remove(myPoints.size()-1);

        Double epsilon = EpsilonHelper.avg(EpsilonHelper.deviations(myPoints));
        List<Point> reduced1 = SeriesReducer.reduce(myPoints, epsilon);
        reduced1.add(reduced1.get(0));
        System.out.println(reduced1.size());


        final Kml createdKml = new Kml();
        de.micromata.opengis.kml.v_2_2_0.Point p2 = createdKml.createAndSetPlacemark()
                .withName("FirstTest").withOpen(Boolean.TRUE)
                .createAndSetPoint();

        for(Point p:reduced1){
            p2.addToCoordinates(p.getY(), p.getX());

        }

        try {
            createdKml.marshal(new File("HelloKml.kml"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

