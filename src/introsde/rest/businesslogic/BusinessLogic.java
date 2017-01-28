package introsde.rest.businesslogic;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ejb.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.glassfish.jersey.client.ClientConfig;
import org.json.*;

import javax.xml.ws.Holder;

@Stateless
@LocalBean
@Path("/blogic")
public class BusinessLogic {

	@GET
	@Path("/login/{personId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Login(@PathParam("personId") int personId) throws IOException {

		// Read the personal info about the user {personId}
		// -----------------------------------------------------
		String ENDPOINT1 = "https://aqueous-thicket-19730.herokuapp.com/introsde/storage/getPersonInfo/" + personId;

		DefaultHttpClient client1 = new DefaultHttpClient();
		HttpGet request1 = new HttpGet(ENDPOINT1);
		HttpResponse response1 = client1.execute(request1);

		BufferedReader rd1 = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));
		StringBuffer result1 = new StringBuffer();
		String line1 = "";

		while ((line1 = rd1.readLine()) != null) {
			result1.append(line1);
		}

		JSONObject o1 = new JSONObject(result1.toString());

		if (response1.getStatusLine().getStatusCode() != 200) {
			System.out.println("Personal Info URL return nothing");
			return Response.status(204).build();
		}

		// ------------------------------------------------------------------------------------------------------

		// Read the motivational image url
		// ----------------------------------------------------------------------

		String ENDPOINT2 = "https://aqueous-thicket-19730.herokuapp.com/introsde/storage/getPicture/";
		DefaultHttpClient client2 = new DefaultHttpClient();
		HttpGet request2 = new HttpGet(ENDPOINT2);
		HttpResponse response2 = client2.execute(request2);

		BufferedReader rd2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
		StringBuffer result2 = new StringBuffer();
		String line2 = "";

		while ((line2 = rd2.readLine()) != null) {
			result2.append(line2);
		}

		JSONObject o2 = new JSONObject(result2.toString());

		if (response2.getStatusLine().getStatusCode() != 200) {
			System.out.println("Image URL return nothing");
			return Response.status(204).build();
		}
		// -----------------------------------------------------------------------------------------------------

		// id Person conversion in String
		String idStringa = String.valueOf(o1.getInt("idPerson"));

		// Creation of the Strings:
		String weight = "";
		String height = "";
		String wtime = "";
		String steps = "";
		String hrate = "";
		String sleep = "";

		// BAD WAY TO CHECK IF THERE IS A NULL INSIDE THE STRING (no other way
		// found)
		String t = o1.getJSONObject("currentHealth").toString();
		String t2 = "null";

		if (!t.toLowerCase().contains(t2.toLowerCase())) {
			// Let's prepare Measure
			JSONArray arr = o1.getJSONObject("currentHealth").getJSONArray("measure");
			for (int i = 0; i < arr.length(); i++) {
				String type = arr.getJSONObject(i).getString("type");
				if (type.equals("weight")) {
					weight = arr.getJSONObject(i).getString("value");
				} else if (type.equals("height")) {
					height = arr.getJSONObject(i).getString("value");
				} else if (type.equals("wtime")) {
					wtime = arr.getJSONObject(i).getString("value");
				} else if (type.equals("steps")) {
					steps = arr.getJSONObject(i).getString("value");
				} else if (type.equals("hrate")) {
					hrate = arr.getJSONObject(i).getString("value");
				} else if (type.equals("sleep")) {
					sleep = arr.getJSONObject(i).getString("value");
				}

			}
		}

		// Let's prepare Goals

		// Creation of the goal Strings:
		String weightg = "";
		String wtimeg = "";
		String stepsg = "";
		String hrateg = "";
		String sleepg = "";

		// BAD WAY TO CHECK IF THERE IS A NULL INSIDE THE STRING (no other way
		// found)
		String test = o1.getJSONObject("currentGoal").toString();
		String test2 = "null";

		if (!test.toLowerCase().contains(test2.toLowerCase())) {
			JSONArray arr2 = o1.getJSONObject("currentGoal").getJSONArray("goal");
			for (int i = 0; i < arr2.length(); i++) {

				String type = arr2.getJSONObject(i).getString("type");

				if (type.equals("weight")) {
					weightg = arr2.getJSONObject(i).getString("value");
				} else if (type.equals("wtime")) {
					wtimeg = arr2.getJSONObject(i).getString("value");
				} else if (type.equals("steps")) {
					stepsg = arr2.getJSONObject(i).getString("value");
				} else if (type.equals("hrate")) {
					hrateg = arr2.getJSONObject(i).getString("value");
				} else if (type.equals("sleep")) {
					sleepg = arr2.getJSONObject(i).getString("value");
				}

			}
		}

		// Prepare the xml--> to send back

		String textXml = "";
		textXml = "<personInfo>";
		textXml += "<idPerson>" + idStringa + "</idPerson>";
		textXml += "<name>" + o1.getString("name") + "</name>";
		textXml += "<lastname>" + o1.getString("lastname") + "</lastname>";
		textXml += "<email>" + o1.getString("email") + "</email>";
		textXml += "<birthdate>" + o1.getString("birthdate") + "</birthdate>";
		textXml += "<picture_url>" + o2.getString("picture_url") + "</picture_url>";

		textXml += "<measure>";
		textXml += "<weight>" + weight + "</weight>";
		textXml += "<height>" + height + "</height>";
		textXml += "<wtime>" + wtime + "</wtime>";
		textXml += "<steps>" + steps + "</steps>";
		textXml += "<hrate>" + hrate + "</hrate>";
		textXml += "<sleep>" + sleep + "</sleep>";
		textXml += "</measure>";

		textXml += "<goal>";
		textXml += "<weightg>" + weightg + "</weightg>";
		textXml += "<wtimeg>" + wtimeg + "</wtimeg>";
		textXml += "<stepsg>" + stepsg + "</stepsg>";
		textXml += "<hrateg>" + hrateg + "</hrateg>";
		textXml += "<sleepg>" + sleepg + "</sleepg>";
		textXml += "</goal>";

		textXml += "</personInfo>";

		JSONObject xmlJSONObj = XML.toJSONObject(textXml);

		String processCentricJson = xmlJSONObj.toString(4);

		System.out.println(processCentricJson);

		return Response.ok(processCentricJson).build();

	}

	@GET
	@Path("/recipe")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Recipe() throws IOException {

		// Read the Lunch Recipe data
		// ----------------------------------------------------------------------

		String ENDPOINT3 = "https://aqueous-thicket-19730.herokuapp.com/introsde/storage/getLunchRecipe";
		DefaultHttpClient client3 = new DefaultHttpClient();
		HttpGet request3 = new HttpGet(ENDPOINT3);
		HttpResponse response3 = client3.execute(request3);

		BufferedReader rd3 = new BufferedReader(new InputStreamReader(response3.getEntity().getContent()));
		StringBuffer result3 = new StringBuffer();
		String line3 = "";

		while ((line3 = rd3.readLine()) != null) {
			result3.append(line3);
		}

		JSONObject o3 = new JSONObject(result3.toString());

		if (response3.getStatusLine().getStatusCode() != 200) {
			System.out.println("Lunch Recipe URL return nothing");
			return Response.status(204).build();
		}
		// -----------------------------------------------------------------------------------------------------

		// Read the Dinner Recipe data
		// ----------------------------------------------------------------------

		String ENDPOINT4 = "https://aqueous-thicket-19730.herokuapp.com/introsde/storage/getDinnerRecipe";
		DefaultHttpClient client4 = new DefaultHttpClient();
		HttpGet request4 = new HttpGet(ENDPOINT4);
		HttpResponse response4 = client4.execute(request4);

		BufferedReader rd4 = new BufferedReader(new InputStreamReader(response4.getEntity().getContent()));
		StringBuffer result4 = new StringBuffer();
		String line4 = "";

		while ((line4 = rd4.readLine()) != null) {
			result4.append(line4);
		}

		JSONObject o4 = new JSONObject(result4.toString());

		if (response4.getStatusLine().getStatusCode() != 200) {
			System.out.println("Dinner Recipe URL return nothing");
			return Response.status(204).build();
		}
		// -----------------------------------------------------------------------------------------------------

		// Prepare the xml--> to send back

		String textXml = "";
		textXml = "<recipe>";
		textXml += "<lunchRecipe>";
		// textXml += "<ingredients>" + o3.getString("ingredients") +
		// "</ingredients>";
		textXml += "<image>" + o3.getString("image") + "</image>";
		textXml += "<label>" + o3.getString("label") + "</label>";
		textXml += "<url>" + o3.getString("url") + "</url>";
		textXml += "</lunchRecipe>";

		textXml += "<dinnerRecipe>";
		// textXml += "<ingredients>" + o4.getString("ingredients") +
		// "</ingredients>";
		textXml += "<image>" + o4.getString("image") + "</image>";
		textXml += "<label>" + o4.getString("label") + "</label>";
		textXml += "<url>" + o4.getString("url") + "</url>";
		textXml += "</dinnerRecipe>";
		textXml += "</recipe>";

		JSONObject xmlJSONObj = XML.toJSONObject(textXml);

		String processCentricJson = xmlJSONObj.toString(4);

		System.out.println(processCentricJson);

		return Response.ok(processCentricJson).build();

	}
	
	

}