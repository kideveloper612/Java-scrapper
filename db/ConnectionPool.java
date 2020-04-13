/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.db;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {
	//private static final Logger LOG = LoggerFactory.getLogger(ConnectionPool.class);
	private static ConnectionPool instance = null;
	private static HikariDataSource ds = null;

	static {
		try {
	//		LOG.info("Initializing the connection pool ... ");
			instance = new ConnectionPool();
	//		LOG.info("Connection pool initialized successfully.");
		} catch (Exception e) {
			System.out.println("Exception when trying to initialize the connection pool " + e.getMessage());
			e.printStackTrace();
		}
	}

	private ConnectionPool() {
		HikariConfig config = new HikariConfig("hikariMySQL.properties");
                
                //config.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(30));
                //config.setValidationTimeout(TimeUnit.MINUTES.toMillis(1));
                config.setMaximumPoolSize(50);
                //config.setMinimumIdle(0);
                //config.setMaxLifetime(TimeUnit.MINUTES.toMillis(2)); // 120 seconds 
                //config.setIdleTimeout(TimeUnit.MINUTES.toMillis(1)); // minutes
                config.setConnectionTimeout(TimeUnit.MINUTES.toMillis(5)); 
                //config.setConnectionTestQuery("/* ping */ SELECT 1");
                
                //System.out.println("config = " + config.getMaximumPoolSize());
                
                ds = new HikariDataSource(config);
                //System.out.println("ds = " + ds);
                //System.exit(0);

		ds.setConnectionTimeout(60000);
		//ds.setIdleTimeout(500);

		//ds.setMinimumIdle(1000);
		//ds.setMaximumPoolSize(10000);

		//ds.setLeakDetectionThreshold(15000);

	}

	public static ConnectionPool getInstance() {
		return instance;
	}

	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
}