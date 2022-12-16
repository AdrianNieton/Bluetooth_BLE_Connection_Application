package com.ann.nrf52840_bleconnection

import android.annotation.SuppressLint
import android.os.StrictMode
import android.util.Log
import java.sql.*

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

    fun insertData(newTemperatures: MutableList<Float>, newHumidity: MutableList<Float>) {
        val connection = dbConn()
        val insertStatement: PreparedStatement = (connection?.prepareStatement("INSERT INTO sensor (temperature, humidity) VALUES (?, ?);")
            ?: null) as PreparedStatement

        newTemperatures.zip(newHumidity).forEach { pair ->
            insertStatement.setFloat(1, pair.component1())
            insertStatement.setFloat(2, pair.component2())
            Log.i("Insert", pair.component1().toString())
            insertStatement.executeUpdate()
        }
    }

    fun getData(): ResultSet? {
        val connection = dbConn()
        val statement : Statement = connection!!.createStatement()

        // Create and execute a SELECT SQL statement.
        val selectSql = "SELECT temperature, humidity from sensor"
        val resultSet = statement.executeQuery(selectSql)

        return resultSet
    }
}
