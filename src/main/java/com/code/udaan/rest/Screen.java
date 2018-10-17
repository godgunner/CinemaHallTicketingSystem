package com.code.udaan.rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/this")
public class Screen {

	@GetMapping("/get")
	public String getGet() {
		return "HelloWorld!";
	}
	
	
	//1. API to accept details of a movie screen.
	@PostMapping(value = "/screens", consumes="application/json")
	public String getData(@RequestBody Screens screens) {
		int n = screens.getSeatInfo().size();
		int max =0;
		for(Row row: screens.getSeatInfo().values()) {
			if(max<row.getNumberOfSeats())
				max = row.getNumberOfSeats();
		}
		
		String s = "";
		
		for(int i=0;i<max;i++) {
			s+=" r"+i+" int";
			if(i!=max-1)
				s+=",";
		}
		
		String name = screens.getName();
		s = "create table "+name+"( "+s+" , id varchar(11) );";
		String a = s;
	try {
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/demo?useSSL=false", "root", "root");
		Statement myStmt = myConn.createStatement();
		int myRs = myStmt.executeUpdate(s);
		for(String row:screens.getSeatInfo().keySet()) {
			s = "insert into "+name+" values (";
			int aisle[] =screens.getSeatInfo().get(row).getAisleSeats();
			for(int i=0;i<screens.getSeatInfo().get(row).getNumberOfSeats();i++) {
				boolean flag = false;
				for(int j =0;j<aisle.length;j++) {
					if(aisle[j]==i) {
						flag = true;
						break;
					}
				}
				if(flag)
					s+=" 10";
				else
					s+=" 0";
				if(i<screens.getSeatInfo().get(row).getNumberOfSeats()-1)
					s+=",";
			}
			for(int i = screens.getSeatInfo().get(row).getNumberOfSeats();i<max;i++) {
				s+=", -1 ";
			}
			s+=", \""+row+"\");";
			a=s;
			myRs = myStmt.executeUpdate(s);
		}
		
	}
	catch(Exception e) {
		e.printStackTrace();
	}
		
		return a;
	}
	
	//2. API to reserve tickets for given seats in a given screen
	@PostMapping(value = "/screens/{screen-name}/reserve", consumes="application/json")
	public String reserveSeats(@PathVariable("screen-name") String screenName, @RequestBody Seat2 screen) {
		String st="";
		Connection myConn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			myConn = DriverManager.getConnection("jdbc:mysql://localhost/demo?useSSL=false", "root", "root");
			Statement myStmt = myConn.createStatement();
			myConn.setAutoCommit(false);
			for(String s:screen.getSeats().keySet()) {
				st = "select * from "+screenName+" where id = \""+s+"\" ;";
				ResultSet res  = myStmt.executeQuery(st);
				res.next();
				ResultSetMetaData rsmd = res.getMetaData();
				int n = rsmd.getColumnCount();
				int seat[] = new int[n-1];
				for(int i=0;i<n-1;i++) 
					seat[i] = res.getInt(i+1);
				int arr[] = screen.getSeats().get(s);
				for(int i: arr) {
					if(seat[i]%10 == 1) {
						myConn.rollback();
						return "Your seat "+s+i+" is already booked! Please select some other seat";
					}
					myStmt.executeUpdate("update "+screenName+" set r"+i+" = "+(seat[i]+1)+" where id = \""+s+"\"");
				}
			}
			myConn.commit();
		}
		catch(Exception e) {
			try {
				myConn.rollback();
				myConn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			
			return "ERROR!";
		}
		try {
			myConn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Success!";
	}
	
	//3. API to get the available seats for a given screen
	@GetMapping(value="/screens/{screen-name}/seats" , params = "status")
	public Seat3 available(@PathVariable("screen-name") String screenName,@RequestParam("status") String status) {
		Seat3 seat = null;
		try {
			String s = "select * from "+screenName+" ;";
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/demo?useSSL=false", "root", "root");
			Statement myStmt = myConn.createStatement();
			ResultSet res = myStmt.executeQuery(s);
			res.next();
			ResultSetMetaData rsmd = res.getMetaData();
			int col = rsmd.getColumnCount();
			int row = res.last()?res.getRow():0;
			res.beforeFirst();
			String str[] = new String[row];
			seat = new Seat3();
			seat.seats = new HashMap<>();
			for(int i=0;i<row;i++) {
				res.next();
				ArrayList<Integer> arr = new ArrayList<>();
				for(int j=0;j<col-1;j++) {
					if(res.getInt(j+1)%10==0) {
						arr.add(j);
					}
				}
				seat.seats.put(res.getString(col), arr);
			}
		}
		catch(Exception e) {
			
		}
		return seat;
	}
	
	
	//4. API to get information of available tickets at a given position
	@GetMapping(value = "/screens/{screen-name}/seats" , params = {"numSeats", "choice"})
	public Available isAvailable(@PathVariable("screen-name") String screenName, @RequestParam("numSeats") int numSeats, @RequestParam("choice") String seatChoice) {
		Available available=null;
		try {
			String s="";
			String n="";
			for(int i=0;i<seatChoice.length();i++) {
				if(seatChoice.charAt(i)<='Z' && seatChoice.charAt(i)>='A')
					s+=seatChoice.charAt(i);
				else if(seatChoice.charAt(i)>='0' && seatChoice.charAt(i)<='9')
					n+=seatChoice.charAt(i);
			}
			int num = Integer.parseInt(n);
			String st = "select * from "+screenName+" where id = \""+s+"\" ;";
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection myConn = DriverManager.getConnection("jdbc:mysql://localhost/demo?useSSL=false", "root", "root");
			Statement myStmt = myConn.createStatement();
			ResultSet res = myStmt.executeQuery(st);
			res.next();
			ResultSetMetaData rsmd = res.getMetaData();
			available = new Available();
			available.availableSeats = new HashMap<>();
			int col = rsmd.getColumnCount();
			int row[] = new int[col-1];
			for(int i=0;i<col-1;i++)
				row[i] = res.getInt(i+1);
			ArrayList<Integer> arr = new ArrayList<>();
			for(int i=num;i>=0;i--) {
				if(row[i]%10==1)
					break;
				if((row[i]/10)%10==1) {
					arr.add(i);
					break;
				}
				arr.add(i);
				if(arr.size()==numSeats) {
					available.availableSeats.put(s, arr);
					return available;
				}
			}
			for(int i=num+1;row[i]!=-1 && i<col-1;i++) {
				if(row[i]%10==1)
					break;
				if((row[i]/10)%10==1) {
					arr.add(i);
					break;
				}
				arr.add(i);
				if(arr.size()==numSeats) {
					available.availableSeats.put(s, arr);
					return available;
				}
			}
			if(arr.size() == numSeats)
				available.availableSeats.put(s, arr);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return available;
	}
	
}
