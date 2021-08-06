package com.example.demo;

import com.opencsv.exceptions.CsvValidationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class DemoApplication {

	public static void main(String[] args) throws IOException, CsvValidationException, ParseException {
		SpringApplication.run(DemoApplication.class, args);

		String PATH_FILE = "src/main/resources/weather_data.csv";
		BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(PATH_FILE));
		String DELIMITER=",";

		List<String[]> list = new ArrayList();
		String line;

		while((line = bufferedReader.readLine()) != null){
			// convert line into columns
			String[] columns = line.split(DELIMITER);

			list.add(columns);
		}



		System.out.println("----------------------\n\n\n\n\n\n\n\n\n\n\n\n");

		// Column names
		System.out.println(Arrays.toString(list.get(0)));
		System.out.println(Arrays.toString(list.get(1)));


		//String date = convertStringToDate(list.get(1)[0]);
		//System.out.println(date);

		for(int i = 1; i<=list.size()-1; i++){
			String date = convertStringToDate(list.get(i)[0]);
			list.get(i)[0] = date;
			if(!list.get(i)[11].isEmpty()) {
				double fahrenheit = convertCelsiusToFahrenheit(Integer.parseInt(list.get(i)[11]));
				list.get(i)[11] = String.valueOf(fahrenheit);
			}


			boolean state = stringToBoolean(list.get(i)[3]);


			if(!list.get(i)[3].isEmpty()) {
				// Convert 0 to false for _fog
				if (list.get(i)[3].equals("0")) {
					list.get(i)[3] = "false";
				}

				if (list.get(i)[3].equals("1")) {
					list.get(i)[3] = "true";
				}

			}

			if(!list.get(i)[3].isEmpty()) {
				if (list.get(i)[4].equals("0")) {
					list.get(i)[4] = "false";
				}

				if (list.get(i)[4].equals("1")) {
					list.get(i)[4] = "true";
				}
			}

		}





		for(int i = 1; i<=list.size()-1; i++){
			System.out.println(Arrays.toString(list.get(i)));
		}





	}



	public static String convertStringToDate(String date) throws ParseException {

		String format = "yyyyMMdd-HH:mm";
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		Date date1 = formatter.parse(date);


		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String dateFormated =  formatter1.format(date1);
		return dateFormated;
	}


	public static double convertCelsiusToFahrenheit(int temperaturCelsius){
		double fahreheit =  temperaturCelsius*1.8+32;
		return fahreheit;
	}


	public static Boolean stringToBoolean(String state){
		return !state.equals("0");
	}




}
