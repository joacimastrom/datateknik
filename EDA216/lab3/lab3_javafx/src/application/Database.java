package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Database is a class that specifies the interface to the 
 * movie database. Uses JDBC and the MySQL Connector/J driver.
 */
public class Database {
    /** 
     * The database connection.
     */
    private Connection conn;
    private String currName;

        
    /**
     * Create the database interface object. Connection to the database
     * is performed later.
     */
    public Database() {
        conn = null;
    }
        
    /** 
     * Open a connection to the database, using the specified user name
     * and password.
     *
     * @param userName The user name.
     * @param password The user's password.
     * @return true if the connection succeeded, false if the supplied
     * user name and password were not recognized. Returns false also
     * if the JDBC driver isn't found.
     */
    public boolean openConnection(String userName, String password) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection 
                ("jdbc:mysql://puccini.cs.lth.se/" + userName,
                 userName, password);
        }
        catch (SQLException e) {
            System.err.println(e);
            e.printStackTrace();
            return false;
        }
        catch (ClassNotFoundException e) {
            System.err.println(e);
            e.printStackTrace();
            return false;
        }
        return true;
    }
        
    /**
     * Close the connection to the database.
     */
    public void closeConnection() {
        try {
            if (conn != null)
                conn.close();
        }
        catch (SQLException e) {
        	e.printStackTrace();
        }
        conn = null;
        
        System.err.println("Database connection closed.");
    }
        
    /**
     * Check if the connection to the database has been established
     *
     * @return true if the connection has been established
     */
    public boolean isConnected() {
        return conn != null;
    }
	
  	public Show getShowData(String mTitle, String mDate) {
  		
  		PreparedStatement stmt = null; 
		String query = "select id, theater_name, seats from performances natural join theaters where movie_name=? and theDate=?";
		Integer mFreeSeats = 0;
		String mVenue = "";
		Integer id = 0;
		
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, mTitle);
			stmt.setString(2, mDate);
			ResultSet rs = stmt.executeQuery();

			
			while (rs.next()) {
				id = rs.getInt("id");
				mFreeSeats = rs.getInt("seats");
				mVenue = rs.getString("theater_name");		
						}
			
			query = "select count(*) as count from reservations where performanceId =?";
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, id);
			rs = stmt.executeQuery();

			
			while (rs.next()) {
				mFreeSeats = mFreeSeats - rs.getInt("count");	
						}

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		
		
		return new Show(mTitle, mDate, mVenue, mFreeSeats);
	}
  	
  	public boolean login(String uname) {
        PreparedStatement stmt = null;
        currName = uname;
        
        String query = "select user_name from users where user_name=?";
        
        try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, currName);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				return true;
			}	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		return false;
  		
  	}

	public List<String> getMovies() {
		
		
		List<String> movies = new ArrayList<String>();
		
		PreparedStatement stmt = null; 
		String query = "select movie_name from movies";
		
		try {
			stmt = conn.prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {

				movies.add(rs.getString("movie_name"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return movies;
	}

	
public List<String> getDates(String m) {
		
		
		List<String> dates = new ArrayList<String>();
		
		PreparedStatement stmt = null; 
		String query = "select theDate from performances where movie_name=?";
		
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, m);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				dates.add(rs.getString("theDate"));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dates;
	}
	

	public boolean bookTicket(String mname, String date) {
		
		
		Integer pID = 0;
		PreparedStatement stmt = null; 
		String query = "insert into exclude(mutid) values(1)";

		
		try {
			stmt = conn.prepareStatement(query);
			stmt.executeUpdate();
			
			query = "select id from performances where movie_name = ? and theDate = ?" ;
			stmt = conn.prepareStatement(query);
			stmt.setString(1, mname);
			stmt.setString(2, date);		
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				pID = rs.getInt("id");
			}
			
			query = "insert into Reservations(performanceid, user_name) values(? , ?)";
			stmt = conn.prepareStatement(query);
			stmt.setInt(1, pID);
			stmt.setString(2, currName);

			stmt.executeUpdate();
			
			query = "delete from exclude where mutid=1";
			stmt = conn.prepareStatement(query);
			stmt.executeUpdate();

			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			query = "delete from exclude where mutid=1";
			try {
				stmt = conn.prepareStatement(query);
				stmt.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return false;
	}

	public boolean checkMutex() {
		Statement stmt = null;
		String query = "select * from exclude";
		
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
		
			if (rs.next()) {
				return true;	
				}			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		return false;
	}
	}
