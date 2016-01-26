package ch.aranea.climbapp;

import static spark.Spark.get;

import java.util.List;

import org.avaje.agentloader.AgentLoader;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.TxRunnable;

import ch.aranea.climbapp.model.Climber;
import spark.Spark;

public class App {
	
	private static EbeanServer server;

	public static void main(String[] args) {
		// ebean
		if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent",
				"debug=1;packages=ch.aranea.climbapp.model.**")) {
			System.out.println("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
		}

		// h2 database
		server = Ebean.getServer("h2");

		// hello world
		get("/hello", (req, res) -> "Hello World: " + server.getName());

		// climber list
		Spark.get("/climbers/list", (req, res) -> {
			List<Climber> climbers = Ebean.find(Climber.class).findList();
			return Ebean.json().toJson(climbers);
		});

		// climber add - should be post
		Spark.get("/climbers/:name", (req, res) -> {

			Ebean.execute(new TxRunnable() {
				public void run() {
					Climber c = new Climber();
					c.setName(req.params("name"));
					Ebean.save(c);
				}
			});

			return "ok";
		});
	}
}