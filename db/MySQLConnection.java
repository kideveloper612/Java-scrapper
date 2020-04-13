/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.scrapper.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySQLConnection implements ConnectionDB {
	HikariConfig config = new HikariConfig("hikariMySQL.properties");
	HikariDataSource ds = new HikariDataSource(config);
	
	@Override
	public Connection getConnection() {
		Connection connection=null;
		try {
			connection = ds.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return connection;
	}



	}
