package com.ann.nrf52840_bleconnection

import android.annotation.SuppressLint
import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class Database {
    private val dbName = "IoTDatabase"
    private val username = "CloudSAccd148c2"
    private val password = "Serveriot1"
    private val serverName = "iotserver1"

    @SuppressLint("AuthLeak")
    fun dbConn() : Connection? {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        var connection : Connection? = null
        val connectionUrl = "jdbc:jtds:sqlserver://iotserver1.database.windows.net:1433;encrypt=true;databaseName=$dbName;user=$username;password=$password;ssl=require"
        Log.i("URL","URL: $connectionUrl")

        try {
            Log.i("TRY", "Dentro try")
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance()
            connection = DriverManager.getConnection(connectionUrl)

            return connection

        }catch (e : SQLException) {
            Log.i("ERROR","Error: $e")
        }

        return null
    }

    fun getMinTemperatureValue() : String {

        val connection = dbConn()
        val statement : Statement = connection!!.createStatement()

        // Create and execute a SELECT SQL statement.
        val selectSql = "SELECT MIN(temperature) from sensor"
        val resultSet = statement.executeQuery(selectSql)

        // Print results from select statement
        while (resultSet.next()) {
            Log.i("MinT", resultSet.getString(1))
            return resultSet.getString(1)
        }

        return ""
    }

    fun getMinHumidityValue() : String {

        val connection = dbConn()
        val statement : Statement = connection!!.createStatement()

        // Create and execute a SELECT SQL statement.
        val selectSql = "SELECT MIN(humidity) from sensor"
        val resultSet = statement.executeQuery(selectSql)

        // Print results from select statement
        while (resultSet.next()) {
            Log.i("MinT", resultSet.getString(1))
            return resultSet.getString(1)
        }

        return ""
    }

    fun getMaxTemperatureValue() : String {

        val connection = dbConn()
        val statement : Statement = connection!!.createStatement()

        // Create and execute a SELECT SQL statement.
        val selectSql = "SELECT MAX(temperature) from sensor"
        val resultSet = statement.executeQuery(selectSql)

        // Print results from select statement
        while (resultSet.next()) {
            Log.i("MaxT", resultSet.getString(1))
            return resultSet.getString(1)
        }

        return ""
    }

    fun getMaxHumidityValue() : String {

        val connection = dbConn()
        val statement : Statement = connection!!.createStatement()

        // Create and execute a SELECT SQL statement.
        val selectSql = "SELECT MAX(humidity) from sensor"
        val resultSet = statement.executeQuery(selectSql)

        // Print results from select statement
        while (resultSet.next()) {
            Log.i("MaxT", resultSet.getString(1))
            return resultSet.getString(1)
        }

        return ""
    }

    fun getMeanTemperatureValue() : String {

        val connection = dbConn()
        val statement : Statement = connection!!.createStatement()

        // Create and execute a SELECT SQL statement.
        val selectSql = "SELECT ROUND(AVG(temperature), 2) from sensor"
        val resultSet = statement.executeQuery(selectSql)

        // Print results from select statement
        while (resultSet.next()) {
            Log.i("AvgT", resultSet.getString(1))
            return resultSet.getString(1)
        }

        return ""
    }

    fun getMeanHumidityValue() : String {

        val connection = dbConn()
        val statement : Statement = connection!!.createStatement()

        // Create and execute a SELECT SQL statement.
        val selectSql = "SELECT ROUND(AVG(humidity), 2) from sensor"
        val resultSet = statement.executeQuery(selectSql)

        // Print results from select statement
        while (resultSet.next()) {
            Log.i("AvgH", resultSet.getString(1))
            return resultSet.getString(1)
        }

        return ""
    }

    fun getVarianceTemperatureValue() : String {

        val connection = dbConn()
        val statement : Statement = connection!!.createStatement()

        // Create and execute a SELECT SQL statement.
        val selectSql = "SELECT ROUND(VAR(temperature), 2) from sensor"
        val resultSet = statement.executeQuery(selectSql)

        // Print results from select statement
        while (resultSet.next()) {
            Log.i("AvgT", resultSet.getString(1))
            return resultSet.getString(1)
        }

        return ""
    }

    fun getVarianceHumidityValue() : String {

        val connection = dbConn()
        val statement : Statement = connection!!.createStatement()

        // Create and execute a SELECT SQL statement.
        val selectSql = "SELECT ROUND(VAR(humidity), 2) from sensor"
        val resultSet = statement.executeQuery(selectSql)

        // Print results from select statement
        while (resultSet.next()) {
            Log.i("AvgH", resultSet.getString(1))
            return resultSet.getString(1)
        }

        return ""
    }
}
