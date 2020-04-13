
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.util;

import com.scrapper.db.ConnectionPool;
import com.scrapper.db.MySQLConnectionFactory;
import com.scrapper.ml.MercadoLibreAPI;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author casa
 */
public class ReadExcelInit {

    org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ReadExcelInit.class);

    public static List<String> readExcelFile(File excelFile) {
        List<String> urls = new ArrayList<String>();

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            String cellValue;

            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {
                    cellValue = hssfRow.getCell(1).getStringCellValue();
                    urls.add(cellValue);
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
        return urls;
    }

    public static void readExcelFileSku(File excelFile) {
        MySQLConnectionFactory connectDB = new MySQLConnectionFactory();

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            String cellValue, cellValue2;

            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {
                    cellValue = hssfRow.getCell(7).getStringCellValue();
                    System.out.println("ASIN=" + cellValue);
                    cellValue2 = hssfRow.getCell(5).getStringCellValue();
                    System.out.println("SKU=" + cellValue2);
                    connectDB.updateProductoMLInicial(cellValue, cellValue2, "");
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
    }

    public static void readExcelFileML(File excelFile) {
        MySQLConnectionFactory connectDB = new MySQLConnectionFactory();

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            String cellValue, cellValue2;

            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {
                    cellValue = hssfRow.getCell(0).getStringCellValue();
                    System.out.println("SKU=" + cellValue);
                    cellValue2 = hssfRow.getCell(1).getStringCellValue();
                    System.out.println(r + "-ML=" + cellValue2);
                    connectDB.updateProductoMLSKU(cellValue2, cellValue);
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
    }

    public static void readExcelFileMLPrice(File excelFile) throws FileNotFoundException {
        MercadoLibreAPI ml = new MercadoLibreAPI();
        OutputStream excelNewOutputStream = new FileOutputStream("nuevoML.xlsx");

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            double cellValue, cellValue2;

            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {
                    try {
                        cellValue = hssfRow.getCell(3).getNumericCellValue();

                        System.out.println("PRECIO=" + cellValue);
                        float newPrice = ml.calculaPrecioMX((float) cellValue);
                        System.out.println("PRECIO=" + newPrice);
                        hssfRow.createCell(14).setCellValue(newPrice);
                    } catch (Exception e) {
                        cellValue = 0;
                        hssfRow.createCell(14).setCellValue(cellValue);

                    }
                }
            }
            hssfWorkbook.write(excelNewOutputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
    }

    public static void readExcelFileMLData(File excelFile) throws FileNotFoundException {
        MercadoLibreAPI ml = new MercadoLibreAPI();

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            String nombre, descripcion, url;

            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {
                    url = hssfRow.getCell(1).getStringCellValue();
                    nombre = hssfRow.getCell(2).getStringCellValue();
                    descripcion = hssfRow.getCell(3).getStringCellValue();
                    System.out.println("url=" + url);
                    System.out.println("nombre=" + nombre);
                    String query = "update producto_inicial set  titulo=?, descripcion=?  where url=?";
                    try (Connection conn = ConnectionPool.getInstance().getConnection();
                            PreparedStatement statement = conn.prepareStatement(query);) {
                        statement.setString(1, nombre);
                        statement.setString(2, descripcion);
                        statement.setString(3, url);
                        statement.executeUpdate();
                        statement.close();
                        conn.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            //hssfWorkbook.write(excelNewOutputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
    }

    public static void readExcelSKUMLData(File excelFile) throws FileNotFoundException {
        MercadoLibreAPI ml = new MercadoLibreAPI();

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            String nombre, descripcion, url;

            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {
                    try {
                        url = hssfRow.getCell(0).getStringCellValue();
                    } catch (Exception e) {
                        url = "NOTIENE";
                    }
                    try {
                        nombre = hssfRow.getCell(1).getStringCellValue();
                    } catch (Exception e) {
                        nombre = "NO TIENE";
                    }
                    System.out.println("url=" + url);
                    System.out.println("nombre=" + nombre);
                    String query = "insert into mlsku (mlcode,sku) values(?,?)";
                    try (Connection conn = ConnectionPool.getInstance().getConnection();
                            PreparedStatement statement = conn.prepareStatement(query);) {
                        statement.setString(1, url);
                        statement.setString(2, nombre);
                        statement.executeUpdate();
                        statement.close();
                        conn.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            //hssfWorkbook.write(excelNewOutputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
    }

    public static void readExcelPruebaData(File excelFile) throws FileNotFoundException {
        MercadoLibreAPI ml = new MercadoLibreAPI();

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            String nombre, descripcion, url;

            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {
                    url = hssfRow.getCell(2).getStringCellValue();

                    nombre = hssfRow.getCell(0).getStringCellValue();

                    System.out.println("url=" + url);
                    System.out.println("nombre=" + nombre);
                    String query = "update productos set mlcode=? where asin=?";
                    try (Connection conn = ConnectionPool.getInstance().getConnection();
                            PreparedStatement statement = conn.prepareStatement(query);) {
                        statement.setString(2, nombre);
                        statement.setString(1, url);
                        statement.executeUpdate();
                        statement.close();
                        conn.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            //hssfWorkbook.write(excelNewOutputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
    }

    public static void readExcelMLNew(File excelFile) throws FileNotFoundException {

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            String mdcode, sku, status, brand, model, precio;

            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {
                    try {
                        mdcode = hssfRow.getCell(0).getStringCellValue();
                    } catch (Exception e) {
                        mdcode = "NO TIENE";
                    }
                    try {
                        mdcode = hssfRow.getCell(0).getStringCellValue();
                    } catch (Exception e) {
                        mdcode = "NO TIENE";
                    }
                    try {
                        sku = hssfRow.getCell(1).getStringCellValue();
                    } catch (Exception e) {
                        sku = "NO TIENE";
                    }
                    try {
                        status = hssfRow.getCell(3).getStringCellValue();
                    } catch (Exception e) {
                        status = "NO TIENE";
                    }
                    try {
                        brand = hssfRow.getCell(4).getStringCellValue();

                    } catch (Exception e) {
                        brand = "NO TIENE";
                    }
                    try {
                        model = hssfRow.getCell(5).getStringCellValue();

                    } catch (Exception e) {
                        model = "NO TIENE";
                    }
                    try {
                        precio = hssfRow.getCell(2).getStringCellValue();
                    } catch (Exception e) {
                        precio = "0";
                    }

                    System.out.println("Mdcode=" + mdcode);
                    System.out.println("sku=" + sku);
                    System.out.println("status=" + status);
                    System.out.println("brand=" + brand);
                    System.out.println("Model=" + model);
                    System.out.println("preico=" + precio);
                    String query = "insert into dataML (mdcode,sku,price,status,brand,model) values(?,?,?,?,?,?)";
                    try (Connection conn = ConnectionPool.getInstance().getConnection();
                            PreparedStatement statement = conn.prepareStatement(query);) {
                        statement.setString(1, mdcode);
                        statement.setString(2, sku);
                        try {
                            statement.setFloat(3, Float.parseFloat(precio));
                        } catch (Exception e) {
                            statement.setFloat(3, 0);
                        }
                        statement.setString(4, status);
                        statement.setString(5, brand);
                        statement.setString(6, model);
                        statement.executeUpdate();
                        statement.close();
                        conn.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            //hssfWorkbook.write(excelNewOutputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
    }

    public static void readExcelFileAllData(File excelFile) throws FileNotFoundException {
        MercadoLibreAPI ml = new MercadoLibreAPI();

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            String asin, sku, titulo, nombre, descripcion, url, mlcode;

            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {

                    try {
                        sku = hssfRow.getCell(1).getStringCellValue();
                    } catch (Exception e) {
                        sku = "ERROR";
                    }
                    try {
                        mlcode = hssfRow.getCell(0).getStringCellValue();
                    } catch (Exception e) {
                        mlcode = "ERROR";
                    }
                    try {
                        asin = hssfRow.getCell(2).getStringCellValue();
                    } catch (Exception e) {
                        asin = "ERROR";
                    }
                    /*           try{
                     descripcion = hssfRow.getCell(3).getStringCellValue();
                     }catch(Exception e){
                     descripcion="ERROR";
                     }
                     try{
                     asin = hssfRow.getCell(4).getStringCellValue();
                     }catch(Exception e){
                     asin="ERROR";
                     }*/
                    System.out.println("asin=" + asin);
                    System.out.println("sku=" + sku);

                    System.out.println("mlcode=" + mlcode);
                    String query = "";
                    query = "update producto_inicial set  asin=? where mlcode=?";

                    /*try (Connection conn = ConnectionPool.getInstance().getConnection(); 
                     PreparedStatement statement = conn.prepareStatement(query);) {
                     statement.setString(1, asin);  
                     statement.setString(2, mlcode);  
                     statement.executeUpdate();
                     } catch (SQLException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                     }
                     */
                }
            }
            //hssfWorkbook.write(excelNewOutputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
    }

    public static void main(String a[]) {
        try {

            readExcelVariantes(new File("variantesdataNEW.xlsx"));
            //updateData();
            //borraData();
            //readExcelDataJesus(new File("dataJesus.xlsx"));
            /*  MySQLConnectionFactory connectDB=new MySQLConnectionFactory();
             List<String> urls=new ArrayList<String>();
             urls=readExcelFile(new File("inventariosku.xlsx"));
             for(String url:urls){
             if(url.indexOf("/dp/")>=0){
             String asin=url.substring(url.indexOf("/dp/")+4,url.indexOf("/dp/")+14);
             System.out.println("ASIN="+asin);
             connectDB.creaProductoInit(asin, url);
             }else{
             System.out.println("URL NO EXUSTE="+url);
             }
             }*/
        } catch (Exception ex) {
            Logger.getLogger(ReadExcelInit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void borraData() {
        String asins[] = {"B0117RFYQE", "B0756CYWWD", "B0117RFOEG", "B0117RFPCC", "B019OHFZGI", "B00LI4L1LO", "B016LL8AIU",
            "B01FASW74S", "B00UW1N60S", "B00AXX0I88", "B00F67RCM6", "B01BNB4JO2", "B00TM1CT98", "B01L7PSJFO", "B00M0V7RWQ",
            "B00M0V7WHG", "B00M1UIUS6", "B01LYE0OZS", "B00B4PJC9K", "B00177BQC6", "B01M8NNB9R", "B01DL3G6MS", "B00VV6Y7O2",
            "B00VW7U8X4", "B00BR3VZCG", "B074S6MP72", "B074TJCD29", "B00HVBPH0E", "B01EN9KI8C", "B00CIR50TY", "B00CN7VCT6",
            "B01HETFQA8", "B00J5SGPN4", "B00RTWITHS", "B010CZDWWK", "B0117RFPM2", "B0117RFZHC", "B0117RGD0K"};
        String borraAtributos = "delete from atributos where asin=?";
        String borraBullets = "delete from bullets where asin=?";
        String borraImagenes = "delete from imagenes where asin=?";
        String borraVariantes = "delete from variantes where asin=?";
        String borraProductos = "delete from productos where asin=?";
        for (String asin : asins) {
            try (Connection conn = ConnectionPool.getInstance().getConnection();
                    PreparedStatement statement = conn.prepareStatement(borraAtributos);) {
                statement.setString(1, asin);
                statement.executeUpdate();
                statement.close();
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try (Connection conn = ConnectionPool.getInstance().getConnection();
                    PreparedStatement statement = conn.prepareStatement(borraBullets);) {
                statement.setString(1, asin);
                statement.executeUpdate();
                statement.close();
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try (Connection conn = ConnectionPool.getInstance().getConnection();
                    PreparedStatement statement = conn.prepareStatement(borraImagenes);) {
                statement.setString(1, asin);
                statement.executeUpdate();
                statement.close();
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try (Connection conn = ConnectionPool.getInstance().getConnection();
                    PreparedStatement statement = conn.prepareStatement(borraVariantes);) {
                statement.setString(1, asin);
                statement.executeUpdate();
                statement.close();
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try (Connection conn = ConnectionPool.getInstance().getConnection();
                    PreparedStatement statement = conn.prepareStatement(borraProductos);) {
                statement.setString(1, asin);
                statement.executeUpdate();
                statement.close();
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("TERMINÉ CON EL PRODUCTO " + asin);
        }
    }

    public static void updateData() {
        String asins[] = {"B06XR9WDZH", "B00UKHLDEU",
            "B005C3K87U",
            "B00S0RFT8S",
            "B001CWVYMI",
            "B00O8RC5NQ",
            "B01MRS1LC1",
            "B00HVBP97U",
            "B003PLC7XY",
            "B01C2A1FZ4",
            "B00I9TSW1I",
            "B004S55KZ8",
            "B01MG588T5",
            "B00NAPQM6S",
            "B00TRRTNRS",
            "B06Y2GZWCJ",
            "B0026A1S30",
            "B00LEYNO5Y",
            "B003JOCVBU",
            "B005QIYRVY",
            "B00XBQ6770",
            "B075FBDWVV",
            "B01686O4C4",
            "B01MXGOGJ7",
            "B002HWS2GS",
            "B009D4UXCO",
            "B007SO5BYG",
            "B01IQXZ0FC",
            "B06XXFVBHV",
            "B06XHQLDJV",
            "B06XRXWW98",
            "B015JWAEIQ",
            "B06XRJ32CV",
            "B06XRYLC44",
            "B00F5D3X5Q",
            "B00AZM9RWK",
            "B005LGV2PA",
            "B01MFCJNOC"
        };
        String mlCodes[] = {
            "MLM614437542",
            "MLM614437558",
            "MLM614531394",
            "MLM614531453",
            "MLM614531456",
            "MLM614531535",
            "MLM614687478",
            "MLM614687496",
            "MLM617087877",
            "MLM617088992",
            "MLM617089770",
            "MLM617089772",
            "MLM617089795",
            "MLM617095420",
            "MLM617095539",
            "MLM617096487",
            "MLM617096531",
            "MLM617096565",
            "MLM617098205",
            "MLM617098208",
            "MLM618132024",
            "MLM618134131",
            "MLM618201751",
            "MLM618201760",
            "MLM618201864",
            "MLM618202673",
            "MLM618202685",
            "MLM618202773",
            "MLM618203210",
            "MLM618440860",
            "MLM618441216",
            "MLM618441233",
            "MLM618441337",
            "MLM618441341",
            "MLM618442621",
            "MLM618442639",
            "MLM618443199",
            "MLM618443221"};

        for (int i = 0; i < asins.length; i++) {
            String query = "update productos set mlcode=? where asin=?";
            try (Connection conn = ConnectionPool.getInstance().getConnection();
                    PreparedStatement statement = conn.prepareStatement(query);) {
                System.out.println("Procesando el ASIN " + asins[i] + " con el MLCODE=" + mlCodes[i]);
                statement.setString(2, asins[i]);
                statement.setString(1, mlCodes[i]);
                statement.executeUpdate();
                statement.close();
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public static void readExcelDataJesus(File excelFile) throws FileNotFoundException {
        MercadoLibreAPI ml = new MercadoLibreAPI();

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            String asin, mlcode, url;

            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {
                    asin = hssfRow.getCell(1).getStringCellValue();

                    mlcode = hssfRow.getCell(2).getStringCellValue();

                    System.out.println("asin=" + asin);
                    System.out.println("mlcode=" + mlcode);
                    String query = "update productos set mlcode=? where asin=?";
                    try (Connection conn = ConnectionPool.getInstance().getConnection();
                            PreparedStatement statement = conn.prepareStatement(query);) {
                        statement.setString(2, asin);
                        statement.setString(1, mlcode);
                        statement.executeUpdate();
                        statement.close();
                        conn.close();
                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
            //hssfWorkbook.write(excelNewOutputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
    }

    public static void readExcelDatanNEW(File excelFile) throws FileNotFoundException {
        MercadoLibreAPI ml = new MercadoLibreAPI();

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            String asin, mlcode, url, sku;
            MercadoLibreAPI mlAgent = new MercadoLibreAPI();

            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {
                    mlcode = hssfRow.getCell(0).getStringCellValue();

                    sku = hssfRow.getCell(1).getStringCellValue();
                    url = hssfRow.getCell(2).getStringCellValue();

                    asin = hssfRow.getCell(4).getStringCellValue();

                    System.out.println("sku=" + sku + "---asin=" + asin + "-----url=" + url);
                    /* String query = "insert into producto_inicial (asin,url,sku,procesado) values(?,?,?,0)";
                     try (Connection conn = ConnectionPool.getInstance().getConnection(); 
                     PreparedStatement statement = conn.prepareStatement(query);) {
                     statement.setString(1, asin);  
                     statement.setString(2, url);  
                     statement.setString(3, sku);  

                     statement.executeUpdate();
                     } catch (SQLException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                     }
                    
                     */
                    String token = mlAgent.getToken();
                    if (token != null) {
                        int res = -1;//mlAgent.updateProducto(token,mlcode,0,0,"paused");
                        if (res == 200) {
                            System.out.println("================= PRODUCTO MODIFICADO ML ========== " + mlcode);
                        } else {
                            System.out.println("================= PRODUCTO NO MODIFICADO ML ========== " + mlcode);

                        }
                    }
                }
            }
            //hssfWorkbook.write(excelNewOutputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
    }

    public static void readExcelVariantes(File excelFile) throws FileNotFoundException {
        MercadoLibreAPI ml = new MercadoLibreAPI();

        InputStream excelStream = null;
        try {
            excelStream = new FileInputStream(excelFile);
            XSSFWorkbook hssfWorkbook = new XSSFWorkbook(excelStream);
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
            XSSFRow hssfRow;
            XSSFCell cell;
            int rows = hssfSheet.getLastRowNum();
            int cols = 0;
            String asinparent, asin, titulo, titulopadre, descripcion, labels, valores;
            for (int r = 1; r < rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                if (hssfRow == null) {
                    break;
                } else {
                    asinparent = hssfRow.getCell(0).getStringCellValue();

                    asin = hssfRow.getCell(1).getStringCellValue();
                    titulopadre = hssfRow.getCell(2).getStringCellValue();

                    descripcion = hssfRow.getCell(3).getStringCellValue();
                    labels = hssfRow.getCell(4).getStringCellValue();

                    valores = hssfRow.getCell(5).getStringCellValue();

                    labels = labels.replaceAll("\"", "");
                    System.out.println(labels);
                    String labelV[] = labels.split(labels);
                    String valoresV[] = valores.split(valores);
                    System.out.println(valores);
                    String adicional = "";
                    for (int i = 0; i < labelV.length; i++) {
                        adicional = labelV[i] + " " + valoresV[i];
                    }
                    titulo = titulopadre + " ----- " + adicional;
                    System.out.println(titulo);
                    /* String query = "insert into producto_inicial (asin,url,sku,procesado) values(?,?,?,0)";
                     try (Connection conn = ConnectionPool.getInstance().getConnection(); 
                     PreparedStatement statement = conn.prepareStatement(query);) {
                     statement.setString(1, asin);  
                     statement.setString(2, url);  
                     statement.setString(3, sku);  

                     statement.executeUpdate();
                     } catch (SQLException e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                     }
                    
                     */
                }
            }
            //hssfWorkbook.write(excelNewOutputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
            try {
                excelStream.close();
            } catch (IOException ex) {
                System.out.println("Error in file processing after close it (Error al procesar el fichero después de cerrarlo): " + ex);
            }
        }
    }
}
