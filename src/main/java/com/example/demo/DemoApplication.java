package com.example.demo;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import javax.swing.event.DocumentEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class DemoApplication {

	private static String DELIMITER=",";
	private static String PATH_FILE = "src/main/resources/weather_data.csv";

	private static String MONGODB_URI = "mongodb://localhost:27017";
	private static String DATABASE = "weather";
	private static String MONGODB_COLLECTION = "weather_data";

	public static void main(String[] args) throws IOException, CsvValidationException, ParseException {
		SpringApplication.run(DemoApplication.class, args);


		BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(PATH_FILE));


		List<String[]> dataList = new ArrayList();
		String line;

		while((line = bufferedReader.readLine()) != null){
			// convert line into columns
			String[] columns = line.split(DELIMITER);
			dataList.add(columns);
		}



		// Column names
		String[] weatherColumnNames = dataList.get(0);


		for(int i = 1; i<=dataList.size()-1; i++){

			// Convert date into format yyyy/MM/dd HH:mm:ss
			String date = convertStringToDate(dataList.get(i)[0]);
			// Overwrite date with the date in yyyy/MM/dd HH:mm:ss format
			dataList.get(i)[0] = date;

			// Check if field is empty before converting to fahrenheit
			if(!dataList.get(i)[11].isEmpty()) {
				double fahrenheit = convertCelsiusToFahrenheit(Integer.parseInt(dataList.get(i)[11]));
				dataList.get(i)[11] = String.valueOf(fahrenheit);
			}

			// Convert 0 to false and 1 to true for fog, hail, rain and snow
			boolean stateFog = stringToBoolean(dataList.get(i)[3]);
			dataList.get(i)[3] = String.valueOf(stateFog);
			boolean stateHail = stringToBoolean(dataList.get(i)[4]);
			dataList.get(i)[4] = String.valueOf(stateHail);
			boolean stateRain = stringToBoolean(dataList.get(i)[9]);
			dataList.get(i)[9] = String.valueOf(stateRain);
			boolean stateSnow = stringToBoolean(dataList.get(i)[10]);
			dataList.get(i)[10] = String.valueOf(stateSnow);

		}



		// Create new csv file with the processed data
		writeListToCSVFile(dataList);



		// Write data into the mongodb weather database
		// Collection weather_data
		insertWeatherDataToMongoDB(dataList);





	}



	public static String convertStringToDate(String date) throws ParseException {

		// Create date based on the old format yyyyMMdd-HH:mm
		String format = "yyyyMMdd-HH:mm";
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date1 = formatter.parse(date);

		// Convert date to the new format yyyy/MM/dd HH:mm:ss
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return formatter1.format(date1);
	}


	public static double convertCelsiusToFahrenheit(int temperaturInC){
		return temperaturInC*1.8+32;
	}


	public static Boolean stringToBoolean(String state){
		return !state.equals("0");
	}



	public static void writeListToCSVFile(List<String[]> weatherData) throws IOException {
		String filePath = "src/main/resources/weather_data_new.csv" ;
		// Write entries without double quotes
		CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath),
				CSVWriter.DEFAULT_SEPARATOR,
				CSVWriter.NO_ESCAPE_CHARACTER,
				CSVWriter.NO_QUOTE_CHARACTER,
				CSVWriter.DEFAULT_LINE_END);
		// Write all entries from the list to the weather_data_new.csv file
		csvWriter.writeAll(weatherData);

	}


	public static void insertWeatherDataToMongoDB(List<String[]> weatherData){


		MongoClient mongoClient = new MongoClient(new MongoClientURI(MONGODB_URI));
		// MongoDB database
		MongoDatabase database = mongoClient.getDatabase(DATABASE);

		// MongoDB collection the data are going to be writen into
		MongoCollection<Document> dbCollection = database.getCollection(MONGODB_COLLECTION);


		String[] weatherColumns = new String[]{"datetime_utc", "_conds", "_dewptm", "_fog", "_hail", "_heatindexm",
				  							   "_hum", "_precipm", "_pressurem", "_rain", "_snow", "_tempm", "_thunder",
											   "_tornado", "_vism", "_wdird", "_wdire", "_wgustm", "_wgustm", "_windchillm", "_wspdm"};



		for(int i=1; i<weatherData.size(); i++) {
				Document document = new Document();
				for(int j=0; j<weatherData.get(i).length; j++) {
					// Add append only the available data from the list
					document.append(weatherColumns[j], weatherData.get(i)[j]);
				}
			dbCollection.insertOne(document);
			{


			}

		}
}
}
