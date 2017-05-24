package pl.luwi.series.reducer;


import de.micromata.opengis.kml.v_2_2_0.*;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import org.w3c.dom.*;
import javax.swing.*;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static pl.luwi.series.reducer.PointImpl.p;



/**
 * Created by maedeh.hesabgar on 12/05/2017.
 */
public class MainClass extends JPanel implements ActionListener {


    static JFileChooser chooser;
    static JButton FileButton;

    public static void main(String[] args){
        fileCooser();
    }

    //This methods reads the kml file, extracts points coordinations
    public static void simplifier(String filePath, String newFilePath){
        Kml kml = Kml.unmarshal(new File(filePath));
        final Placemark placemark = (Placemark) kml.getFeature();
        final Polygon polygon = (Polygon) placemark.getGeometry();
        final Boundary outerBoundaryIs = polygon.getOuterBoundaryIs();
        LinearRing linearRing = outerBoundaryIs.getLinearRing();
        List<Coordinate> coordinates = linearRing.getCoordinates();
        System.out.print(coordinates.size());

        List<Point> myPoints= new ArrayList<Point>();
        for(Coordinate coordinate : coordinates) {
            if(coordinate != null) {

                myPoints.add(p(coordinate.getLatitude(),coordinate.getLongitude()));
            }
        }

        //remove the last point which is the first point. Because algorithm cannot work on a loop.
        myPoints.remove(myPoints.size()-1);

        //calculating epsilon.EpsilonHelper has different methods for calculating epsilon. I think "avg" works best for us.
        Double epsilon = EpsilonHelper.avg(EpsilonHelper.deviations(myPoints));

        //reduce is a method that eliminates the redundant/extra points.
        List<Point> reduced1 = SeriesReducer.reduce(myPoints, epsilon);
        reduced1.add(reduced1.get(0));

        String pointCoordinates = "";
        for(Point p:reduced1){
            pointCoordinates = pointCoordinates + p.getY() + "," + p.getX() + " ";
        }

        //creating the new kml file for reduced points.
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
            Element writingCoordinates = doc.createElement("coordinates");
            writingCoordinates.appendChild(doc.createTextNode(pointCoordinates));
            writingLinearRing.appendChild(writingCoordinates);


            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = null;
            transformer = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            //String newFilePath = s;
            StreamResult result = new StreamResult(new File(newFilePath));

            transformer.transform(source, result);


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    //This method helps you to choose your kml file. It sets the filePath. and call the simplifier method on the selected chosen file.
    public static void fileCooser(){
        FileButton = new JButton("Select A Kml file");
        FileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = chooser.showOpenDialog(new JFrame());
                if (result == JFileChooser.APPROVE_OPTION) {

                    String chosenFilePath = String.valueOf(chooser.getSelectedFile());
                    String fileName = chosenFilePath.substring(chosenFilePath.lastIndexOf("\\")+1);
                    String filePath = chosenFilePath.substring(0,chosenFilePath.lastIndexOf("\\")+1);
                    String newFilePath = filePath + "Reduced_" + fileName;
                    simplifier(chosenFilePath,newFilePath);
                    System.out.print(fileName);
                }
            }
        });
        JFrame frame = new JFrame("Choosing a kml file");
        frame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                }
        );

        JPanel panel = new JPanel();
        panel.add(FileButton);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

    }

    public void actionPerformed(ActionEvent e) {

    }
}

