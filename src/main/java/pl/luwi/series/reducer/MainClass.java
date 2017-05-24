package pl.luwi.series.reducer;


import de.micromata.opengis.kml.v_2_2_0.*;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
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


        String pointCoordinates = "";
        for(Point p:reduced1){
            pointCoordinates = pointCoordinates + p.getY() + "," + p.getX() + " ";
        }


        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("kml");
            rootElement.setAttribute("xmlns","http://www.opengis.net/kml/2.2");
            doc.appendChild(rootElement);

            // placeMark element
            Element writingPlacemark = doc.createElement("Placemark");
            rootElement.appendChild(writingPlacemark);
            writingPlacemark.setAttribute("id","ca_on_neighbourhood_london_airport");

            // polygon element
            Element writingPolygon = doc.createElement("Polygon");
            writingPlacemark.appendChild(writingPolygon);

            // outerBoundaryIs element
            Element writingouterBoundaryIs = doc.createElement("outerBoundaryIs");
            writingPolygon.appendChild(writingouterBoundaryIs);

            // LinearRing element
            Element writingLinearRing = doc.createElement("LinearRing");
            writingouterBoundaryIs.appendChild(writingLinearRing);

            // coordinates element
            Element writingcoordinates = doc.createElement("coordinates");
            writingcoordinates.appendChild(doc.createTextNode(pointCoordinates));
            writingLinearRing.appendChild(writingcoordinates);


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            transformer = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("HelloKml.kml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }
}

