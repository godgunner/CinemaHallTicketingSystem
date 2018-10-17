package com.code.udaan.rest;

import java.util.ArrayList;
import java.util.HashMap;



public class Screens {

	private String name;
	private HashMap<String, Row> seatInfo;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public HashMap<String, Row> getSeatInfo() {
		return seatInfo;
	}
	public void setSeatInfo(HashMap<String, Row> seatInfo) {
		this.seatInfo = seatInfo;
	}
}

class Row{
	private int numberOfSeats;
	private int aisleSeats[];
	public int getNumberOfSeats() {
		return numberOfSeats;
	}
	public void setNumberOfSeats(int numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}
	public int[] getAisleSeats() {
		return aisleSeats;
	}
	public void setAisleSeats(int[] aisleSeats) {
		this.aisleSeats = aisleSeats;
	}
	
}

class Seat2{
	HashMap<String, int[]> seats;

	public HashMap<String, int[]> getSeats() {
		return seats;
	}

	public void setSeats(HashMap<String, int[]> seats) {
		this.seats = seats;
	}
}


class Seat3{
	HashMap<String, ArrayList> seats;

	public HashMap<String, ArrayList> getSeats() {
		return seats;
	}

	public void setSeats(HashMap<String, ArrayList> seats) {
		this.seats = seats;
	}
}

class Available{
	HashMap<String, ArrayList> availableSeats;

	public HashMap<String, ArrayList> getAvailableSeats() {
		return availableSeats;
	}

	public void setAvailableSeats(HashMap<String, ArrayList> availableSeats) {
		this.availableSeats = availableSeats;
	}
}